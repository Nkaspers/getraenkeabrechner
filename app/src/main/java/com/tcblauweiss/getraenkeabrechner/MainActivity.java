package com.tcblauweiss.getraenkeabrechner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;

import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;


public class MainActivity extends AppCompatActivity {

    private RecyclerView lastEntriesRecyclerView;
    private RecyclerView itemSelectionRecycleView;
    private Button resetEntryBtn;
    private Button submitEntryBtn;
    private MaterialAutoCompleteTextView memberNameInputField;
    private TextInputLayout memberNameInputLayout;
    private SignaturePad signaturePad;
    private RecyclerView receiptRecyclerView;
    private AppViewModel appViewModel;
    private ReceiptAdapter receiptAdapter;
    private LastEntriesAdapter lastEntriesAdapter;

    private ItemSelectionAdapter itemSelectionAdapter;
    private Receipt receipt;
    private TextView receiptTotalTextView;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo biometricPromptInfo;
    private ArrayAdapter<Member> memberInputFieldAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(this, MainActivity.class));

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
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
            //Prüfe, ob Authentifizierung verfügbar
            BiometricManager biometricManager = BiometricManager.from(this);
            switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
                case BiometricManager.BIOMETRIC_SUCCESS:
                    Log.d("MainActivity", "App can authenticate using biometrics.");
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                    Log.e("MainActivity", "No biometric features available on this device.");
                    break;
                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    Log.e("MainActivity", "Biometric features are currently unavailable.");
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                    final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                    enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL);
                    startActivity(enrollIntent);
                    break;
                default:
                    Log.e("MainActivity", "Biometric features are unavailable.");
                    break;
            }

            //Authentifizierungsprozess starten
            biometricPrompt.authenticate(biometricPromptInfo);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Boolean submitEntryAction() {

        if (!areInputFieldsValid()) {
            Log.d("MainActivity", "no valid user input");
            return false;
        }

        Entry[] entries = createEntries();
        AlertDialog dialog = createEntryConfirmationDialog(entries);
        dialog.show();

        return true;
    }
    private void resetEntryAction() {
        resetInputElements();
    }

    private Entry[] createEntries() {
        String[] memberName = memberNameInputField.getText().toString().split(", ");
        List<ItemWrapper> items = receiptAdapter.getReceiptItemList();
        Entry[] entries = new Entry[items.size()];

        int i = 0;
        for (ItemWrapper itemWrapper : items) {
            Entry entry = new Entry(
                    new Date(), memberName[0], memberName[1],
                    itemWrapper.getItem().getName(), itemWrapper.getItem().getPrice(),
                    itemWrapper.getCount(), itemWrapper.getTotal());
            entries[i++] = entry;
        }

        return entries;
    }

    private Boolean areInputFieldsValid() {
        if (memberNameInputLayout.getError() != null || memberNameInputField.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.invalid_member_toast, Toast.LENGTH_LONG).show();
            return false;
        }
        if (signaturePad.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.no_signature_toast, Toast.LENGTH_LONG).show();
            return false;
        }

        if (receiptAdapter.getItemCount() == 0) {
            Toast.makeText(getApplicationContext(), R.string.no_items_selected_toast, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private AlertDialog createEntryConfirmationDialog(Entry[] entries) {
        double total = 0;
        for (Entry entry : entries) {
            total += entry.getTotalPrice();
        }

        String message = String.format("Bist du <b>%s %s</b> und möchtest Getränke im Wert von <b>%.2f€</b> buchen?", entries[0].getFirstName(), entries[0].getLastName(), total);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered);
        builder.setTitle(R.string.dialog_title_entry_confirmation);
        builder.setIcon(R.drawable.ic_done);
        builder.setMessage(Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY));
        builder.setPositiveButton(R.string.submit_entry_btn_label, (dialogInterface, i) -> {

            List <Long> entryIds = appViewModel.insertEntries(entries);
            if (entryIds == null) {
                Log.d("MainActivity", "EntryIds are null");
                Toast.makeText(getApplicationContext(), R.string.submit_failure_toast, Toast.LENGTH_LONG).show();
            }

            Bitmap signatureBitmap = signaturePad.getSignatureBitmap();
            for (Long entryId : entryIds) {
                appViewModel.storeSignature(signatureBitmap, entryId);
            }

            lastEntriesRecyclerView.post(() -> lastEntriesRecyclerView.smoothScrollToPosition(0));
            resetInputElements();
            Toast.makeText(getApplicationContext(), R.string.submit_success_toast, Toast.LENGTH_LONG).show();
        });

        builder.setNegativeButton(R.string.cancel_action_button_label, (dialogInterface, i) -> {

        });

        return builder.create();
    }



    private void resetInputElements() {
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
        Executor executor = ContextCompat.getMainExecutor(this);
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
        appViewModel.getEntriesToday().observe(this, new Observer<List<Entry>>() {
            @Override
            public void onChanged(List<Entry> entries) {

                lastEntriesAdapter.submitList(entries);
                lastEntriesRecyclerView.scrollToPosition(0);
            }
        });
        lastEntriesRecyclerView.setAdapter(lastEntriesAdapter);
    }

    private void setupItemSelectionView() {
        itemSelectionRecycleView.setLayoutManager(new GridLayoutManager(this, 3){
            @Override
            public boolean checkLayoutParams( RecyclerView.LayoutParams lp) {
                lp.width = (int) (getWidth() * 0.30);
                lp.height = (int) (getHeight() * 0.43);
                return true;
            }
        });

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

    private void setupMemberNameInputField() {
        LiveData<List<Member>> allMembers = appViewModel.getAllMembers();
        memberInputFieldAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_dropdown_menu_item);

        allMembers.observe(this, new Observer<List<Member>>() {
            @Override
            public void onChanged(List<Member> members) {
                    memberInputFieldAdapter.clear();
                    memberInputFieldAdapter.addAll(members);
                    memberInputFieldAdapter.notifyDataSetChanged();
            }
        });

        memberNameInputField.setThreshold(1);
        memberNameInputField.setAdapter(memberInputFieldAdapter);
        //show error, if input string is not in Memberlist
        memberNameInputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<String> allMembersStr = Objects.requireNonNull(allMembers.getValue()).stream().map(Member::getName).collect(Collectors.toList());
                if (!allMembersStr.contains(memberNameInputField.getText().toString())) {
                    memberNameInputLayout.setError(getString(R.string.name_input_field_error_label));
                } else {
                    memberNameInputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0 && editable.charAt(0) == ' ') {
                    editable.delete(0, 1);
                }
            }

        });

        //clear focus on enter (done) button action
        memberNameInputField.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                memberNameInputField.clearFocus();
            }
            return false;
        });

        memberNameInputField.setOnClickListener(view -> memberNameInputField.dismissDropDown());

        memberNameInputField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                memberNameInputField.clearFocus();
            }
        });
    }

    private void setupSignaturePad() {
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
