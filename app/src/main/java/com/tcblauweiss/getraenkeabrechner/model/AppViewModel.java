package com.tcblauweiss.getraenkeabrechner.model;

import android.app.Application;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.tcblauweiss.getraenkeabrechner.database.AppRepository;

import java.util.List;

public class AppViewModel extends AndroidViewModel {
    private AppRepository appRepository;
    private final LiveData<List<Entry>> allEntries;
    private final LiveData<List<Item>> allItems;
    private final LiveData<List<Member>> allMembers;


    public AppViewModel(@NonNull Application application) {
        super(application);
        appRepository = new AppRepository(application);
        allEntries = appRepository.getAllEntries();
        allItems = appRepository.getAllItems();
        allMembers = appRepository.getAllMembers();
    }

    public LiveData<List<Entry>> getAllEntries() {
        return allEntries;
    }

    public List<Long> insertEntries(Entry... entries){
        return appRepository.insertEntries(entries);
    }

    public void deleteAllEntries(){
        appRepository.deleteAllEntries();
    }

    public LiveData<List<Item>> getAllItems() {
        return allItems;
    }

    public void insertItems(Item... items) {
        appRepository.insertItems(items);
    }

    public void deleteItem(Item item) {
        appRepository.deleteItem(item);
    }

    public void deleteAllItems() {
        appRepository.deleteAllItems();
    }

    public LiveData<List<Member>> getAllMembers() {
        return allMembers;
    }

    public void insertMembers(Member... members) {
        appRepository.insertMembers(members);
    }

    public void deleteMembers(Member... members) {
        appRepository.deleteMember(members);
    }

    public void deleteAllMembers() {
        appRepository.deleteAllMembers();
    }

    public void storeSignature(String signature, Long entryId){
        appRepository.storeSignatureAsFile(signature, entryId);
    }

    public Drawable getSignature(Long entryId){
        return appRepository.loadSignatureFromFile(entryId);
    }
}
