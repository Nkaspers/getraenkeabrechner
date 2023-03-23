package com.tcblauweiss.getraenkeabrechner.ui.mainactivity.itemselection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tcblauweiss.getraenkeabrechner.R;
import com.tcblauweiss.getraenkeabrechner.model.ItemWrapper;

import java.util.ArrayList;
import java.util.List;

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ViewHolder> {
    private ArrayList<ItemWrapper> receiptItemList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView countTextView;
        private final TextView nameTextView;
        private final TextView priceTextView;

        private final TextView totalTextView;

        public ViewHolder(View view){
            super(view);
            countTextView = view.findViewById(R.id.text_receipt_item_count);
            nameTextView = view.findViewById(R.id.text_receipt_item_name);
            priceTextView = view.findViewById(R.id.text_receipt_item_price);
            totalTextView = view.findViewById(R.id.text_receipt_item_total);
        }
        public TextView getCountTextView() {
            return countTextView;
        }

        public TextView getNameTextView() {
            return nameTextView;
        }

        public TextView getPriceTextView() {
            return priceTextView;
        }

        public TextView getTotalTextView() {
            return totalTextView;
        }
    }


    public ReceiptAdapter(ArrayList<ItemWrapper> receiptItemList) {
        this.receiptItemList = receiptItemList;
    }

    public void updateData(ArrayList<ItemWrapper> newReceiptItemList) {
        final ItemWrapperDiffCallback diffCallback = new ItemWrapperDiffCallback(this.receiptItemList, newReceiptItemList);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.receiptItemList = newReceiptItemList;
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.receipt_item_viewholder, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getCountTextView().setText(String.valueOf(receiptItemList.get(position).getCount()));
        viewHolder.getNameTextView().setText(receiptItemList.get(position).getItem().getName());
        viewHolder.getPriceTextView().setText(receiptItemList.get(position).getItem().getPriceString());
        viewHolder.getTotalTextView().setText(receiptItemList.get(position).getTotalString());
    }

    @Override
    public int getItemCount(){
        return receiptItemList.size();
    }

    public ArrayList<ItemWrapper> getReceiptItemList() {
        return receiptItemList;
    }
}
