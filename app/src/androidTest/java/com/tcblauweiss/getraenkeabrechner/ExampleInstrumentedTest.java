package com.tcblauweiss.getraenkeabrechner;

import android.app.Application;
import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.tcblauweiss.getraenkeabrechner.database.AppRepository;
import com.tcblauweiss.getraenkeabrechner.model.Entry;
import com.tcblauweiss.getraenkeabrechner.model.Item;
import com.tcblauweiss.getraenkeabrechner.model.Member;

import java.util.ArrayList;
import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.tcblauweiss.getraenkeabrechner", appContext.getPackageName());
    }

    public void populateDatabase(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        List<Member> memberList = new ArrayList<>();
        memberList.add(new Member("Test", "User1"));
        memberList.add(new Member("Test", "User2"));
        memberList.add(new Member("Test", "User3"));
        memberList.add(new Member("Test", "User4"));
        memberList.add(new Member("Test", "User5"));
        memberList.add(new Member("Test", "User6"));
        memberList.add(new Member("Test", "User7"));
        memberList.add(new Member("Test", "User8"));
        memberList.add(new Member("Test", "User9"));
        memberList.add(new Member("Test", "User10"));

        List<Item> itemList = new ArrayList<>();
        itemList.add(new Item("Item 1", 1));
        itemList.add(new Item("Item 2", 1.50));
        itemList.add(new Item("Item 3", 2));
        itemList.add(new Item("Item 4", 2.50));
        itemList.add(new Item("Item 5", 3));
        itemList.add(new Item("Item 6", 3.50));

        List<Entry> entryList = new ArrayList<>();
        entryList.add( new Entry(1698948263, "Test", "User1", "Item 1", 1, 1, 1));
        entryList.add( new Entry(1698948263, "Test", "User2", "Item 2", 1.50, 1, 1.50));
        entryList.add( new Entry(1698948263, "Test", "User3", "Item 3", 2, 1, 2));
        entryList.add( new Entry(1698948263, "Test", "User4", "Item 4", 2.50, 1, 2.50));
        entryList.add( new Entry(1698948263, "Test", "User5", "Item 5", 3, 1, 3));
        entryList.add( new Entry(1698948263, "Test", "User6", "Item 6", 3.50, 1, 3.50));
        entryList.add( new Entry(1698948263, "Test", "User7", "Item 1", 1, 1, 1));
        entryList.add( new Entry(1698948263, "Test", "User8", "Item 2", 1.50, 1, 1.50));

        AppRepository appRepository = new AppRepository((Application) appContext.getApplicationContext());
        for(Member member: memberList){
            appRepository.insertMembers(member);
        }
        for(Item item: itemList){
            appRepository.insertItems(item);
        }
        for(Entry entry: entryList){
            appRepository.insertEntries(entry);
        }
    }
}