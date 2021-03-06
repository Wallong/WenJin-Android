package com.twt.service.wenjin.ui.login;

import android.text.TextUtils;

import com.twt.service.wenjin.R;
import com.twt.service.wenjin.WenJinApp;
import com.twt.service.wenjin.bean.UserInfo;
import com.twt.service.wenjin.interactor.LoginInteractor;
import com.twt.service.wenjin.support.JPushHelper;
import com.twt.service.wenjin.support.LogHelper;
import com.twt.service.wenjin.support.NetworkHelper;
import com.twt.service.wenjin.support.PrefUtils;
import com.twt.service.wenjin.support.ResourceHelper;

/**
 * Created by M on 2015/3/23.
 */
public class LoginPresenterImpl implements LoginPresenter, OnLoginCallback {

    private LoginView mLoginView;
    private LoginInteractor mLoginInteractor;

    public LoginPresenterImpl(LoginView loginView, LoginInteractor loginInteractor) {
        this.mLoginView = loginView;
        this.mLoginInteractor = loginInteractor;
    }

    @Override
    public void validateLogin(String username, String password) {
        mLoginView.hideKeyboard();
        if (!NetworkHelper.isOnline()) {
            mLoginView.toastMessage(ResourceHelper.getString(R.string.network_not_connected));
            return;
        }
        mLoginView.showProgressBar();
        if (TextUtils.isEmpty(username)) {
            mLoginView.usernameError(ResourceHelper.getString(R.string.login_error_empty));
            mLoginView.hideProgressBar();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            mLoginView.passwordError(ResourceHelper.getString(R.string.login_error_empty));
            mLoginView.hideProgressBar();
            return;
        }
        mLoginInteractor.login(username, password, this);
    }

    @Override
    public void onSuccess(UserInfo userInfo) {
        PrefUtils.setDefaultPrefUserInfo(userInfo);
        PrefUtils.setLogin(true);

        JPushHelper jPushHelper = new JPushHelper(String.valueOf(userInfo.uid),null);
        jPushHelper.setAlias();

        mLoginView.hideProgressBar();
        mLoginView.startMainActivity();
    }

    @Override
    public void onFailure(String errorString) {
        mLoginView.hideProgressBar();
        mLoginView.toastMessage(errorString);
    }
}
