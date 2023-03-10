package com.tcblauweiss.getraenkeabrechner.ui.members;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.search.SearchBar;
import com.tcblauweiss.getraenkeabrechner.R;
import com.tcblauweiss.getraenkeabrechner.model.Member;

import java.util.ArrayList;
import java.util.List;

public class EditMembersFragment extends Fragment {
    FloatingActionButton addMemberFab;
    AlertDialog addMemberDialog;
    AppCompatActivity parentActivity;
    SearchBar searchBar;
    RecyclerView membersRecyclerView;
    List<Member> memberDataSet;
    private AllMembersViewAdapter membersRecyclerViewAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_members, container, false);
        parentActivity = (AppCompatActivity) requireActivity();

        searchBar = view.findViewById(R.id.searchbar);
        membersRecyclerView = view.findViewById(R.id.recyclerview_edit_members_fragment);
        addMemberFab = view.findViewById(R.id.fab_edit_members_fragment);
        //Init Searchbar
        searchBar.setNavigationIcon(R.drawable.ic_menu);
        searchBar.setHint(R.string.search_members_label);
        parentActivity.setSupportActionBar(searchBar);
        //Init RecyclerView
        memberDataSet = new ArrayList<>();
        memberDataSet.add(new Member("Max", "Mustermann"));
        membersRecyclerViewAdapter = new AllMembersViewAdapter(memberDataSet);
        membersRecyclerView.setAdapter(membersRecyclerViewAdapter);
        membersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        addMemberFab.setOnClickListener(view1 -> {
            if(addMemberDialog == null) setupNewMemberDialog();
            addMemberDialog.show();
        });
        return view;
    }

    private void setupNewMemberDialog() {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_member, null);
        TextInputEditText memberFirstNameInput = view.findViewById(R.id.text_input_new_member_firstname);
        TextInputEditText memberLastNameInput = view.findViewById(R.id.text_input_new_member_lastname);

        addMemberDialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.add_member_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.add_member_dialog_confirm_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO: Mitglied hinzuf??gen
                        Editable firstName = memberFirstNameInput.getEditableText();
                        Editable lastName = memberLastNameInput.getEditableText();
                        if(firstName != null && lastName != null){
                            memberDataSet.add(new Member(firstName.toString(), lastName.toString()));
                            //Inserts member at bottom. Change later when list is sorted Alphabetically
                            membersRecyclerViewAdapter.notifyItemInserted(memberDataSet.size()-1);
                            Log.d("addMemberDialog", "add Member: " + firstName + " " + lastName);
                        }else{
                            Log.d("addMemberDialog", "member name is null");
                        }
                    }
                })
                .setNegativeButton(R.string.add_member_dialog_cancel_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addMemberDialog.hide();
                    }
                })
                .create();
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
