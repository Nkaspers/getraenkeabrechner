package com.tcblauweiss.getraenkeabrechner;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tcblauweiss.getraenkeabrechner.databinding.ActivityMainBinding;
import com.tcblauweiss.getraenkeabrechner.model.Entry;
import com.tcblauweiss.getraenkeabrechner.model.Item;
import com.tcblauweiss.getraenkeabrechner.ui.mainactivity.itemselection.ItemSelectionAdapter;
import com.tcblauweiss.getraenkeabrechner.ui.mainactivity.lastentries.LastEntriesAdapter;

import android.view.Menu;
import android.view.MenuItem;

import java.time.LocalDateTime;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private Item item1 = new Item("Bier (0,5l)", Float.valueOf((float)1.5));
    private Item item2 = new Item("Wasser (1l)", Float.valueOf((float)1));
    private Entry entry1 = new Entry(LocalDateTime.now(),"Musterman", "Max", item1, 2,(float)3.0);
    private Entry entry2= new Entry(LocalDateTime.now(),"Meier", "Hans", item1, 1,(float)1.5);
    private Entry entry3 =  new Entry(LocalDateTime.now(),"Müller", "Peter", item2, 1,(float)1.0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        setupLastEntriesView();
        setupItemSelectionView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent_launch_settings = new Intent(this, SettingsActivity.class);
            startActivity(intent_launch_settings);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupLastEntriesView() {
        LastEntriesAdapter lastEntriesAdapter;

        RecyclerView lastEntriesRecyclerView = findViewById(R.id.rec_view_last_entries);
        lastEntriesRecyclerView.setHasFixedSize(true);
        lastEntriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        // some sample data for testing purposes
        ArrayList<Entry> dataSet = new ArrayList<>();
        dataSet.add(entry1);
        dataSet.add(entry2);
        dataSet.add(entry3);
        dataSet.add(entry2);
        dataSet.add(entry3);
        dataSet.add(entry1);
        dataSet.add(entry3);
        dataSet.add(entry3);
        dataSet.add(entry3);

        lastEntriesAdapter = new LastEntriesAdapter(dataSet);
        lastEntriesRecyclerView.setAdapter(lastEntriesAdapter);
    }

    private void setupItemSelectionView() {
        ItemSelectionAdapter itemSelectionAdapter;

        RecyclerView itemSelectionRecycleView = findViewById(R.id.rec_view_itemselection);
        itemSelectionRecycleView.setLayoutManager(new GridLayoutManager(this,3));

        ArrayList<Item> itemList = new ArrayList<>();
        itemList.add(item1);
        itemList.add(item2);
        itemList.add(item1);
        itemList.add(item2);
        itemSelectionAdapter = new ItemSelectionAdapter(itemList);
        itemSelectionRecycleView.setAdapter(itemSelectionAdapter);
    }

}