package com.hexl.rxjava2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hexl.rxjava2.api.Api;
import com.hexl.rxjava2.entity.LoginResponse;
import com.hexl.rxjava2.entity.RegisterResponse;
import com.hexl.rxjava2.net.RetrofitUtils;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * 可以将 RxJava1-9 代码 copy 过来运行
 */
public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        demo1();
//        login();
//        zip();
//        test();
//        flowable();
    }

    private void flowable() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                Log.d(TAG, "emitter 1");
                e.onNext(1);
                Log.d(TAG, "emitter 2");
                e.onNext(2);
                Log.d(TAG, "emitter 3");
                e.onNext(3);
                Log.d(TAG, "emitter 4");
                e.onNext(4);
                Log.d(TAG, "emitter onComplete");
                e.onComplete();
            }
        }, BackpressureStrategy.ERROR).subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                Log.d(TAG, "onSubscribe");
                s.request(Integer.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "onNext" + integer);
            }

            @Override
            public void onError(Throwable t) {
                Log.d(TAG, "onError");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        });
    }

    private void test() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Log.d(TAG, "Emitter 1");
                e.onNext(1);
                Log.d(TAG, "Emitter 2");
                e.onNext(2);
                Log.d(TAG, "Emitter 3");
                e.onNext(3);

            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "integer:" + integer);
            }
        });
    }

    private void zip() {
        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Log.d(TAG, "observable1 线程" + Thread.currentThread().getName());

                Log.d(TAG, "Emitter 1");
                e.onNext(1);
                Thread.sleep(1000);

                Log.d(TAG, "Emitter 2");
                e.onNext(2);
                Thread.sleep(1000);

                Log.d(TAG, "Emitter 3");
                e.onNext(3);
                Thread.sleep(1000);

                Log.d(TAG, "Emitter 4");
                e.onNext(4);
                Thread.sleep(1000);

                Log.d(TAG, "Emitter onComplete 1");
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                Log.d(TAG, "observable2 线程" + Thread.currentThread().getName());

                Log.d(TAG, "Emitter A");
                e.onNext("A");
                Thread.sleep(1000);

                Log.d(TAG, "Emitter B");
                e.onNext("B");
                Thread.sleep(1000);

                Log.d(TAG, "Emitter C");
                e.onNext("C");
                Thread.sleep(1000);

                Log.d(TAG, "Emitter onComplete 2");
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(Integer integer, String s) throws Exception {
                return integer + s;
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
                        Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * flatMap
     */
    private void demo1() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                Log.d(TAG, "Emitter  1");

                e.onNext(2);
                Log.d(TAG, "Emitter  2");

                e.onNext(3);
                Log.d(TAG, "Emitter  3");

            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                ArrayList<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("I am value " + integer);
                }

                return Observable.fromIterable(list).delay(10, TimeUnit.SECONDS);
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String value) {
                Log.d(TAG, "onNext" + value);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void start(View view) {
    }

    public void request(View view) {
    }


}
