package com.tcblauweiss.getraenkeabrechner.ui.members;

import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tcblauweiss.getraenkeabrechner.R;
import com.tcblauweiss.getraenkeabrechner.model.Member;

import java.util.ArrayList;
import java.util.List;


public class AllMembersViewAdapter extends RecyclerView.Adapter<AllMembersViewAdapter.ViewHolder> implements Filterable {
    private List<Member> localDataSetFiltered;
    private List<Member> localDataSet;
    private final List<View> selectedViews;
    private MemberClickedListener memberClickedListener;
    public interface MemberClickedListener {
        void onMemberClicked(Member member);
        void onMemberLongClicked(Member member);
    }

    public AllMembersViewAdapter() {
        this.localDataSetFiltered = new ArrayList<>();
        this.localDataSet = new ArrayList<>();
        this.selectedViews = new ArrayList<>();
    }

    public void setMemberClickedListener(MemberClickedListener memberClickedListener) {
        this.memberClickedListener = memberClickedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.viewholder_member, viewGroup, false);
        return new ViewHolder(view);
    }

    public void submitList(final List<Member> members){
        localDataSet = members;
        MemberDiffCallback diffCallback = new MemberDiffCallback(localDataSetFiltered, members);
        localDataSetFiltered = members;
        DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo(this);
    }

    private void submitFilter(List<Member> filteredMembers){
        MemberDiffCallback diffCallback = new MemberDiffCallback(localDataSetFiltered, filteredMembers);
        localDataSetFiltered = filteredMembers;
        DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo(this);
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Member member = localDataSetFiltered.get(position);
        String lastName = member.getLastName();

        viewHolder.getLastNameTextView().setText(member.getLastName()+",");
        viewHolder.getFirstNameTextView().setText(member.getFirstName());
        viewHolder.getFirstLetterTextView().setText(String.valueOf(lastName.charAt(0)));

        if (memberClickedListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(selectedViews.isEmpty()){
                        return;
                    }
                    if(view.isActivated()){
                        Log.d("AllMembersViewAdapter", "remove view from selection");
                        selectedViews.remove(view);
                        view.setActivated(false);
                    }else{
                        Log.d("AllMembersViewAdapter", "add view to selection");
                        selectedViews.add(view);
                        view.setActivated(true);
                    }
                    memberClickedListener.onMemberClicked(member);
                }
            });
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    //using setActivated is better than setSelected according to documentation
                    if(view.isActivated()){
                        Log.d("AllMembersViewAdapter", "remove view from selection");
                        selectedViews.remove(view);
                        view.setActivated(false);
                    }else {
                        Log.d("AllMembersViewAdapter", "add view to selection");
                        selectedViews.add(view);
                        view.setActivated(true);
                    }
                    memberClickedListener.onMemberLongClicked(member);
                    //return true to consume event so that normal onClick() is not triggered
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return localDataSetFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String searchStr = charSequence.toString().toLowerCase();
                List<Member> filteredMembers = new ArrayList<>();
                if(searchStr.isEmpty()) {
                    filteredMembers = localDataSet;
                }
                else {
                    for(Member member: localDataSet){
                        if(member.getLastName().toLowerCase().contains(searchStr) ||
                                member.getFirstName().toLowerCase().contains(searchStr)){
                            filteredMembers.add(member);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredMembers;
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                submitFilter((List<Member>) filterResults.values);
            }
        };
    }

    public void clearViewSelection(){
        Log.d("AllMembersViewAdapter", "clear view selection");
        for(View view: selectedViews){
            view.setActivated(false);
        }
        selectedViews.clear();
    }

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
}
