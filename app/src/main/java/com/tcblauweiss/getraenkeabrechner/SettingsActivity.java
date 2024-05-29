package com.tcblauweiss.getraenkeabrechner;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.tcblauweiss.getraenkeabrechner.databinding.ActivitySettingsBinding;
import com.tcblauweiss.getraenkeabrechner.model.AppViewModel;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private AppViewModel viewModel;
    private NavController navController;
    private DrawerLayout drawer;
    private MaterialSwitch kioskModeSwitch;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName adminComponentName;
    private boolean isKioskMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.tcblauweiss.getraenkeabrechner.databinding.ActivitySettingsBinding binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarSettings.toolbar);

        drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        viewModel = new AppViewModel(getApplication());

        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        adminComponentName = DeviceAdminReceiver.getComponentName(this);

        setupNavigationLayout();
        setNavigationDrawerListeners(navigationView);

        kioskModeSwitch = Objects.requireNonNull(navigationView.getMenu().findItem(R.id.nav_kiosk_mode).getActionView()).findViewById(R.id.nav_switch);

        kioskModeSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if(isChecked && !isKioskMode){
                createLockDeviceDialog().show();
            }else if (!isChecked && isKioskMode){
                disableKioskMode();
            }
        });
    }

    private void setupNavigationLayout(){
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_members_edit, R.id.nav_members_import, R.id.nav_members_delete_all, R.id.nav_entries_show_all,
                R.id.nav_entries_cancel, R.id.nav_entries_export, R.id.nav_entries_delete_all,
                R.id.nav_items_edit, R.id.nav_password_change, R.id.nav_kiosk_mode, R.id.nav_return_to_main)
                .setOpenableLayout(drawer)
                .build();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container_activity_settings);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
    }
    public void setNavigationDrawerListeners(NavigationView navigationView){

        navigationView.getMenu().findItem(R.id.nav_members_edit).setOnMenuItemClickListener(view -> {
            navController.navigate(R.id.nav_members_edit);
            return false;
        });
        navigationView.getMenu().findItem(R.id.nav_members_import).setOnMenuItemClickListener(view -> {
            navController.navigate(R.id.action_import_members);
            return false;
        });

        navigationView.getMenu().findItem(R.id.nav_members_delete_all).setOnMenuItemClickListener(view -> {
            navController.navigate(R.id.action_delete_all_members);
            return false;
        });

        navigationView.getMenu().findItem(R.id.nav_entries_show_all).setOnMenuItemClickListener(view -> {
            navController.navigate(R.id.nav_entries_show_all);
            return false;
        });

        navigationView.getMenu().findItem(R.id.nav_entries_cancel).setOnMenuItemClickListener(view -> {
            Toast.makeText(SettingsActivity.this,"Buchungen stornieren", Toast.LENGTH_SHORT).show();
            navController.navigate(R.id.action_cancel_entry);
            return false;
        });

        navigationView.getMenu().findItem(R.id.nav_entries_export).setOnMenuItemClickListener(view -> {
            Toast.makeText(SettingsActivity.this,"Buchungen exportieren", Toast.LENGTH_SHORT).show();
            navController.navigate(R.id.action_entries_export);
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

        navigationView.getMenu().findItem(R.id.nav_password_change).setOnMenuItemClickListener(view -> {
            navController.navigate(R.id.nav_password_change);
            return false;
        });

        navigationView.getMenu().findItem(R.id.nav_kiosk_mode).setOnMenuItemClickListener(view -> {
            kioskModeSwitch.toggle();
            return false;
        });

        navigationView.getMenu().findItem(R.id.nav_return_to_main).setOnMenuItemClickListener(view -> {
            Intent intent_launch_mainActivity = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent_launch_mainActivity);
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Open drawer after FragmentView ist created
        drawer.openDrawer(GravityCompat.START);
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

        isKioskMode = activityManager.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_LOCKED;

        if( isKioskMode ) {
            kioskModeSwitch.setChecked(true);
        }
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


    private void enableKioskMode() {
        setDefaultDeviceLockPolicies(true);
        startLockTask();
        isKioskMode = true;
        Toast.makeText(this, "Gesperrter Modus wurde aktiviert", Toast.LENGTH_SHORT).show();
    }
    private void disableKioskMode() {
        stopLockTask();
        setDefaultDeviceLockPolicies(false);
        isKioskMode = false;
        Toast.makeText(this, "Gesperrter Modus wurde deaktiviert", Toast.LENGTH_SHORT).show();
    }

    private AlertDialog createLockDeviceDialog() {

        return new MaterialAlertDialogBuilder(this, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                .setIcon(R.drawable.ic_lock)
                .setTitle(R.string.lock_device_alert_title)
                .setMessage(R.string.lock_device_alert_message)
                .setPositiveButton(R.string.confirm_action_button_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        enableKioskMode();
                    }
                })
                .setNegativeButton(R.string.cancel_action_button_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        kioskModeSwitch.setChecked(false);
                    }
                }).create();
    }

    private void setDefaultDeviceLockPolicies(boolean active){

        if ( !devicePolicyManager.isDeviceOwnerApp( getPackageName() ) ){
            Log.d("SettingsActivity", "App is not device owner.");
            Toast.makeText(this, "App muss Besitzer des Gerätes sein, um den gesperrten Modus zu aktivieren", Toast.LENGTH_SHORT).show();

            return;
        }

        final String[] LOCKED_APPS = {getApplicationContext().getPackageName()};

        // set user restrictions
        setUserRestriction(UserManager.DISALLOW_SAFE_BOOT, active);
        setUserRestriction(UserManager.DISALLOW_FACTORY_RESET, false);
        setUserRestriction(UserManager.DISALLOW_ADD_USER, active);
        setUserRestriction(UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, active);
        setUserRestriction(UserManager.DISALLOW_ADJUST_VOLUME, active);

        // disable keyguard and status bar
        devicePolicyManager.setKeyguardDisabled(adminComponentName, active);
        devicePolicyManager.setStatusBarDisabled(adminComponentName, active);
        devicePolicyManager.setLockTaskPackages(adminComponentName, LOCKED_APPS);
        devicePolicyManager.setLockTaskFeatures(adminComponentName, DevicePolicyManager.LOCK_TASK_FEATURE_SYSTEM_INFO);
    }

    private void setUserRestriction(String restriction, boolean disallow) {
        if (disallow) {
            devicePolicyManager.addUserRestriction(adminComponentName,
                    restriction);
        } else {
            devicePolicyManager.clearUserRestriction(adminComponentName,
                    restriction);
        }
    }

    public AppViewModel getViewModel() {
        return viewModel;
    }
    public DrawerLayout getDrawer() {
        return drawer;
    }
}