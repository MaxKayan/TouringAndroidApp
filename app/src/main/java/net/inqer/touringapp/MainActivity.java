package net.inqer.touringapp;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import net.inqer.touringapp.databinding.ActivityMainBinding;
import net.inqer.touringapp.util.DrawableHelpers;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MAIN_INTENT_TYPE = "EXTRA_MAIN_INTENT_TYPE";
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private NavController navController;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupNavigation();

        setupClickListeners();

        binding.fab.setCompatPressedTranslationZ(1f);

        handleIntent(getIntent());
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    public void restartApp() {
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(
                getBaseContext().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
    }

    private void setupNavigation() {
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_map, R.id.navigation_settings)
                .build();

        NavHostFragment hostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        if (hostFragment == null) {
            Log.e(TAG, "setupNavigation: Nav Host Fragment is null! Navigation is not initialized.");
            return;
        }

        navController = hostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            setFabState(destination.getId() == R.id.navigation_map);
        });
    }

    private void setupClickListeners() {
        binding.fab.setOnClickListener(view -> {
            navigateTo(R.id.navigation_map);
        });
    }

    private void navigateTo(final int navigationId) {
        NavDestination destination = navController.getCurrentDestination();
        if (destination == null || navController == null || destination.getId() == navigationId)
            return;

        navController.navigate(navigationId, null);
    }

    private void setFabState(boolean active) {
        if (active) {
            DrawableHelpers.INSTANCE.modifyFab(this, binding.fab,
                    R.drawable.ic_outline_map_24, R.color.purple_200);
        } else {
            DrawableHelpers.INSTANCE.modifyFab(this, binding.fab,
                    R.drawable.ic_outline_map_24,
                    android.R.color.darker_gray,
                    R.color.bottom_panel);
        }
    }

    private void handleIntent(Intent intent) {
        IntentType intentType = (IntentType) intent.getSerializableExtra(EXTRA_MAIN_INTENT_TYPE);

        NavDestination destination = navController.getCurrentDestination();
        Integer id = destination != null ? destination.getId() : null;

        if (intentType == IntentType.TO_MAP_FRAGMENT) {
            if (id != null && id == R.id.navigation_map) return;

            navigateTo(R.id.navigation_map);
        }
    }

    public enum IntentType {
        TO_MAP_FRAGMENT;
    }
}
