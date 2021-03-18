package net.inqer.touringapp;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainViewModel extends ViewModel {
    private static final String TAG = "MainViewModel";

    private final String testString2;

    @Inject
    public MainViewModel(@Named("String2") String testString2) {
        this.testString2 = testString2;
    }

//    init {
//        Log.d(TAG, String.format("init: injected string - %s", testString2));
//    }

    public String getTestString2() {
        return testString2;
    }
}
