package com.tcblauweiss.getraenkeabrechner;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.tcblauweiss.getraenkeabrechner.databinding.ActivityMainBinding;
import com.tcblauweiss.getraenkeabrechner.model.AppViewModel;
import com.tcblauweiss.getraenkeabrechner.model.Entry;
import com.tcblauweiss.getraenkeabrechner.model.Item;
import com.tcblauweiss.getraenkeabrechner.model.ItemWrapper;
import com.tcblauweiss.getraenkeabrechner.model.Member;
import com.tcblauweiss.getraenkeabrechner.model.Receipt;
import com.tcblauweiss.getraenkeabrechner.ui.mainactivity.itemselection.ItemSelectionAdapter;
import com.tcblauweiss.getraenkeabrechner.ui.mainactivity.itemselection.ReceiptAdapter;
import com.tcblauweiss.getraenkeabrechner.ui.mainactivity.lastentries.LastEntriesAdapter;

import se.warting.signatureview.views.SignaturePad;
import se.warting.signatureview.views.SignedListener;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


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
    private RecyclerView receiptRecyclerView;
    private AppViewModel appViewModel;
    private ReceiptAdapter receiptAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);

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

        if(!areInputFieldsValid()){
            Log.d("MainActivity", "no valid user input");
        }else{
            String signatureSvg = signaturePad.getSignatureSvg();
            //TODO: store svg or filepath in database
            Entry[] entries = createEntries();
            appViewModel.insertEntries(entries);
            resetInputElements();
            Toast.makeText(getApplicationContext(), R.string.submit_success_toast, Toast.LENGTH_LONG).show();
        }
        return true;
    }
    private void resetEntryAction() {
        resetInputElements();
    }
    private Entry[] createEntries(){
        String[] memberName = memberNameInputField.getText().toString().split("");
        List<ItemWrapper> items = receiptAdapter.getReceiptItemList();
        Entry[] entries = new Entry[items.size()];
        int i=0;
        for(ItemWrapper itemWrapper: items){
            Entry entry = new Entry(
                    System.currentTimeMillis(), memberName[1], memberName[0],
                    itemWrapper.getItem().getName(), itemWrapper.getItem().getPrice(),
                    itemWrapper.getCount(), itemWrapper.getTotal());
            entries[i++] = entry;
        }
        return entries;
    }

    private Boolean areInputFieldsValid(){
        if(memberNameInputLayout.getError() != null || memberNameInputField.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), R.string.invalid_member_toast, Toast.LENGTH_LONG).show();
            return false;
        }
        if(signaturePad.isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.no_signature_toast, Toast.LENGTH_LONG).show();
            return false;
        }
        //TODO: check if Items are selected
        return true;
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

        lastEntriesAdapter = new LastEntriesAdapter();

        appViewModel.getAllEntries().observe(this, new Observer<List<Entry>>() {
            @Override
            public void onChanged(List<Entry> entries) {
                lastEntriesAdapter.submitList(entries);
            }
        });
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
        receiptAdapter = new ReceiptAdapter(receipt.getItemsAndCount());
        receiptRecyclerView.setAdapter(receiptAdapter);

        itemSelectionAdapter = new ItemSelectionAdapter(itemList, receipt, receiptAdapter);
        itemSelectionRecycleView.setAdapter(itemSelectionAdapter);
    }

    private void setupMemberNameInputField(){
        LiveData<List<Member>> allMembers = appViewModel.getAllMembers();
        List<String> allMembersStr = new ArrayList<>();
        allMembers.observe(this, new Observer<List<Member>>() {
            @Override
            public void onChanged(List<Member> members) {
                    for(Member member: members){
                        String memberStr = member.firstName + " " + member.lastName;
                        allMembersStr.add(memberStr);
                }
            }
        });
        ArrayAdapter<String> memberInputFieldAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_dropdown_menu_item,  allMembersStr);
        memberNameInputField.setThreshold(1);
        memberNameInputField.setAdapter(memberInputFieldAdapter);

        memberNameInputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!allMembersStr.contains(memberNameInputField.getText().toString())){
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