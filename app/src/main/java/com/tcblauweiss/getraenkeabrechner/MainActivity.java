package com.tcblauweiss.getraenkeabrechner;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.tcblauweiss.getraenkeabrechner.databinding.ActivityMainBinding;
import com.tcblauweiss.getraenkeabrechner.model.Entry;
import com.tcblauweiss.getraenkeabrechner.model.Item;
import com.tcblauweiss.getraenkeabrechner.ui.mainactivity.itemselection.ItemSelectionAdapter;
import com.tcblauweiss.getraenkeabrechner.ui.mainactivity.lastentries.LastEntriesAdapter;

import se.warting.signatureview.views.SignaturePad;
import se.warting.signatureview.views.SignedListener;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private Button resetEntryBtn;
    private Button submitEntryBtn;
    private MaterialAutoCompleteTextView memberNameInputField;
    private TextInputLayout memberNameInputLayout;
    private SignaturePad signaturePad;
    private Item item1 = new Item("Bier (0,5l)", Float.valueOf((float)1.5));
    private Item item2 = new Item("Wasser (1l)", Float.valueOf((float)1));
    private Entry entry1 = new Entry(LocalDateTime.now(),"Musterman", "Max", item1, 2,(float)3.0);
    private Entry entry2= new Entry(LocalDateTime.now(),"Meier", "Hans", item1, 1,(float)1.5);
    private Entry entry3 =  new Entry(LocalDateTime.now(),"Müller", "Peter", item2, 1,(float)1.0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize Ui Elements
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        setupLastEntriesView();
        setupItemSelectionView();
        signaturePad = findViewById(R.id.signature_pad);
        resetEntryBtn = findViewById(R.id.btn_reset_entry);
        submitEntryBtn = findViewById(R.id.btn_submit_entry);
        memberNameInputField = findViewById(R.id.text_input_member_name);
        memberNameInputLayout = findViewById(R.id.layout_input_member_name);

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
                    memberNameInputLayout.setError(getString(R.string.name_imput_field_error_label));
                }else{
                    memberNameInputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        memberNameInputField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                memberNameInputField.dismissDropDown();
            }
        });

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

        resetEntryBtn.setOnClickListener(view -> resetEntryAction());

        submitEntryBtn.setOnClickListener(view -> submitEntryAction());
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