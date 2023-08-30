package com.tcblauweiss.getraenkeabrechner.ui.items;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.util.Log;
import androidx.appcompat.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.tcblauweiss.getraenkeabrechner.R;
import com.tcblauweiss.getraenkeabrechner.SettingsActivity;
import com.tcblauweiss.getraenkeabrechner.model.AppViewModel;
import com.tcblauweiss.getraenkeabrechner.model.Item;
import com.tcblauweiss.getraenkeabrechner.util.DecimalDigitsInputFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class AllItemsFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private SettingsActivity parentActivity;
    private MaterialToolbar toolbar;
    private AppViewModel viewModel;
    private RecyclerView recyclerView;
    private MyItemRecyclerViewAdapter allItemsViewAdapter;
    private FloatingActionButton addItemFab;
    private LiveData<List<Item>> allItems;
    private List<Item> selectedItems;
    private ActionMode.Callback itemSelectedActionCallback;
    private ActionMode itemSelectedActionMode;
    
    public AllItemsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_items, container, false);
        parentActivity = (SettingsActivity) getActivity();

        //reinitialize toolbar in case it was overwritten from previous fragments.
        toolbar = parentActivity.findViewById(R.id.toolbar);
        parentActivity.setSupportActionBar(toolbar);
        viewModel = new ViewModelProvider(this).get(AppViewModel.class);

        recyclerView = view.findViewById(R.id.list_all_items);
        addItemFab = view.findViewById(R.id.fab_all_items_fragment);
        setupAllItemsView();
        setupActionMode();

        addItemFab.setOnClickListener(view1 -> {
            AlertDialog newMemberDialog = createNewItemDialog();
            newMemberDialog.show();

        });
        return view;
    }

    private void setupActionMode() {
        itemSelectedActionCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                Log.d("AllItemFragment", "create actionMode");
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
                    Item[] items = selectedItems.toArray(new Item[0]);
                    viewModel.deleteItems(items);
                    actionMode.finish();
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                Log.d("AllItemFragment", "destroy actionMode");
                selectedItems.clear();
                allItemsViewAdapter.clearViewSelection();
            }
        };
    }

    private void setupAllItemsView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        allItemsViewAdapter = new MyItemRecyclerViewAdapter();
        recyclerView.setAdapter(allItemsViewAdapter);
        selectedItems = new ArrayList<>();
        allItems = viewModel.getAllItems();
        allItemsViewAdapter.setItemClickedListener(new MyItemRecyclerViewAdapter.ItemClickedListener() {
            @Override
            public void onItemClicked(Item item) {
                if(selectedItems.isEmpty()){
                    return;
                }
                if(selectedItems.contains(item)){
                    Log.d("AllItemsFragment", "remove item from selection");
                    selectedItems.remove(item);
                }else{
                    Log.d("AllItemsFragment", "add item to selection");
                    selectedItems.add(item);
                }
                if(selectedItems.isEmpty()){
                    itemSelectedActionMode.finish();
                }
            }

            @Override
            public void onItemLongClicked(Item item) {
                Log.d("AllItemsFragment", "start ItemSelectedAction");
                if(selectedItems.isEmpty()){
                    itemSelectedActionMode = parentActivity.startSupportActionMode(itemSelectedActionCallback);
                }
                if(selectedItems.contains(item)){
                    Log.d("AllItemsFragment", "remove item from selection");
                    selectedItems.remove(item);
                }else {
                    Log.d("AllItemsFragment", "add item to selection");
                    selectedItems.add(item);
                }
                if(selectedItems.isEmpty()){
                    Log.d("AllItemsFragment", "finish actionMode");
                    itemSelectedActionMode.finish();
                }
            }
        });

        allItems.observe(requireActivity(), allItemsViewAdapter::submitList);
    }

    private AlertDialog createNewItemDialog() {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_item, null);

        TextInputEditText itemNameInput = view.findViewById(R.id.text_input_new_item_name);
        TextInputEditText itemPriceInput = view.findViewById(R.id.text_input_new_item_price);

        itemPriceInput.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(3,2)});

        return new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                .setIcon(R.drawable.ic_beverage)
                .setTitle(R.string.add_item_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.add_member_dialog_confirm_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Editable itemName = itemNameInput.getEditableText();
                        Editable itemPrice = itemPriceInput.getEditableText();
                        double itemPriceDouble = Double.parseDouble(itemPrice.toString().replace(",", "."));

                        if(!itemName.toString().equals("") && !itemPrice.toString().equals("")){
                            viewModel.insertItems(new Item(itemName.toString(), itemPriceDouble));
                            Log.d("addItemDialog", "added Item: " + itemName + " " + itemPrice);
                        }else{
                            Log.d("addItemDialog", "item name is null");
                        }
                    }
                })
                .setNegativeButton(R.string.add_member_dialog_cancel_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DrawerLayout drawer = parentActivity.getDrawer();
        drawer.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
