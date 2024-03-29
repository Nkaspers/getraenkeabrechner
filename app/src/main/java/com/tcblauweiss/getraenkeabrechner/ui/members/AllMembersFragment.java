package com.tcblauweiss.getraenkeabrechner.ui.members;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.search.SearchView;
import com.google.android.material.textfield.TextInputEditText;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.search.SearchBar;
import com.tcblauweiss.getraenkeabrechner.R;
import com.tcblauweiss.getraenkeabrechner.SettingsActivity;
import com.tcblauweiss.getraenkeabrechner.model.AppViewModel;
import com.tcblauweiss.getraenkeabrechner.model.Member;

import java.util.ArrayList;
import java.util.List;

public class AllMembersFragment extends Fragment {
    private FloatingActionButton addMemberFab;
    private AlertDialog addMemberDialog;
    private SettingsActivity parentActivity;
    private SearchBar searchBar;
    private RecyclerView membersRecyclerView;
    private AllMembersViewAdapter membersRecyclerViewAdapter;
    private AppViewModel appViewModel;
    private LiveData<List<Member>> allMembers;
    private List<Member> selectedMembers;
    private SearchView searchView;
    private RecyclerView searchRecyclerView;
    private DrawerLayout drawer;
    private ActionMode.Callback itemSelectedActionCallback;
    private ActionMode itemSelectedActionMode;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_members, container, false);

        parentActivity = (SettingsActivity) requireActivity();


        Bundle args = getArguments();
        assert args != null;
        boolean importMembersFlag = args.getBoolean("importMembers");
        Log.d("EditMembersFragment", "onCreate->importMembers: " + importMembersFlag);
        boolean deleteAllMembersFlag = args.getBoolean("deleteAllMembers");
        Log.d("EditMembersFragment", "onCreate->deleteAllMembers: " + deleteAllMembersFlag);

        searchBar = view.findViewById(R.id.searchbar);
        searchView = view.findViewById(R.id.searchview_member);
        membersRecyclerView = view.findViewById(R.id.recyclerview_edit_members_fragment);
        searchRecyclerView = view.findViewById(R.id.recyclerview_search);
        addMemberFab = view.findViewById(R.id.fab_edit_members_fragment);

        drawer = parentActivity.getDrawer();
        setupSearchBar();
        setupAllMembersView();
        setupSearchView();
        setupActionMode();

        addMemberFab.setOnClickListener(view1 -> {
            AlertDialog newMemberDialog = createNewMemberDialog();
            newMemberDialog.show();

        });
        return view;
    }

    private void setupActionMode() {
        itemSelectedActionCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                parentActivity.getMenuInflater().inflate(R.menu.action_bar_member_selected, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.action_delete_selection){
                    Member[] members = selectedMembers.toArray(new Member[0]);
                    appViewModel.deleteMembers(members);
                    actionMode.finish();
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                Log.d("AllMemberFragment", "destroy actionMode");
                selectedMembers.clear();
                membersRecyclerViewAdapter.clearViewSelection();
            }
        };
    }

    private AlertDialog createNewMemberDialog() {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_member, null);

        TextInputEditText memberFirstNameInput = view.findViewById(R.id.text_input_new_member_firstname);
        TextInputEditText memberLastNameInput = view.findViewById(R.id.text_input_new_member_lastname);

        AlertDialog addMemberDialog = new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                .setTitle(R.string.add_member_dialog_title)
                .setIcon(R.drawable.ic_members)
                .setView(view)
                .setPositiveButton(R.string.add_member_dialog_confirm_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO: Mitglied hinzufügen
                        Editable firstName = memberFirstNameInput.getEditableText();
                        Editable lastName = memberLastNameInput.getEditableText();

                        if(firstName != null && lastName != null){
                            appViewModel.insertMembers(new Member(firstName.toString(), lastName.toString()));
                            Log.d("addMemberDialog", "added Member: " + firstName + " " + lastName);
                        }else{
                            Log.d("addMemberDialog", "member name is null");
                        }
                    }
                })
                .setNegativeButton(R.string.add_member_dialog_cancel_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create();

        return addMemberDialog;
    }

    private void setupSearchBar(){
        searchBar.setHint(R.string.search_members_label);
        searchBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
    }

    private void setupAllMembersView(){
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        allMembers = appViewModel.getAllMembers();
        membersRecyclerViewAdapter = new AllMembersViewAdapter();
        membersRecyclerView.setAdapter(membersRecyclerViewAdapter);
        membersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        selectedMembers = new ArrayList<>();
        membersRecyclerViewAdapter.setMemberClickedListener(new AllMembersViewAdapter.MemberClickedListener() {
            @Override
            public void onMemberClicked(Member member) {
                if(selectedMembers.isEmpty()){
                    return;
                }
                if(selectedMembers.contains(member)){
                    Log.d("AllMembersFragment", "remove member from selection");
                    selectedMembers.remove(member);
                }else{
                    Log.d("AllMembersFragment", "add member to selection");
                    selectedMembers.add(member);
                }
                if(selectedMembers.isEmpty()){
                    itemSelectedActionMode.finish();
                }
            }

            @Override
            public void onMemberLongClicked(Member member) {
                Log.d("AllMembersFragment", "start ItemSelectedAction");
                if(selectedMembers.isEmpty()){
                    itemSelectedActionMode = parentActivity.startSupportActionMode(itemSelectedActionCallback);
                }
                if(selectedMembers.contains(member)){
                    Log.d("AllMembersFragment", "remove member from selection");
                    selectedMembers.remove(member);
                }else {
                    Log.d("AllMembersFragment", "add member to selection");
                    selectedMembers.add(member);
                }
                if(selectedMembers.isEmpty()){
                    Log.d("AllMembersFragment", "finish actionMode");
                    itemSelectedActionMode.finish();
                }
            }
        });
        allMembers.observe(requireActivity() , new Observer<List<Member>>() {
            @Override
            public void onChanged(List<Member> members) {
                membersRecyclerViewAdapter.submitList(members);
            }
        });
    }

    private void setupSearchView() {
        searchRecyclerView.setAdapter(membersRecyclerViewAdapter);
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
                        membersRecyclerViewAdapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
    }

    private AlertDialog createDeleteAllMembersDialog() {

        return new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                .setIcon(R.drawable.ic_delete_all)
                .setTitle(R.string.delete_all_members_alert_title)
                .setMessage(R.string.delete_all_members_alert_message)
                .setPositiveButton(R.string.confirm_action_button_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        parentActivity.getViewModel().deleteAllMembers();
                    }
                })
                .setNegativeButton(R.string.cancel_action_button_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).create();
    }

    private AlertDialog createImportMembersDialog() {

        return new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                .setIcon(R.drawable.ic_info)
                .setTitle(R.string.import_members_alert_title)
                .setMessage(R.string.import_members_alert_message)
                .setPositiveButton(R.string.confirm_action_button_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        launchFilePickerAndImportMembers();
                    }
                })
                .setNegativeButton(R.string.cancel_action_button_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).create();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        drawer.close();
        Bundle args = getArguments();
        if(args != null && args.getBoolean("deleteAllMembers", false)){
            AlertDialog deleteAllMembersDialog = createDeleteAllMembersDialog();
            deleteAllMembersDialog.show();
        } else if (args != null && args.getBoolean("importMembers", false)){
            AlertDialog importMembersDialog = createImportMembersDialog();
            importMembersDialog.show();
        }

    }

    ActivityResultLauncher<String> filePickerIntent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    Log.d("AllMembersFragment",result.toString());

                    String filePath;
                    Log.d("AllMembersFragment","URI = "+ result);
                    if ("content".equals(result.getScheme())) {
                        Cursor cursor = requireActivity().getContentResolver().query(result, new String[] { MediaStore.Downloads.DATA}, null, null, null);
                        assert cursor != null;
                        cursor.moveToFirst();
                        filePath = cursor.getString(0);
                        cursor.close();
                    } else {
                        filePath = result.getPath();
                    }
                    Log.d("AllMembersFragment","Filepath = "+ filePath);
                    int num = parentActivity.getViewModel().importMembersFromCsv(filePath);

                    Toast.makeText(getContext() ,num + " Mitglieder importiert", Toast.LENGTH_SHORT).show();
                }

            });

    private void launchFilePickerAndImportMembers() {
        filePickerIntent.launch("text/comma-separated-values");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        Log.d("EditMembersFragment", "OnResume");
        Bundle args = getArguments();
        assert args != null;
        args.putBoolean("importMembers", false);
        args.putBoolean("deleteAllMembers", false);
        super.onResume();
    }
}
