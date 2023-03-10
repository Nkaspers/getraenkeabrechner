package com.tcblauweiss.getraenkeabrechner.ui.members;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.tcblauweiss.getraenkeabrechner.R;

public class ImportMembersFragment extends Fragment {
    AppCompatActivity parentActivity;
    MaterialToolbar toolbar;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_import_members, container, false);
        parentActivity = (AppCompatActivity) getActivity();
        //reinitialize toolbar in case it was overwritten from previous fragments.
        toolbar = parentActivity.findViewById(R.id.toolbar);
        parentActivity.setSupportActionBar(toolbar);
        parentActivity.getSupportActionBar().show();

        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}