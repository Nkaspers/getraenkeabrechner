package com.tcblauweiss.getraenkeabrechner.ui.administration;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.tcblauweiss.getraenkeabrechner.R;
import com.tcblauweiss.getraenkeabrechner.SettingsActivity;

public class ChangePasswordFragment extends Fragment {

    private SettingsActivity parentActivity;
    private MaterialToolbar toolbar;

    public ChangePasswordFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        parentActivity = (SettingsActivity) getActivity();

        //reinitialize toolbar in case it was overwritten from previous fragments.
        toolbar = parentActivity.findViewById(R.id.toolbar);
        parentActivity.setSupportActionBar(toolbar);

        return view;
    }

    @Override
    public void onResume() {
        Log.d("ChangePasswordFragment", "OnResume");
        Bundle args = getArguments();
        assert args != null;
        args.putBoolean("lockDevice", false);
        super.onResume();
    }
}


