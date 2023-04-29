package com.tcblauweiss.getraenkeabrechner.ui.items;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.tcblauweiss.getraenkeabrechner.R;
import com.tcblauweiss.getraenkeabrechner.SettingsActivity;
import com.tcblauweiss.getraenkeabrechner.model.AppViewModel;
import com.tcblauweiss.getraenkeabrechner.model.Item;
import com.tcblauweiss.getraenkeabrechner.model.Member;

import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class AllItemsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private SettingsActivity parentActivity;
    private MaterialToolbar toolbar;
    private AppViewModel viewModel;
    private RecyclerView recyclerView;
    private FloatingActionButton addItemFab;


    public AllItemsFragment() {
    }

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

        addItemFab.setOnClickListener(view1 -> {
            AlertDialog newMemberDialog = createNewItemDialog();
            newMemberDialog.show();

        });
        return view;
    }

    private void setupAllItemsView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        MyItemRecyclerViewAdapter recyclerViewAdapter = new MyItemRecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);

        LiveData<List<Item>> itemsLiveData = viewModel.getAllItems();
        itemsLiveData.observe(requireActivity(), recyclerViewAdapter::submitList);
    }

    private AlertDialog createNewItemDialog() {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_item, null);

        TextInputEditText itemNameInput = view.findViewById(R.id.text_input_new_item_name);
        TextInputEditText itemPriceInput = view.findViewById(R.id.text_input_new_item_price);

        AlertDialog addMemberDialog = new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
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

        return addMemberDialog;
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
