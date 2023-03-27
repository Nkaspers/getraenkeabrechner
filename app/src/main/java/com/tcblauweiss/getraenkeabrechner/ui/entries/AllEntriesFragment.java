package com.tcblauweiss.getraenkeabrechner.ui.entries;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
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
import com.tcblauweiss.getraenkeabrechner.ui.mainactivity.lastentries.LastEntriesAdapter;

import java.util.List;


public class AllEntriesFragment extends Fragment {
    private SearchBar searchBar;
    private SearchView searchView;
    private SettingsActivity parentActivity;
    private DrawerLayout drawer;
    private AppViewModel appViewModel;

    private RecyclerView lastEntriesRecyclerView;
    private RecyclerView searchRecyclerView;
    private LastEntriesAdapter lastEntriesAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_entries, container, false);

        searchBar = view.findViewById(R.id.searchbar);
        searchView = view.findViewById(R.id.searchview_all_entries);
        lastEntriesRecyclerView = view.findViewById(R.id.recyclerview_all_entries_fragment);
        searchRecyclerView = view.findViewById(R.id.recycler_search_all_entries);

        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);

        parentActivity = (SettingsActivity) requireActivity();
        drawer = parentActivity.getDrawer();

        setupSearchBar();
        setupLastEntriesView();
        setupSearchView();

        Bundle args = getArguments();
        assert args != null;
        boolean flag = args.getBoolean("deleteAllEntries");
        Log.d("AllEntriesFragment", "onCreateView->deleteAllEntries: " + flag);
        return view;
    }

    private void setupSearchView() {
        searchRecyclerView.setAdapter(lastEntriesAdapter);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        searchView.setupWithSearchBar(searchBar);
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
                lastEntriesAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setupLastEntriesView() {
        lastEntriesRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        lastEntriesAdapter = new LastEntriesAdapter();
        lastEntriesRecyclerView.setAdapter(lastEntriesAdapter);

        appViewModel.getAllEntries().observe(getViewLifecycleOwner(), new Observer<List<Entry>>() {
            @Override
            public void onChanged(List<Entry> entries) {
                lastEntriesAdapter.submitList(entries);
            }
        });

    }

    private AlertDialog createDeleteAllEntriesDialog() {

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_all_entries_alert_title)
                .setMessage(R.string.delete_all_entries_alert_message)
                .setPositiveButton(R.string.delete_all_entries_dialog_confirm_button_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        parentActivity.getViewModel().deleteAllEntries();
                    }
                })
                .setNegativeButton(R.string.delete_all_entries_dialog_cancel_button_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).create();
    }

    public void setupSearchBar(){
        searchBar.setHint(R.string.search_entries_label);
        searchBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
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
