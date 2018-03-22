package com.hexl.rxjava2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.hexl.rxjava2.api.Api;
import com.hexl.rxjava2.entity.LoginResponse;
import com.hexl.rxjava2.entity.RegisterResponse;
import com.hexl.rxjava2.net.RetrofitUtils;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * Company: 
 * <p>
 * Description: 学习 RxJava map flatMap concatMap 操作符
 *
 * @author hexl
 * @date 2017/8/27 on 23:39
 */
public class RxJava3 extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //转换类型
        map();
        //无序
        flatMap();
        //有序
        concatMap();

        //伪码,使用 flatMap 操作符实现注册后登录
//        login();
    }

    private void concatMap() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
        }).concatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                ArrayList<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("I am value " + integer);
                }
                return Observable.fromIterable(list).delay(10, TimeUnit.MILLISECONDS);

            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        });
    }

    /**
     * 上游每发送一个事件, flatMap都将创建一个新的水管,
     * 然后发送转换之后的新的事件,下游接收到的就是这些新的水管发送的数据.
     * 这里需要注意的是,flatMap并不保证事件的顺序
     * 如果需要保证顺序则需要使用concatMap.
     */
    private void flatMap() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                ArrayList<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("I am value " + integer);
                }
                return Observable.fromIterable(list).delay(10, TimeUnit.MILLISECONDS);

            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        });
    }

    /**
     * 在上游发送了个 int 类型的参数
     * 通过 map 操作符转换成 String 类型
     */
    private void map() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return "This is result " + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        });
    }


    private void login() {
        Retrofit retrofit = RetrofitUtils.create();
        final Api api = retrofit.create(Api.class);

        api.register(new RegisterResponse())
                .subscribeOn(Schedulers.io())//切换io 线程,请求网络
                .observeOn(AndroidSchedulers.mainThread())//切换到main 线程更新 UI
                .doOnNext(new Consumer<RegisterResponse>() {
                    @Override
                    public void accept(RegisterResponse registerResponse) throws Exception {
                        //注册逻辑操作
                    }
                })
                .observeOn(Schedulers.io())//再次切换到 io 线程请求登录接口
                .flatMap(new Function<RegisterResponse, ObservableSource<LoginResponse>>() {//不懂
                    @Override
                    public ObservableSource<LoginResponse> apply(RegisterResponse registerResponse) throws Exception {
                        return api.login(new LoginResponse());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//切换到main 线程更新 UI
                .subscribe(new Consumer<LoginResponse>() {
                    @Override
                    public void accept(LoginResponse loginResponse) throws Exception {
                        Toast.makeText(RxJava3.this, "登录成功", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(RxJava3.this, "登录失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
