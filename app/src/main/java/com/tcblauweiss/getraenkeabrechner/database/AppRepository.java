package com.tcblauweiss.getraenkeabrechner.database;

import android.app.Application;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.pixplicity.sharp.Sharp;
import com.tcblauweiss.getraenkeabrechner.model.Entry;
import com.tcblauweiss.getraenkeabrechner.model.Item;
import com.tcblauweiss.getraenkeabrechner.model.Member;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class AppRepository {
    private EntryDao entryDao;
    private LiveData<List<Entry>> allEntries;

    private ItemDao itemDao;
    private LiveData<List<Item>> allItems;

    private MemberDao memberDao;
    private LiveData<List<Member>> allMembers;

    private Application application;

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
        }catch (Exception e){
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
    public void storeSignatureAsFile(String signatureSvg, Long entryId){
        File rootDir = application.getFilesDir();
        File signDir = new File(rootDir, "sign");
        if(!signDir.exists()){
            if(!signDir.mkdir()){
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

    public Drawable loadSignatureFromFile(Long entryId){
        File signDir = new File(application.getFilesDir(), "sign");
        String filename = entryId.toString() + ".svg";

        File signatureFile = new File(signDir, filename);
        if(!signatureFile.exists()){
            Log.d("AppRepository", "Failed to open signature File");
            return null;
        }

        return Sharp.loadFile(signatureFile).getDrawable();
    }

    public boolean deleteAllSignatureFiles(){
        File signDir = new File(application.getFilesDir(), "sign");
        Log.d("AppRepository", "Deleting signature files");
        boolean success = true;
        if (signDir.listFiles() != null) {
            for (File child : signDir.listFiles()) {
                if(!child.delete()) success = false;
            }
        }
        return success;
    }
}
