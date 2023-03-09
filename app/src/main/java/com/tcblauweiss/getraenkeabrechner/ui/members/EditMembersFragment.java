package com.tcblauweiss.getraenkeabrechner.ui.members;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.search.SearchBar;
import com.tcblauweiss.getraenkeabrechner.R;

public class EditMembersFragment extends Fragment {
    FloatingActionButton addMemberFab;
    AlertDialog addMemberDialog;

    SearchBar searchBar;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_members, container, false);
        searchBar = view.findViewById(R.id.searchbar_edit_members);
        searchBar.setNavigationIcon(R.drawable.ic_menu);
        ((AppCompatActivity) getActivity()).setSupportActionBar(searchBar);

        addMemberFab = view.findViewById(R.id.fab_edit_members_fragment);
        addMemberFab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                userInputDialog();
            }
        });
        return view;
    }

    private void userInputDialog() {
        addMemberDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.add_member_dialog_title)
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
