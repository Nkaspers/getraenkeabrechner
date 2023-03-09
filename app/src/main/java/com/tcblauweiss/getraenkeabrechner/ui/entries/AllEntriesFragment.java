package com.tcblauweiss.getraenkeabrechner.ui.entries;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.search.SearchBar;
import com.tcblauweiss.getraenkeabrechner.R;


public class AllEntriesFragment extends Fragment {
    SearchBar searchBar;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_entries, container, false);
        searchBar = view.findViewById(R.id.searchbar_all_entries);
        searchBar.setNavigationIcon(R.drawable.ic_menu);
        ((AppCompatActivity) getActivity()).setSupportActionBar(searchBar);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}