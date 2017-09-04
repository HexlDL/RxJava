package com.hexl.rxjava2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Company: 辽宁众信同行软件开发有限公司
 * <p>
 * Description: 线程之间切换
 *
 * @author hexl
 * @date 2017/8/27 on 22:37
 */
public class RxJava2 extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Log.d(TAG, "Observable Thread.currentThread():   " + Thread.currentThread().getName());
                e.onNext(1);
            }
        });

        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "Observer Thread.currentThread():   " + Thread.currentThread().getName());
                Log.d(TAG, "OnNext integer:" + integer);
            }
        };

//        observable.subscribe(consumer);
        /*
         * subscribeOn 指定上游发送事件的线程
         * observerOn 指定下游发送事件的线程
         * 多次指定上游的线程只有第一次指定的有效,多次调用会被忽略掉
         * 多次指定下游的线程是可以的,每调用一次 observeOn下游的线程就会切换一次
         */
//        observable.subscribeOn(Schedulers.newThread())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .observeOn(Schedulers.io())
//                .subscribe(consumer);

        /*
         * 每调用一次 observeOn() 线程便会切换一次,
         * RxJava内置了很多线程选项供我们选择
         * 例如:
         *     -1,Schedulers.io() 代表 io 操作的线程,通常用于网络,读写文件等 io 密集型的操作
         *     -2,Schedulers.newThread() 代表开启一个子线程
         *     -3,Schedulers.computation() 代表 Cpu 计算密集型的操作,通常用于需要大量计算的操作
         *     -4,AndroidSchedulers.mainThread() 代表 Android 的主线程
         */
        observable.subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "After observeOn (mainThread), current thread is:  " + Thread.currentThread().getName());
                        Log.d(TAG, "one doOnNext integer:" + integer);
                    }
                })
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "After observeOn (io) ,current thread is: " + Thread.currentThread().getName());
                        Log.d(TAG, "two doOnNext integer:" + integer);
                    }
                })
                .subscribe(consumer);

    }

}
