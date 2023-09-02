package com.tcblauweiss.getraenkeabrechner.ui.entries;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.tcblauweiss.getraenkeabrechner.R;
import com.tcblauweiss.getraenkeabrechner.SettingsActivity;
import com.tcblauweiss.getraenkeabrechner.model.AppViewModel;
import com.tcblauweiss.getraenkeabrechner.model.Entry;
import com.tcblauweiss.getraenkeabrechner.model.Member;
import com.tcblauweiss.getraenkeabrechner.ui.mainactivity.lastentries.LastEntriesAdapter;
import com.tcblauweiss.getraenkeabrechner.ui.members.AllMembersViewAdapter;

import java.util.List;


public class AllEntriesFragment extends Fragment {
    private SearchBar searchBar;
    private SearchView searchView;
    private RecyclerView searchRecycleView;
    private SettingsActivity parentActivity;
    private DrawerLayout drawer;
    private AppViewModel appViewModel;
    private RecyclerView lastEntriesRecyclerView;
    private LiveData<List<Member>> allMembers;
    private LastEntriesAdapter lastEntriesAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_entries, container, false);

        searchBar = view.findViewById(R.id.searchbar);

        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        allMembers = appViewModel.getAllMembers();
        parentActivity = (SettingsActivity) requireActivity();
        drawer = parentActivity.getDrawer();

        lastEntriesRecyclerView = view.findViewById(R.id.recyclerview_all_entries_fragment);
        searchView = view.findViewById(R.id.searchview_member);
        searchRecycleView = view.findViewById(R.id.recyclerview_search);

        setupSearchBar();

        Bundle args = getArguments();
        assert args != null;
        boolean flag = args.getBoolean("deleteAllEntries");
        Log.d("AllEntriesFragment", "onCreateView->deleteAllEntries: " + flag);
        setupLastEntriesView();
        setupSearchView();
        return view;
    }

    private void setupLastEntriesView() {
        lastEntriesRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        lastEntriesAdapter = new LastEntriesAdapter();

        appViewModel.getAllEntries().observe(requireActivity(), new Observer<List<Entry>>() {
            @Override
            public void onChanged(List<Entry> entries) {
                lastEntriesAdapter.addEntryToTop(entries);
            }
        });
        lastEntriesRecyclerView.setAdapter(lastEntriesAdapter);

        lastEntriesAdapter.setEntryClickedListener(new LastEntriesAdapter.EntryClickedListener() {
            @Override
            public void onEntryClicked(Entry entry) {
                AlertDialog signatureDialog = createSignatureDialog(entry);
                signatureDialog.show();
            }

            @Override
            public void onEntryLongClicked(Entry entry) {

            }
        });
    }

    private AlertDialog createSignatureDialog(Entry entry) {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_signature, null);

        ImageView signatureView = view.findViewById(R.id.signature_dialog_signature_view);
        Drawable signature = appViewModel.getSignature(entry.getId());

        signatureView.setImageDrawable(signature);
        return new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                .setIcon(R.drawable.ic_edit)
                .setTitle(entry.getName())
                .setView(view)
                .create();
    }

    private AlertDialog createDeleteAllEntriesDialog() {

        return new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                .setIcon(R.drawable.ic_delete_all)
                .setTitle(R.string.delete_all_entries_alert_title)
                .setMessage(R.string.delete_all_entries_alert_message)
                .setPositiveButton(R.string.delete_all_entries_dialog_confirm_button_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        parentActivity.getViewModel().deleteAllEntries();
                        if(!parentActivity.getViewModel().deleteAllSignatures()){
                            Log.d("AllEntriesFragment", "Failed to delete signatures");
                        }
                    }
                })
                .setNegativeButton(R.string.delete_all_entries_dialog_cancel_button_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).create();
    }

    public void setupSearchBar(){
        searchBar.setHint(R.string.search_members_label);
        searchBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
    }

    public void setupSearchView() {
        AllMembersViewAdapter membersViewAdapter= new AllMembersViewAdapter();
        allMembers.observe(requireActivity(), new Observer<List<Member>>() {
            @Override
            public void onChanged(List<Member> members) {
                membersViewAdapter.submitList(members);
            }
        });
        searchRecycleView.setAdapter(membersViewAdapter);
        searchRecycleView.setLayoutManager(new LinearLayoutManager(requireContext()));

        searchView.setupWithSearchBar(searchBar);
        membersViewAdapter.setMemberClickedListener(new AllMembersViewAdapter.MemberClickedListener() {
            @Override
            public void onMemberClicked(Member member) {
                lastEntriesAdapter.filterByMember(member);
                searchBar.setText(member.getName());
                searchView.hide();
            }

            @Override
            public void onMemberLongClicked(Member member) {}
        });
        searchView
                .getEditText()
                .setOnEditorActionListener((v, actionId, event) -> {
                    searchBar.setText(searchView.getText());
                    searchView.hide();
                    return false;
                });

        searchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                membersViewAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        drawer.close();
        Bundle args = getArguments();
        if(args != null && args.getBoolean("deleteAllEntries", false)){
            AlertDialog deleteAllEntriesDialog = createDeleteAllEntriesDialog();
            deleteAllEntriesDialog.show();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle args = getArguments();
        assert args != null;
        args.putBoolean("deleteAllEntries", false);
    }

}
