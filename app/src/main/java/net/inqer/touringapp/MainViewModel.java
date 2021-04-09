package net.inqer.touringapp;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainViewModel extends ViewModel {
    private static final String TAG = "MainViewModel";

    @Inject
    public MainViewModel() {

    }

//    init {
//        Log.d(TAG, String.format("init: injected string - %s", testString2));
//    }

}
