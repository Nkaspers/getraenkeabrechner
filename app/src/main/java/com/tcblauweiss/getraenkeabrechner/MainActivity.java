package com.tcblauweiss.getraenkeabrechner;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
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
import com.tcblauweiss.getraenkeabrechner.util.StringFormatter;

import se.warting.signatureview.views.SignaturePad;
import se.warting.signatureview.views.SignedListener;

import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;


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
    private LastEntriesAdapter lastEntriesAdapter;

    private ItemSelectionAdapter itemSelectionAdapter;
    private Receipt receipt;
    private TextView receiptTotalTextView;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo biometricPromptInfo;
    private Executor executor;

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
        receiptTotalTextView = findViewById(R.id.text_receipt_total);

        receipt = new Receipt();

        setupBiometricPrompt();
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
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //Authentifizierungsprozess starten
            biometricPrompt.authenticate(biometricPromptInfo);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Boolean submitEntryAction() {

        if(!areInputFieldsValid()){
            Log.d("MainActivity", "no valid user input");
        }else{
            Entry[] entries = createEntries();
            List<Long> entryIds = appViewModel.insertEntries(entries);
            if (entryIds == null){
                Log.d("MainActivity", "EntryIds are null");
                Toast.makeText(getApplicationContext(), R.string.submit_failure_toast, Toast.LENGTH_LONG).show();
                return false;
            }

            String signatureSvg = signaturePad.getSignatureSvg();
            for(Long entryId : entryIds){
                appViewModel.storeSignature(signatureSvg, entryId);
            }

            lastEntriesRecyclerView.post(() -> lastEntriesRecyclerView.smoothScrollToPosition(0));
            resetInputElements();
            Toast.makeText(getApplicationContext(), R.string.submit_success_toast, Toast.LENGTH_LONG).show();
        }
        return true;
    }
    private void resetEntryAction() {
        resetInputElements();
    }
    private Entry[] createEntries(){
        String[] memberName = memberNameInputField.getText().toString().split(" ");
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
        receipt.clearData();
        receiptAdapter.updateData(receipt.getItemsAndCount());
        itemSelectionAdapter.refresh();
        receiptTotalTextView.setText(StringFormatter.formatToCurrencyString(0));
    }

    private void setupBiometricPrompt() {
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(), R.string.auth_error_toast, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), R.string.auth_success_toast, Toast.LENGTH_SHORT).show();

                //SettingsActivity starten
                Intent intent_launch_settings = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent_launch_settings);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), R.string.auth_failed_toast, Toast.LENGTH_SHORT).show();
            }
        });

        biometricPromptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getResources().getString(R.string.auth_prompt_title))
                .setSubtitle("")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build();
    }
    private void setupLastEntriesView() {
        lastEntriesRecyclerView.setHasFixedSize(true);
        lastEntriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        lastEntriesAdapter = new LastEntriesAdapter();
        appViewModel.getAllEntries().observe(this, new Observer<List<Entry>>() {
            @Override
            public void onChanged(List<Entry> entries) {

                lastEntriesAdapter.addEntryToTop(entries);
                lastEntriesRecyclerView.scrollToPosition(0);
            }
        });
        lastEntriesRecyclerView.setAdapter(lastEntriesAdapter);
    }

    private void setupItemSelectionView() {
        itemSelectionRecycleView.setLayoutManager(new GridLayoutManager(this,3));

        receiptRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        receiptAdapter = new ReceiptAdapter(receipt.getItemsAndCount());
        receiptRecyclerView.setAdapter(receiptAdapter);

        itemSelectionAdapter = new ItemSelectionAdapter(receipt, receiptAdapter);
        itemSelectionRecycleView.setAdapter(itemSelectionAdapter);
        appViewModel.getAllItems().observe(this, new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                itemSelectionAdapter.submitList(items);
            }
        });
        itemSelectionAdapter.setReceiptChangedListener(new ItemSelectionAdapter.ReceiptChangedListener() {
            @Override
            public void onReceiptChanged(Receipt receipt) {
                receiptTotalTextView.setText(receipt.getTotalString());
                Log.d(null, receipt.getTotalString());
            }
        });
    }

    private void setupMemberNameInputField(){
        LiveData<List<Member>> allMembers = appViewModel.getAllMembers();
        List<String> allMembersStr = new ArrayList<>();
        allMembers.observe(this, new Observer<List<Member>>() {
            @Override
            public void onChanged(List<Member> members) {
                    for(Member member: members){
                        String memberStr = member.getFirstName() + " " + member.getLastName();
                        allMembersStr.add(memberStr);
                }
            }
        });
        ArrayAdapter<String> memberInputFieldAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_dropdown_menu_item,  allMembersStr);
        memberNameInputField.setThreshold(1);
        memberNameInputField.setAdapter(memberInputFieldAdapter);
        //show error, if input string is not in Memberlist
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
        //clear focus on enter (done) button action
        memberNameInputField.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if(actionId== EditorInfo.IME_ACTION_DONE){
                memberNameInputField.clearFocus();
            }
            return false;
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
