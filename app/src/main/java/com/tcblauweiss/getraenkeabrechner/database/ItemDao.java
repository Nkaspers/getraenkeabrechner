package com.tcblauweiss.getraenkeabrechner.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.tcblauweiss.getraenkeabrechner.model.Item;

import java.util.List;

@Dao
public interface ItemDao {
    @Query("SELECT * FROM item")
    LiveData<List<Item>> getAll();

    @Query("SELECT * FROM item WHERE item_name LIKE :name")
    List<Item> findByName(String name);

    @Insert
    void insertAll(Item... items);

    @Query("UPDATE item SET item_name = :name, price = :price WHERE id = :id")
    void update(long id, String name, double price);

    @Delete
    void delete(Item... items);

    @Query("DELETE FROM item")
    void deleteAll();
}
