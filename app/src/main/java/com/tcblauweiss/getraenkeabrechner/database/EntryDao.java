package com.tcblauweiss.getraenkeabrechner.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.tcblauweiss.getraenkeabrechner.model.Entry;
import com.tcblauweiss.getraenkeabrechner.model.Member;

import java.util.List;

@Dao
public interface EntryDao {
    @Query("SELECT * FROM entry ORDER BY date_created DESC")
    LiveData<List<Entry>> getAll();

    @Query("SELECT * FROM entry WHERE first_name LIKE :first AND " +
            "last_name LIKE :last")
    List<Entry> findByName(String first, String last);

    @Insert
    List<Long> insertAll(Entry... entries);

    @Query("DELETE FROM entry")
    public void deleteAll();
}
