package net.inqer.touringapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.snackbar.Snackbar;

import net.inqer.touringapp.databinding.ActivityMainBinding;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Inject
    @Named("String1")
    String testString1;

    private ActivityMainBinding binding;
    private NavController navController;

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupNavigation();

        setupClickListeners();
    }

    public void restart() {
        Intent intent = getIntent();
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

            Toast.makeText(this, String.format("Injected String - %s \n hash - %s",
                    testString1, testString1.hashCode()), Toast.LENGTH_SHORT).show();

//            Snackbar.make(view, String.format("View model string - \n %s", viewModel.getTestString2()), Snackbar.LENGTH_LONG).show();
        });
    }

    private void navigateTo(final int navigationId) {
        NavDestination destination = navController.getCurrentDestination();
        if (destination == null || navController == null || destination.getId() == navigationId)
            return;

        navController.navigate(navigationId, null);
    }

    private void setFabState(boolean active) {
        Drawable fabIcon = ContextCompat.getDrawable(this, R.drawable.ic_outline_map_24);
        if (fabIcon == null) {
            Log.e(TAG, "setFabState: fab icon is null!");
            return;
        }

        if (active) {
            binding.fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.design_default_color_primary)));

            fabIcon.mutate().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        } else {
            binding.fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.material_on_background_emphasis_medium)));

            fabIcon.mutate().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
        }

        binding.fab.setImageDrawable(fabIcon);
    }

}
