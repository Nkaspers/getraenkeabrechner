package com.tcblauweiss.getraenkeabrechner;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.tcblauweiss.getraenkeabrechner.databinding.ActivitySettingsBinding;
import com.tcblauweiss.getraenkeabrechner.model.AppViewModel;
import com.tcblauweiss.getraenkeabrechner.ui.entries.AllEntriesFragment;

public class SettingsActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivitySettingsBinding binding;
    private AppViewModel viewModel;
    private FragmentManager fragmentManager;
    private NavController navController;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarSettings.toolbar);
        drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        viewModel = new AppViewModel(getApplication());
        fragmentManager = getSupportFragmentManager();

        //INITIALIZE DRAWER NAVIGATION MENU
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_members_edit, R.id.nav_members_import, R.id.nav_entries_show_all,
                R.id.nav_entries_cancel, R.id.nav_entries_export, R.id.nav_entries_delete_all,
                R.id.nav_items_edit, R.id.nav_password_change, R.id.nav_return_to_main)
                .setOpenableLayout(drawer)
                .build();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container_activity_settings);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        setNavigationDrawerListeners(navigationView);
        drawer.openDrawer(GravityCompat.START);
    }

    public void setNavigationDrawerListeners(NavigationView navigationView){

        navigationView.getMenu().findItem(R.id.nav_members_edit).setOnMenuItemClickListener(view -> {
            navController.navigate(R.id.nav_members_edit);
            return false;
        });
        navigationView.getMenu().findItem(R.id.nav_members_import).setOnMenuItemClickListener(view -> {
            Toast.makeText(SettingsActivity.this,"Mitglieder importieren", Toast.LENGTH_SHORT).show();
            navController.navigate(R.id.action_import_members);
            return false;
        });

        navigationView.getMenu().findItem(R.id.nav_entries_cancel).setOnMenuItemClickListener(view -> {
            Toast.makeText(SettingsActivity.this,"Buchungen stornieren", Toast.LENGTH_SHORT).show();
            //TODO: Buchungen stornieren
            return false;
        });

        navigationView.getMenu().findItem(R.id.nav_entries_export).setOnMenuItemClickListener(view -> {
            Toast.makeText(SettingsActivity.this,"Buchungen exportieren", Toast.LENGTH_SHORT).show();
            //TODO: Buchungen exportieren
            return false;
        });

        navigationView.getMenu().findItem(R.id.nav_entries_delete_all).setOnMenuItemClickListener(view -> {
            navController.navigate(R.id.action_delete_all_entries);
            return false;
        });

        navigationView.getMenu().findItem(R.id.nav_items_edit).setOnMenuItemClickListener(view -> {
            navController.navigate(R.id.nav_items_edit);
            return false;
        });

        navigationView.getMenu().findItem(R.id.nav_return_to_main).setOnMenuItemClickListener(view -> {
            Intent intent_launch_mainActivity = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent_launch_mainActivity);
            return false;
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.fragment_container_activity_settings);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public AppViewModel getViewModel() {
        return viewModel;
    }
    public DrawerLayout getDrawer() {
        return drawer;
    }
}