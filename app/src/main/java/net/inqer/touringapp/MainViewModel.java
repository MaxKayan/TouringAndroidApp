package net.inqer.touringapp;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import net.inqer.touringapp.data.repository.main.MainRepository;
import net.inqer.touringapp.util.JavaContinuation;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlin.Unit;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

@HiltViewModel
public class MainViewModel extends ViewModel {
    private static final String TAG = "MainViewModel";

    private final MainRepository repository;

    @Inject
    public MainViewModel(
            MainRepository repository
    ) {
        this.repository = repository;
    }

    public void deactivateRoutes() {
        repository.deactivateRoutes(new JavaContinuation<Unit>() {
            @Override
            public void resume(Unit value) {
                Log.d(TAG, "resume: deactivated routes. " + value);
            }

            @Override
            public void resumeWithException(@NotNull Throwable exception) {
                Log.e(TAG, "resumeWithException: failed to deactivate routes!", exception);
            }

            @NotNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }
        });
    }

}
