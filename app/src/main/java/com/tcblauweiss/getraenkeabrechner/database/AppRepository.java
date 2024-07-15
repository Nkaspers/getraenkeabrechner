package com.tcblauweiss.getraenkeabrechner.database;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.tcblauweiss.getraenkeabrechner.model.Entry;
import com.tcblauweiss.getraenkeabrechner.model.Item;
import com.tcblauweiss.getraenkeabrechner.model.Member;
import com.tcblauweiss.getraenkeabrechner.util.CsvImporter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;



public class AppRepository {
    private final EntryDao entryDao;
    private final LiveData<List<Entry>> allEntries;

    private final ItemDao itemDao;
    private final LiveData<List<Item>> allItems;

    private final MemberDao memberDao;
    private final LiveData<List<Member>> allMembers;

    private final Application application;

    public AppRepository(Application application) {
        this.application = application;

        AppDatabase db = AppDatabase.getDatabase(application);
        entryDao = db.entryDao();
        allEntries = entryDao.getAll();

        itemDao = db.itemDao();
        allItems = itemDao.getAll();

        memberDao = db.memberDao();
        allMembers = memberDao.getAll();
    }

    public LiveData<List<Entry>> getAllEntries() {
        return allEntries;
    }

    public LiveData<List<Entry>> getAllEntriesOfToday() {
        LocalDateTime todayMidnight = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
        return entryDao.getAllEntriesAfterDate(todayMidnight.toEpochSecond(ZoneId.systemDefault().getRules().getOffset(LocalDateTime.now())) * 1000);
    }

    public List<Long> insertEntries(Entry... entries) {
        List<Long> entryIds;
        Future<List<Long>> idsFuture = AppDatabase.databaseWriteExecutor.submit(new Callable<List<Long>>() {
            @Override
            public List<Long> call() throws Exception {
                return entryDao.insertAll(entries);
            }
        });
        try {
            entryIds = idsFuture.get();
        } catch (Exception e) {
            Log.d("AppRepository", e.toString());
            entryIds = null;
        }
        return entryIds;
    }


    public void deleteAllEntries() {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                entryDao.deleteAll();
            }
        });
        //TODO: delete signature files
    }

    public LiveData<List<Item>> getAllItems() {
        return allItems;
    }

    public void insertItems(Item... items) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                itemDao.insertAll(items);
            }
        });
    }

    public void updateItem(long id, String name, double price) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                itemDao.update(id, name, price);
            }
        });
    }

    public void deleteItem(Item... items) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                itemDao.delete(items);
            }
        });
    }

    public void deleteAllItems() {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                itemDao.deleteAll();
            }
        });
    }

    public LiveData<List<Member>> getAllMembers() {
        return allMembers;
    }

    public void insertMembers(Member... members) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                memberDao.insertAll(members);
            }
        });
    }

    public void deleteMember(Member... members) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                memberDao.delete(members);
            }
        });
    }

    public void deleteAllMembers() {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                memberDao.deleteAll();
            }
        });
    }

    //Stores signature in /sign/ folder using the entryId as Filename
    public void storeSignatureSvgAsFile(String signatureSvg, Long entryId) {
        File rootDir = application.getFilesDir();
        File signDir = new File(rootDir, "sign");
        if (!signDir.exists()) {
            if (!signDir.mkdir()) {
                Log.d("AppRepository", "Failed to create directory: sign");
            }
        }

        String filename = entryId.toString() + ".svg";
        File signatureFile = new File(signDir, filename);

        try {
            FileOutputStream stream = new FileOutputStream(signatureFile);
            stream.write(signatureSvg.getBytes());
        } catch (IOException e) {
            Log.d("AppRepository", "Failed to create or write file");
            throw new RuntimeException(e);
        }
        Log.d("AppRepository", "Signature file created");
    }

    //Stores signature in /sign/ folder using the entryId as Filename
    public void storeSignatureBitmapAsFile(Bitmap signatureBitmap, Long entryId) {
        File rootDir = application.getFilesDir();
        File signDir = new File(rootDir, "sign");
        if (!signDir.exists()) {
            if (!signDir.mkdir()) {
                Log.d("AppRepository", "Failed to create directory: sign");
            }
        }

        String filename = entryId.toString() + ".bmp";
        File signatureFile = new File(signDir, filename);

        boolean success;
        try {
            FileOutputStream stream = new FileOutputStream(signatureFile);
             success = signatureBitmap.compress(Bitmap.CompressFormat.PNG, 30, stream);
        } catch (IOException e) {
            Log.d("AppRepository", "Failed to create or write file");
            throw new RuntimeException(e);
        }
        if (success) {
            Log.d("AppRepository", "Signature file created");
        } else {
            Log.d("AppRepository", "Failed to compress signature bitmap ");
        }
    }


    public Drawable loadSignatureFromFile(Long entryId) {
        String filepath = application.getFilesDir() + "/sign/" + entryId.toString() + ".bmp";

        return new BitmapDrawable(application.getResources(),BitmapFactory.decodeFile( filepath ));
    }

    public boolean deleteAllSignatureFiles() {
        File signDir = new File(application.getFilesDir(), "sign");
        Log.d("AppRepository", "Deleting signature files");
        boolean success = true;
        if (signDir.listFiles() != null) {
            for (File child : signDir.listFiles()) {
                if (!child.delete()) success = false;
            }
        }
        return success;
    }

    public int importMembersFromCsv(String path){
        List<Member> members = CsvImporter.importMembersFromCsv(path);
        insertMembers(members.toArray(new Member[0]));
        return members.size();
    }

    public String exportAllEntriesToCsv(List<Entry> entries){
        Date date = new Date( System.currentTimeMillis() );
        LocalDateTime dateTime = date.toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalDateTime();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
        String timeFormatted = dateTime.format(timeFormatter);
        String filename = "/getraenkeliste_" + timeFormatted + ".csv";

        File rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String path = rootDir.getAbsolutePath() + filename;

        CsvImporter.exportEntriesToCsv(entries, path);
        return path;
    }
}
