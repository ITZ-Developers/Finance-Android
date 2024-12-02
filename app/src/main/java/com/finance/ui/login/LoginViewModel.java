package com.finance.ui.login;


import android.content.Intent;

import androidx.databinding.ObservableField;

import com.finance.MVVMApplication;
import com.finance.R;
import com.finance.data.Repository;
import com.finance.data.model.api.request.login.LoginRequest;
import com.finance.ui.base.BaseViewModel;
import com.finance.ui.main.MainActivity;
import com.finance.utils.NetworkUtils;

import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;

public class LoginViewModel extends BaseViewModel {
    public ObservableField<Boolean> isShowPassword = new ObservableField<>(false);
    public ObservableField<String> username = new ObservableField<>();
    public ObservableField<String> password = new ObservableField<>();

    public LoginViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public void doLogin(){
        if (username.get() == null || Objects.requireNonNull(username.get()).trim().isEmpty()){
            showErrorMessage(application.getString(R.string.invalid_username_login));
            return;
        }
        if (password.get()== null || Objects.requireNonNull(password.get()).trim().isEmpty()){
            showErrorMessage(application.getString(R.string.password_can_not_blank));
            return;
        }
        if (Objects.requireNonNull(username.get()).length() < 2 ){
            showErrorMessage(application.getString(R.string.invalid_length_username));
            return;
        }
        if (Objects.requireNonNull(username.get()).length() < 2 ){
            showErrorMessage(application.getString(R.string.invalid_length_username));
            return;
        }
        if (Objects.requireNonNull(username.get()).contains(" "))  {
            showErrorMessage(application.getString(R.string.invalid_username_space));
            return;
        }
        if (Objects.requireNonNull(password.get()).contains(" "))  {
            showErrorMessage(application.getString(R.string.invalid_password_space));
            return;
        }
        if (!Objects.requireNonNull(username.get()).matches("^[a-zA-Z0-9_]*$") ) {
            showErrorMessage(application.getString(R.string.invalid_username_special_characters));
            return;
        }
        if (Objects.requireNonNull(username.get()).length() > 20 ) {
            showErrorMessage(application.getString(R.string.invalid_length_username_2));
            return;
        }
        if (Objects.requireNonNull(password.get()).length() < 6 ){
            showErrorMessage(application.getString(R.string.invalid_length_password));
            return;
        }
        showLoading();
        compositeDisposable.add(repository.getApiService().login(new LoginRequest(username.get(), password.get(), "password"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(throwable ->
                        throwable.flatMap((Function<Throwable, ObservableSource<?>>) throwable1 -> {
                            if (NetworkUtils.checkNetworkError(throwable1)) {
                                hideLoading();
                                return application.showDialogNoInternetAccess();
                            }else{
                                return Observable.error(throwable1);
                            }
                        })
                )
                .subscribe(
                        response -> {
                            repository.setToken(response.getAccess_token());
                            Intent intent = new Intent(application.getCurrentActivity(), MainActivity.class);
                            application.getCurrentActivity().startActivity(intent);
                            application.getCurrentActivity().finish();
                            showSuccessMessage(application.getString(R.string.login_successfully));
                            hideLoading();
                        }, throwable -> {
                            if(throwable instanceof HttpException){
                                if(((HttpException) throwable).code()==400){
                                    showErrorMessage(application.getString(R.string.incorrect_username_or_password));
                                }
                            }else {
                                showErrorMessage(application.getResources().getString(R.string.no_internet));
                            }
                            hideLoading();
                        }));
    }

    public void showPassword(){
        isShowPassword.set(Boolean.FALSE.equals(isShowPassword.get()));
    }

}
