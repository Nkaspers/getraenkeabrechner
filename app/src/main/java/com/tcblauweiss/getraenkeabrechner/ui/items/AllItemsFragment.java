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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
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
import java.util.stream.Collectors;

/**
 * A fragment representing a list of Items.
 */
public class AllItemsFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private SettingsActivity parentActivity;
    private MaterialToolbar toolbar;
    private AppViewModel viewModel;
    private RecyclerView itemRecyclerView1;
    private RecyclerView itemRecyclerView2;
    private MyItemRecyclerViewAdapter allItemsViewAdapter1;
    private MyItemRecyclerViewAdapter allItemsViewAdapter2;
    private FloatingActionButton addItemFab;
    private FloatingActionButton addCategory1Fab;
    private FloatingActionButton addCategory2Fab;

    private LiveData<List<Item>> allItems;
    private List<Item> selectedItems;
    private ActionMode.Callback itemSelectedActionCallback;
    private ActionMode itemSelectedActionMode;

    private boolean isFabMenuOpen = false;
    
    public AllItemsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            int mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_items, container, false);
        parentActivity = (SettingsActivity) requireActivity();

        //reinitialize toolbar in case it was overwritten from previous fragments.
        toolbar = parentActivity.findViewById(R.id.toolbar);
        parentActivity.setSupportActionBar(toolbar);
        viewModel = new ViewModelProvider(this).get(AppViewModel.class);

        itemRecyclerView1 = view.findViewById(R.id.list_items_1);
        itemRecyclerView2 = view.findViewById(R.id.list_items_2);
        addItemFab = view.findViewById(R.id.fab_all_items_fragment);
        addCategory1Fab = view.findViewById(R.id.fab_all_items_fragment_add_category_1);
        addCategory2Fab = view.findViewById(R.id.fab_all_items_fragment_add_category_2);

        setupAllItemsView();
        setupActionMode();

        addItemFab.setOnClickListener(view1 -> {
             if( !isFabMenuOpen ){
                 openFabMenu();
             } else {
                 closeFabMenu();
             }
        });

        addCategory1Fab.setOnClickListener(view1 -> {
            createNewItemDialog(0).show();
            closeFabMenu();
        });

        addCategory2Fab.setOnClickListener(view1 -> {
            createNewItemDialog(1).show();
            closeFabMenu();
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
                allItemsViewAdapter1.clearViewSelection();
            }
        };
    }

    private void setupAllItemsView(){
        itemRecyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));
        itemRecyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));

        allItemsViewAdapter1 = new MyItemRecyclerViewAdapter();
        allItemsViewAdapter2 = new MyItemRecyclerViewAdapter();

        itemRecyclerView1.setAdapter(allItemsViewAdapter1);
        itemRecyclerView2.setAdapter(allItemsViewAdapter2);

        selectedItems = new ArrayList<>();
        allItems = viewModel.getAllItems();

        MyItemRecyclerViewAdapter.ItemClickedListener itemClickedListener = new MyItemRecyclerViewAdapter.ItemClickedListener() {
            @Override
            public void onItemClicked(Item item) {
                if(selectedItems.isEmpty()){
                    Log.d("AllItemsFragment", "edit item");
                    createEditItemDialog(item).show();
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
        };

        allItemsViewAdapter1.setItemClickedListener(itemClickedListener);
        allItemsViewAdapter2.setItemClickedListener(itemClickedListener);

        allItems.observe(requireActivity(), new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                allItemsViewAdapter1.submitList( items.stream().filter(item -> item.getCategory() == 0).collect(Collectors.toList()));
                allItemsViewAdapter2.submitList( items.stream().filter(item -> item.getCategory() == 1).collect(Collectors.toList()));
            }
        });
    }

    private void openFabMenu() {
        isFabMenuOpen = true;
        addItemFab.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_cancel, null));
        addCategory1Fab.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        addCategory2Fab.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        addCategory1Fab.setVisibility(View.VISIBLE);
        addCategory2Fab.setVisibility(View.VISIBLE);
    }

    private void closeFabMenu() {
        isFabMenuOpen = false;
        addItemFab.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_plus, null));
        addCategory1Fab.animate().translationY(0).withEndAction(new Runnable() {
            @Override
            public void run() {
                addCategory1Fab.setVisibility(View.INVISIBLE);
            }
        });

        addCategory2Fab.animate().translationY(0).withEndAction(new Runnable() {
            @Override
            public void run() {
                addCategory2Fab.setVisibility(View.INVISIBLE);
            }
        });
    }

    private AlertDialog createNewItemDialog(int category) {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_item, null);

        TextInputEditText itemNameInput = view.findViewById(R.id.text_input_new_item_name);
        TextInputEditText itemPriceInput = view.findViewById(R.id.text_input_new_item_price);

        itemPriceInput.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(3,2)});

        String title = category == 0 ? getString(R.string.add_item_category_1_dialog_title) : getString(R.string.add_item_category_2_dialog_title);
        int icon = category == 0 ? R.drawable.ic_beverage : R.drawable.ic_snack;

        return new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                .setIcon(icon)
                .setTitle(title)
                .setView(view)
                .setPositiveButton(R.string.add_member_dialog_confirm_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Editable itemName = itemNameInput.getEditableText();
                        Editable itemPrice = itemPriceInput.getEditableText();

                        if ( itemPrice.toString().isEmpty() || itemName.toString().isEmpty()) {
                            Toast.makeText(getContext(), R.string.add_item_dialog_error_message, Toast.LENGTH_SHORT).show();
                        } else if( allItems.getValue().stream().filter(item->item.getCategory() == category).count() > 5){
                            Toast.makeText(getContext(), R.string.add_item_dialog_max_items_error_message, Toast.LENGTH_SHORT).show();
                        } else {
                            double itemPriceDouble = Double.parseDouble(itemPrice.toString().replace(",", "."));
                            viewModel.insertItems(new Item(itemName.toString(), itemPriceDouble, category));
                            Log.d("addItemDialog", "added Item: " + itemName + " " + itemPrice);
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

    private AlertDialog createEditItemDialog(Item item) {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_item, null);

        TextInputEditText itemNameInput = view.findViewById(R.id.text_input_new_item_name);
        TextInputEditText itemPriceInput = view.findViewById(R.id.text_input_new_item_price);

        itemNameInput.setText(item.getName());
        itemPriceInput.setText(String.valueOf(item.getPrice()));

        int icon = item.getCategory() == 0 ? R.drawable.ic_beverage : R.drawable.ic_snack;

        itemPriceInput.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(3,2)});

        return new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                .setIcon(icon)
                .setTitle(R.string.edit_item_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.add_member_dialog_confirm_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Editable itemName = itemNameInput.getEditableText();
                        Editable itemPrice = itemPriceInput.getEditableText();

                        if( !itemPrice.toString().isEmpty() && !itemName.toString().isEmpty()){
                            double itemPriceDouble = Double.parseDouble(itemPrice.toString().replace(",", "."));
                            viewModel.updateItem(item.getId(), itemName.toString(), itemPriceDouble);
                            Log.d("addItemDialog", "edited Item: " + itemName + " " + itemPrice);
                        }else{
                            Log.d("addItemDialog", "item name or price is null");
                            Toast.makeText(getContext(), R.string.add_item_dialog_error_message, Toast.LENGTH_SHORT).show();
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
