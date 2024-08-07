package com.tcblauweiss.getraenkeabrechner.ui.mainactivity.itemselection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.tcblauweiss.getraenkeabrechner.R;
import com.tcblauweiss.getraenkeabrechner.model.Item;
import com.tcblauweiss.getraenkeabrechner.model.Receipt;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ItemSelectionAdapter extends RecyclerView.Adapter<ItemSelectionAdapter.ViewHolder> {
    private final ArrayList<Item> localItemList;
    private final Receipt receipt;

    private final ReceiptAdapter receiptAdapter;

    private ReceiptChangedListener receiptChangedListener;

    public interface ReceiptChangedListener {
        void onReceiptChanged(Receipt receipt);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public static final int DUMMY = 0;
        public static final int ITEM = 1;

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

    public ItemSelectionAdapter(Receipt receipt, ReceiptAdapter receiptAdapter) {
        localItemList = new ArrayList<>();
        this.receipt = receipt;
        this.receiptAdapter = receiptAdapter;
    }

    public void submitList(@NonNull List<Item> items){
        localItemList.clear();
        items.stream()
                .filter(item -> item.getCategory() == 0)
                .sorted(Comparator.comparing(Item::getId))
                .forEach(localItemList::add);

        addDummyItems(6);

        items.stream()
                .filter(item -> item.getCategory() == 1)
                .sorted(Comparator.comparing(Item::getId))
                .forEach(localItemList::add);

        addDummyItems(12);

        notifyDataSetChanged();
    }

    private void addDummyItems(int targetSize) {
        while (localItemList.size() < targetSize) {
            localItemList.add(new Item("", 0, 0));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = (viewType == ViewHolder.DUMMY) ?
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.viewholder_itemselection_dummy, viewGroup, false) :
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.viewholder_itemselection, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return (int) getItemId(position);
    }

    @Override
    public long getItemId(int position) {
       if ( localItemList.get(position).getName().isEmpty() ) {
           return ViewHolder.DUMMY;
       } else {
           return ViewHolder.ITEM;
       }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        if (viewHolder.getItemViewType() == ViewHolder.DUMMY) {
            return;
        }

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

    public void setReceiptChangedListener(ReceiptChangedListener receiptChangedListener) {
        this.receiptChangedListener = receiptChangedListener;
    }

    public int getItemCount() {
        return localItemList.size();
    }

    public void refresh() {
        notifyDataSetChanged();
    }


}
