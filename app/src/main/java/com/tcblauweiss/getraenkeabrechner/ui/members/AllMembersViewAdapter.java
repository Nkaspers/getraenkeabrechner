package com.tcblauweiss.getraenkeabrechner.ui.members;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tcblauweiss.getraenkeabrechner.R;
import com.tcblauweiss.getraenkeabrechner.model.Member;

import java.util.ArrayList;
import java.util.List;


public class AllMembersViewAdapter extends RecyclerView.Adapter<AllMembersViewAdapter.ViewHolder> {
    private List<Member> localDataSet;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView lastNameTextView, firstNameTextView, firstLetterTextView;

        public ViewHolder(View view) {
            super(view);
            lastNameTextView = view.findViewById(R.id.text_member_last_name);
            firstNameTextView = view.findViewById(R.id.text_member_first_name);
            firstLetterTextView = view.findViewById(R.id.text_member_name_icon);
        }

        public TextView getLastNameTextView(){
            return lastNameTextView;
        }

        public TextView getFirstNameTextView()  {
            return firstNameTextView;
        }

        public TextView getFirstLetterTextView() {
            return firstLetterTextView;
        }
    }

    public AllMembersViewAdapter() {
        this.localDataSet = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.member_viewholder, viewGroup, false);
        return new ViewHolder(view);
    }

    public void submitList(final List<Member> members){
        localDataSet = members;
        //TODO: show animation on right position with diffutil
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        String lastName = localDataSet.get(position).getLastName();
        viewHolder.getLastNameTextView().setText(localDataSet.get(position).getLastName());
        viewHolder.getFirstNameTextView().setText(localDataSet.get(position).getFirstName());
        viewHolder.getFirstLetterTextView().setText(String.valueOf(lastName.charAt(0)));
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

}

