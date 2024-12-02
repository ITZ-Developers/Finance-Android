package com.finance.ui.main;

import com.finance.MVVMApplication;
import com.finance.data.Repository;
import com.finance.ui.base.BaseViewModel;
import com.finance.utils.NetworkUtils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends BaseViewModel {
    public MainViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
        getSettings();
        getProfile();
    }

    public void getProfile(){
        showLoading();
        compositeDisposable.add(repository.getApiService().getProfile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(throwable ->
                        throwable.flatMap((Function<Throwable, ObservableSource<?>>) throwable1 -> {
                            if (NetworkUtils.checkNetworkError(throwable1)) {
                                return application.showDialogNoInternetAccess();
                            }else{
                                return Observable.error(throwable1);
                            }
                        })
                )
                .subscribe(
                        response -> {
                            if(response.isResult()){
                                repository.getSharedPreferences().savePermissions(response.getData().getGroup().getPermissions());
                                repository.setAccount(response.getData());
                                hideLoading();
                            }else{
                                showErrorMessage(response.getMessage());
                            }
                        },
                        throwable -> {
                            hideLoading();
                            showErrorMessage(throwable.getMessage());
                        }
                ));
    }

    public void getSettings(){
        showLoading();
        compositeDisposable.add(repository.getApiService().getSettings()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(throwable ->
                        throwable.flatMap((Function<Throwable, ObservableSource<?>>) throwable1 -> {
                            if (NetworkUtils.checkNetworkError(throwable1)) {
                                return application.showDialogNoInternetAccess();
                            }else{
                                return Observable.error(throwable1);
                            }
                        })
                )
                .subscribe(
                        response -> {
                            if(response.isResult()){
                                hideLoading();
                            }
                        },
                        throwable -> {
                            hideLoading();
//                            showErrorMessage(throwable.getMessage());
                        }
                ));
    }

}
