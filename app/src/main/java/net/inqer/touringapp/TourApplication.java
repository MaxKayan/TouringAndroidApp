package net.inqer.touringapp;

import android.app.Application;
import android.provider.FontRequest;

import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class TourApplication extends Application {
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
    }
}
