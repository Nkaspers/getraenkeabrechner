package com.tcblauweiss.getraenkeabrechner.ui.mainactivity.itemselection;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.tcblauweiss.getraenkeabrechner.R;
import com.tcblauweiss.getraenkeabrechner.model.Item;
import com.tcblauweiss.getraenkeabrechner.model.ItemWrapper;
import com.tcblauweiss.getraenkeabrechner.model.Receipt;

import java.util.ArrayList;

public class ItemSelectionAdapter extends RecyclerView.Adapter<ItemSelectionAdapter.ViewHolder> {
    private ArrayList<Item> localItemList;
    private Receipt receipt;

    private ReceiptAdapter receiptAdapter;

    private ReceiptChangedListener receiptChangedListener;

    public interface ReceiptChangedListener {
        void onReceiptChanged(Receipt receipt);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView, priceTextView, amountTextView;
        private final Button decreaseButton, increaseButton;

         public ViewHolder(View view) {
             super(view);
             nameTextView = view.findViewById(R.id.text_itemselection_itemname);
             priceTextView = view.findViewById(R.id.text_itemselection_price);
             amountTextView = view.findViewById(R.id.text_itemselection_amount);
             decreaseButton = view.findViewById(R.id.btn_itemselection_decrease);
             increaseButton = view.findViewById(R.id.btn_itemselection_increase);
         }

         public TextView getNameTextView() {
             return nameTextView;
         }

         public TextView getPriceTextView() {
             return priceTextView;
         }

         public TextView getAmountTextView() {return amountTextView;}

        public Button getDecreaseButton() {return decreaseButton;}

        public Button getIncreaseButton() {return increaseButton;}

    }

    public ItemSelectionAdapter(ArrayList<Item> itemList, Receipt receipt, ReceiptAdapter receiptAdapter) {
        localItemList = itemList;
        this.receipt = receipt;
        this.receiptAdapter = receiptAdapter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.itemselection_viewholder, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getNameTextView().setText(localItemList.get(position).getName());
        viewHolder.getPriceTextView().setText(localItemList.get(position).getPriceString());
        viewHolder.getAmountTextView().setText(String.valueOf(receipt.getNumberOfItems(localItemList.get(position))));
        viewHolder.getDecreaseButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = viewHolder.getBindingAdapterPosition();
                receipt.removeItem(localItemList.get(adapterPosition));
                receiptAdapter.updateData(receipt.getItemsAndCount());
                viewHolder.getAmountTextView().setText(String.valueOf(receipt.getNumberOfItems(localItemList.get(adapterPosition))));
                receiptChangedListener.onReceiptChanged(receipt);
            }
        });

        viewHolder.getIncreaseButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = viewHolder. getBindingAdapterPosition();
                receipt.addItem(localItemList.get(adapterPosition));
                receiptAdapter.updateData(receipt.getItemsAndCount());
                viewHolder.getAmountTextView().setText(String.valueOf(receipt.getNumberOfItems(localItemList.get(adapterPosition))));
                receiptChangedListener.onReceiptChanged(receipt);
            }
        });
    }

    public void setReceiptChangedListender(ReceiptChangedListener receiptChangedListener) {
        this.receiptChangedListener = receiptChangedListener;
    }

    public int getItemCount() {
        return localItemList.size();
    }

    public void refresh() {
        notifyDataSetChanged();
    }

}
