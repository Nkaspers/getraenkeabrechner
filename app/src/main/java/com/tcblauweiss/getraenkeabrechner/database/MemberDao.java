package com.tcblauweiss.getraenkeabrechner.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.tcblauweiss.getraenkeabrechner.model.Member;

import java.util.List;

@Dao
public interface MemberDao {
    @Query("SELECT * FROM member ORDER BY last_name, first_name asc")
    LiveData<List<Member>> getAll();

    @Query("SELECT * FROM member WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    Member findByName(String first, String last);

    @Insert
    void insertAll(Member... members);

    @Delete
    void delete(Member... members);

    @Query("DELETE FROM member")
    public void deleteAll();
}
