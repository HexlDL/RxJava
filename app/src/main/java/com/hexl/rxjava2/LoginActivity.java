package com.hexl.rxjava2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hexl.rxjava2.api.Api;
import com.hexl.rxjava2.entity.LoginResponse;
import com.hexl.rxjava2.entity.RegisterResponse;
import com.hexl.rxjava2.net.RetrofitUtils;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * 使用 Retrofit+RxJava 访问网络
 * (未完成)
 */
public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";

    private EditText mEtPhone;
    private EditText mEtPwd;

    private Api mApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEtPhone = (EditText) findViewById(R.id.et_phone);
        mEtPwd = (EditText) findViewById(R.id.et_pwd);

        Retrofit retrofit = RetrofitUtils.create();
        mApi = retrofit.create(Api.class);

    }

    /**
     * 登录
     *
     * @param view
     */
    public void login(View view) {
        /*
         * 网络请求
         */
        LoginResponse request = new LoginResponse();

        request.setUsername(mEtPhone.getText().toString());
        request.setUsername(mEtPwd.getText().toString());

        mApi.register(new RegisterResponse());


        mApi.login(request)
                .subscribeOn(Schedulers.io())//开启 io 线程访问网络数据
                .observeOn(AndroidSchedulers.mainThread())//切换到主线程更新 UI
                .subscribe(new Observer<LoginResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(LoginResponse value) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    }
                });

    }

}
