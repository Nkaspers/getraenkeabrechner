package com.tcblauweiss.getraenkeabrechner;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.tcblauweiss.getraenkeabrechner.databinding.ActivityMainBinding;
import com.tcblauweiss.getraenkeabrechner.model.Entry;
import com.tcblauweiss.getraenkeabrechner.model.Item;
import com.tcblauweiss.getraenkeabrechner.model.Receipt;
import com.tcblauweiss.getraenkeabrechner.ui.mainactivity.itemselection.ItemSelectionAdapter;
import com.tcblauweiss.getraenkeabrechner.ui.mainactivity.itemselection.ReceiptAdapter;
import com.tcblauweiss.getraenkeabrechner.ui.mainactivity.lastentries.LastEntriesAdapter;

import se.warting.signatureview.views.SignaturePad;
import se.warting.signatureview.views.SignedListener;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private RecyclerView lastEntriesRecyclerView;
    private RecyclerView itemSelectionRecycleView;
    private Button resetEntryBtn;
    private Button submitEntryBtn;
    private MaterialAutoCompleteTextView memberNameInputField;
    private TextInputLayout memberNameInputLayout;
    private SignaturePad signaturePad;
    private Item item1 = new Item("Bier (0,5l)", Float.valueOf((float)1.5));
    private Item item2 = new Item("Wasser (1l)", Float.valueOf((float)1));
    private Item item3 = new Item("Weizen (0,5l)", Float.valueOf((float)1.5));
    private Item item4 = new Item("Softdrink (1l)", Float.valueOf((float)1.3));

    private Entry entry1 = new Entry(LocalDateTime.now(),"Musterman", "Max", item1, 2,(float)3.0);
    private Entry entry2= new Entry(LocalDateTime.now(),"Meier", "Hans", item1, 1,(float)1.5);
    private Entry entry3 =  new Entry(LocalDateTime.now(),"Müller", "Peter", item2, 1,(float)1.0);
    private RecyclerView receiptRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        lastEntriesRecyclerView = findViewById(R.id.rec_view_last_entries);
        itemSelectionRecycleView = findViewById(R.id.rec_view_itemselection);
        receiptRecyclerView = findViewById(R.id.rec_view_receipt);
        memberNameInputField = findViewById(R.id.text_input_member_name);
        memberNameInputLayout = findViewById(R.id.layout_input_member_name);
        signaturePad = findViewById(R.id.signature_pad);
        resetEntryBtn = findViewById(R.id.btn_reset_entry);
        submitEntryBtn = findViewById(R.id.btn_submit_entry);

        setupLastEntriesView();
        setupItemSelectionView();
        setupMemberNameInputField();
        setupSignaturePad();
        resetEntryBtn.setOnClickListener(view -> resetEntryAction());
        submitEntryBtn.setOnClickListener(view -> submitEntryAction());
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

    private Boolean submitEntryAction() {
        if(memberNameInputLayout.getError() != null){
            Toast.makeText(getApplicationContext(), R.string.invalid_member_toast, Toast.LENGTH_LONG).show();
        }else{
            resetInputElements();
            //TODO: Submit Entry Action
            Toast.makeText(getApplicationContext(), R.string.submit_success_toast, Toast.LENGTH_LONG).show();
        }
        return true;
    }

    private void resetEntryAction() {
        resetInputElements();
    }

    private void resetInputElements(){
        signaturePad.clear();
        memberNameInputField.setText("");
        memberNameInputField.clearFocus();
        memberNameInputLayout.setError(null);
    }

    private void setupLastEntriesView() {
        LastEntriesAdapter lastEntriesAdapter;
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
        Receipt receipt = new Receipt();
        itemSelectionRecycleView.setLayoutManager(new GridLayoutManager(this,3));

        ArrayList<Item> itemList = new ArrayList<>();
        itemList.add((item1));
        itemList.add((item2));
        itemList.add((item3));
        itemList.add((item4));
        itemList.add((item1));
        itemList.add((item2));
        itemList.add((item3));
        itemList.add((item4));

        receiptRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ReceiptAdapter receiptAdapter = new ReceiptAdapter(receipt.getItemsAndCount());
        receiptRecyclerView.setAdapter(receiptAdapter);

        itemSelectionAdapter = new ItemSelectionAdapter(itemList, receipt,receiptAdapter);
        itemSelectionRecycleView.setAdapter(itemSelectionAdapter);
    }

    private void setupMemberNameInputField(){
        List<String> members = new ArrayList<>();
        members.add("Ninian Kaspers");
        members.add("Leon Schmidt");
        members.add("Max Mustermann");
        members.add("Peter Müller");
        members.add("Hans Meier");

        ArrayAdapter<String> memberInputFieldAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_dropdown_menu_item, members);
        memberNameInputField.setThreshold(1);
        memberNameInputField.setAdapter(memberInputFieldAdapter);
        memberNameInputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!members.contains(memberNameInputField.getText().toString())){
                    memberNameInputLayout.setError(getString(R.string.name_input_field_error_label));
                }else{
                    memberNameInputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        memberNameInputField.setOnClickListener(view -> memberNameInputField.dismissDropDown());
    }

    private void setupSignaturePad(){
        signaturePad.setOnSignedListener(new SignedListener() {
            @Override
            public void onStartSigning() {
                Log.d("SignedListener", "OnStartSigning");
            }
            @Override
            public void onSigning() {
                Log.d("SignedListener", "OnSigning");
            }
            @Override
            public void onSigned() {
                Log.d("SignedListener", "OnSigned");
            }
            @Override
            public void onClear() {
                Log.d("SignedListener", "OnClear");
            }
        });
    }



}