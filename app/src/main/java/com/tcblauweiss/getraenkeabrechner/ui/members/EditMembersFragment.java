package com.tcblauweiss.getraenkeabrechner.ui.members;

import androidx.appcompat.app.ActionBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.search.SearchBar;
import com.tcblauweiss.getraenkeabrechner.R;

public class EditMembersFragment extends Fragment {
    FloatingActionButton addMemberFab;
    AlertDialog addMemberDialog;
    AppCompatActivity parentActivity;
    SearchBar searchBar;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_members, container, false);
        parentActivity = (AppCompatActivity) requireActivity();
        searchBar = view.findViewById(R.id.searchbar);
        searchBar.setNavigationIcon(R.drawable.ic_menu);
        searchBar.setHint(R.string.search_members_label);
        parentActivity.setSupportActionBar(searchBar);

        addMemberFab = view.findViewById(R.id.fab_edit_members_fragment);
        addMemberFab.setOnClickListener(view1 -> newMemberDialog());
        return view;
    }

    private void newMemberDialog() {
        TextInputEditText memberFirstNameInput = new TextInputEditText(getContext());
        TextInputEditText memberSurnameInput = new TextInputEditText(getContext());
        memberSurnameInput.setHint(R.string.member_surname_input_label);
        memberFirstNameInput.setHint(R.string.member_first_name_input_label);

        addMemberDialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.add_member_dialog_title)
                .setView(R.layout.dialog_new_member)
                .setPositiveButton(R.string.add_member_dialog_confirm_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO: Mitglied hinzufügen
                    }
                })
                .setNegativeButton(R.string.add_member_dialog_cancel_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addMemberDialog.hide();
                    }
                })
                .create();
        addMemberDialog.show();
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
