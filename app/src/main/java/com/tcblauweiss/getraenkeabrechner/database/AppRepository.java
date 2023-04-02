package com.tcblauweiss.getraenkeabrechner.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.tcblauweiss.getraenkeabrechner.model.Entry;
import com.tcblauweiss.getraenkeabrechner.model.Item;
import com.tcblauweiss.getraenkeabrechner.model.Member;

import java.util.List;

public class AppRepository {
    private EntryDao entryDao;
    private LiveData<List<Entry>> allEntries;

    private ItemDao itemDao;
    private LiveData<List<Item>> allItems;

    private MemberDao memberDao;
    private LiveData<List<Member>> allMembers;

    public AppRepository(Application application) {
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

    public void insertEntries(Entry... entries) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                entryDao.insertAll(entries);
            }
        });
    }


    public void deleteAllEntries() {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                entryDao.deleteAll();
            }
        });
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

    public void deleteItem(Item item) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                itemDao.delete(item);
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
}
