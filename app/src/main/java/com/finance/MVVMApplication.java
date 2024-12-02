package com.finance;

import android.app.Application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.finance.di.component.AppComponent;
import com.finance.di.component.DaggerAppComponent;
import com.finance.others.MyTimberDebugTree;
import com.finance.others.MyTimberReleaseTree;
import com.finance.utils.DialogUtils;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import io.reactivex.rxjava3.subjects.PublishSubject;
import lombok.Getter;
import lombok.Setter;
import timber.log.Timber;

public class MVVMApplication  extends Application implements LifecycleObserver{
    @Getter
    @Setter
    private AppCompatActivity currentActivity;

    @Setter
    @Getter
    private AppComponent appComponent;


    @Override
    public void onCreate() {
        super.onCreate();

        // Enable firebase log
        FirebaseCrashlytics firebaseCrashlytics = FirebaseCrashlytics.getInstance();
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(true);

        if (BuildConfig.DEBUG) {
            Timber.plant(new MyTimberDebugTree());
        } else {
            Timber.plant(new MyTimberReleaseTree(firebaseCrashlytics));
        }

        appComponent = DaggerAppComponent.builder()
                .application(this)
                .build();
        appComponent.inject(this);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }
    public PublishSubject<Integer> showDialogNoInternetAccess(){
        final PublishSubject<Integer> subject = PublishSubject.create();
        currentActivity.runOnUiThread(() ->
                DialogUtils.dialogConfirm(currentActivity, currentActivity.getResources().getString(R.string.newtwork_error),
                        currentActivity.getResources().getString(R.string.newtwork_error_button_retry),
                        (dialogInterface, i) -> subject.onNext(1), currentActivity.getResources().getString(R.string.newtwork_error_button_exit),
                        (dialogInterface, i) -> System.exit(0))
        );
        return subject;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        //App in background
        Timber.d("APP IN BACKGROUND");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        // App in foreground
        Timber.d("APP IN FOREGROUND");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onAppDestroy() {
        // destroy
        Timber.d("APP DESTROY");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onAppCreate() {
        // create
        Timber.d("APP CREATE");
    }

}
