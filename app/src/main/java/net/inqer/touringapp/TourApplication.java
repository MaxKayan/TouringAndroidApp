package net.inqer.touringapp;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import net.inqer.touringapp.data.models.TourRoute;
import net.inqer.touringapp.di.qualifiers.ActiveTourRouteLiveData;
import net.inqer.touringapp.service.RouteService;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class TourApplication extends Application {
    private static final String TAG = "TourApplication";

    @Inject
    @ActiveTourRouteLiveData
    LiveData<TourRoute> activeRoute;

    @Override
    public void onCreate() {
        super.onCreate();
//        EmojiCompat.init(
//                FontRequestEmojiCompatConfig(
//                        this,
//                        FontRequest(
//                                "com.google.android.gms.fonts",
//                                "com.google.android.gms",
//                                "Noto Color Emoji Compat",
//                        )
//                )
//        )

        subscribeObservers();
    }

    private void subscribeObservers() {
        activeRoute.observeForever(tourRoute -> {
            Log.d(TAG, "subscribeObservers: " + tourRoute);
            if (tourRoute != null) {
                RouteService.Companion.startService(this, "Запускаем следование маршруту...");
            } else {
                RouteService.Companion.stopService(this);
            }
        });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
