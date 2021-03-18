package net.inqer.touringapp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import net.inqer.touringapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();

        navController = ((NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment)).getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            Log.d(TAG, String.format("onCreate: %s %s", destination, destination.getId()));
            Log.d(TAG, String.format("onCreate: home nav %s", R.id.navigation_home));

            setFabState(destination.getId() == R.id.navigation_dashboard);
        });

        binding.fab.setOnClickListener(view -> {
            navigateTo(R.id.navigation_dashboard);
        });
    }

    private void navigateTo(final int navigationId) {
        if (navController.getCurrentDestination().getId() == navigationId) return;

        navController.navigate(navigationId, null);
    }

    private void setFabState(boolean active) {
        Drawable fabIcon = ContextCompat.getDrawable(this, R.drawable.ic_outline_map_24);

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
