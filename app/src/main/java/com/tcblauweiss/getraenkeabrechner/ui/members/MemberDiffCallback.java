package com.tcblauweiss.getraenkeabrechner.ui.members;

import androidx.recyclerview.widget.DiffUtil;

import com.tcblauweiss.getraenkeabrechner.model.Member;

import java.util.List;

public class MemberDiffCallback extends DiffUtil.Callback {
    private final List<Member> oldMemberList;
    private final List<Member> newMemberList;

    MemberDiffCallback(List<Member> oldMemberList, List<Member> newMemberList){
        this.oldMemberList = oldMemberList;
        this.newMemberList = newMemberList;
    }
    @Override
    public int getOldListSize() {
        return oldMemberList.size();
    }

    @Override
    public int getNewListSize() {
        return newMemberList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldMemberList.get(oldItemPosition).getId() == newMemberList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldMemberList.get(oldItemPosition).getFirstName().equals(newMemberList.get(newItemPosition).getFirstName())
                && oldMemberList.get(oldItemPosition).getLastName().equals(newMemberList.get(newItemPosition).getLastName());
    }
}
