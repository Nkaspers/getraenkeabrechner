package com.tcblauweiss.getraenkeabrechner.ui.entries;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.search.SearchBar;
import com.tcblauweiss.getraenkeabrechner.R;
import com.tcblauweiss.getraenkeabrechner.SettingsActivity;
import com.tcblauweiss.getraenkeabrechner.model.AppViewModel;


public class AllEntriesFragment extends Fragment {
    SearchBar searchBar;
    SettingsActivity parentActivity;
    DrawerLayout drawer;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_entries, container, false);

        searchBar = view.findViewById(R.id.searchbar);

        parentActivity = (SettingsActivity) requireActivity();
        drawer = parentActivity.getDrawer();

        setupSearchBar();

        Bundle args = getArguments();
        assert args != null;
        boolean flag = args.getBoolean("deleteAllEntries");
        Log.d("AllEntriesFragment", "onCreateView->deleteAllEntries: " + flag);

        return view;
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
        searchBar.setHint(R.string.search_Entries_label);
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