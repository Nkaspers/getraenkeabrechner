package com.tcblauweiss.getraenkeabrechner.ui.items;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tcblauweiss.getraenkeabrechner.databinding.ViewholderItemsBinding;
import com.tcblauweiss.getraenkeabrechner.model.Item;
import com.tcblauweiss.getraenkeabrechner.ui.mainactivity.itemselection.ItemDiffCallback;
//import com.tcblauweiss.getraenkeabrechner.ui.items.databinding.FragmentAllItemsBinding;

import java.util.ArrayList;
import java.util.List;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<Item> itemList;
    private final List<View> selectedViews;
    private ItemClickedListener itemClickedListener;
    public interface ItemClickedListener {
        void onItemClicked(Item item);
        void onItemLongClicked(Item item);
    }

    public MyItemRecyclerViewAdapter() {
        this.itemList = new ArrayList<>();
        this.selectedViews = new ArrayList<>();
    }

    public void setItemClickedListener(ItemClickedListener itemClickedListener) {
        this.itemClickedListener = itemClickedListener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(ViewholderItemsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    public void submitList(List<Item> items) {
        final ItemDiffCallback itemDiffCallback = new ItemDiffCallback(this.itemList, items);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(itemDiffCallback);

        this.itemList.clear();
        this.itemList.addAll(items);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        Item item = itemList.get(position);
        viewHolder.getNumberTextView().setText(String.valueOf(position));
        viewHolder.getNameTextView().setText(item.getName());
        viewHolder.getPriceTextView().setText(item.getPriceString());

        if (itemClickedListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(selectedViews.isEmpty()){
                        return;
                    }
                    if(view.isActivated()){
                        Log.d("AllItemsViewAdapter", "remove view from selection");
                        selectedViews.remove(view);
                        view.setActivated(false);
                    }else{
                        Log.d("AllItemsViewAdapter", "add view to selection");
                        selectedViews.add(view);
                        view.setActivated(true);
                    }
                    itemClickedListener.onItemClicked(item);
                }
            });
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    //using setActivated is better than setSelected according to documentation
                    if(view.isActivated()){
                        Log.d("AllItemsViewAdapter", "remove view from selection");
                        selectedViews.remove(view);
                        view.setActivated(false);
                    }else {
                        Log.d("AllItemsViewAdapter", "add view to selection");
                        selectedViews.add(view);
                        view.setActivated(true);
                    }
                    itemClickedListener.onItemLongClicked(item);
                    //return true to consume event so that normal onClick() is not triggered
                    return true;
                }
            });
        }
    }

    public void clearViewSelection(){
        Log.d("AllItemsViewAdapter", "clear view selection");
        for(View view: selectedViews){
            view.setActivated(false);
        }
        selectedViews.clear();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView, numberTextView, priceTextView;

        public ViewHolder(ViewholderItemsBinding binding) {
            super(binding.getRoot());
            numberTextView = binding.itemNumber;
            nameTextView = binding.itemName;
            priceTextView = binding.itemPrice;
        }

        public TextView getNameTextView() {
            return nameTextView;
        }

        public TextView getNumberTextView() {
            return numberTextView;
        }

        public TextView getPriceTextView() {
            return priceTextView;
        }
    }
}