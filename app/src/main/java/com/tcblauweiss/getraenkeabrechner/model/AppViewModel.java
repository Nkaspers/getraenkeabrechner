package com.tcblauweiss.getraenkeabrechner.model;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.tcblauweiss.getraenkeabrechner.database.AppRepository;

import java.util.List;

public class AppViewModel extends AndroidViewModel {
    private final AppRepository appRepository;
    private final LiveData<List<Entry>> entriesToday;
    private final LiveData<List<Item>> allItems;
    private final LiveData<List<Member>> allMembers;


    public AppViewModel(@NonNull Application application) {
        super(application);
        appRepository = new AppRepository(application);
        entriesToday = appRepository.getAllEntriesOfToday();
        allItems = appRepository.getAllItems();
        allMembers = appRepository.getAllMembers();
    }

    public LiveData<List<Entry>> getEntriesToday() {
        return entriesToday;
    }

    public LiveData<List<Entry>> getAllEntries() {
        return appRepository.getAllEntries();
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

    public void updateItem(long id, String name, double price){
        appRepository.updateItem(id, name, price);
    }

    public void deleteItems(Item... items) {
        appRepository.deleteItem(items);
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
        appRepository.storeSignatureSvgAsFile(signature, entryId);
    }

    public void storeSignature(Bitmap signature, Long entryId){
        appRepository.storeSignatureBitmapAsFile(signature, entryId);
    }

    public Drawable getSignature(Long entryId){
        return appRepository.loadSignatureFromFile(entryId);
    }

    public boolean deleteAllSignatures(){
        return appRepository.deleteAllSignatureFiles();
    }

    public int importMembersFromCsv(String path){
        return appRepository.importMembersFromCsv(path);
    }

    public String exportAllEntries(List<Entry> entries) {
        return appRepository.exportAllEntriesToCsv(entries);
    }
}
