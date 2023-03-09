package com.tcblauweiss.getraenkeabrechner.ui.mainactivity.itemselection;

import android.telephony.ims.ImsMmTelManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.tcblauweiss.getraenkeabrechner.R;
import com.tcblauweiss.getraenkeabrechner.model.Item;

import java.util.ArrayList;

public class ItemSelectionAdapter extends RecyclerView.Adapter<ItemSelectionAdapter.ViewHolder> {
    private ArrayList<Item> localItemList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView, priceTextView;

         public ViewHolder(View view) {
             super(view);
             nameTextView = view.findViewById(R.id.text_itemselection_itemname);
             priceTextView = view.findViewById(R.id.text_itemselection_price);
         }

         public TextView getNameTextView() {
             return nameTextView;
         }

         public TextView getPriceTextView() {
             return priceTextView;
         }
    }

    public ItemSelectionAdapter(ArrayList<Item> itemList) {
        localItemList = itemList;
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
        viewHolder.getPriceTextView().setText(localItemList.get(position).getPrice().toString());
    }

    public int getItemCount() {
        return localItemList.size();
    }
}
