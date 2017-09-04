package com.hexl.rxjava2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RxJava9 extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //同步
//        demo1();
        //异步
        demo2();
    }

    public void start(View view) {
    }

    public void request(View view) {
    }


    public void request() {
        mSubscription.request(128);
    }

    /**
     * 下游调用request(n) 告诉上游它的处理能力，
     * 上游每发送一个next事件之后，requested就减一，
     * 注意是next事件，complete和error事件不会消耗requested，
     * 当减到0时，则代表下游没有处理能力了，这个时候你如果继续发送事件，
     * 会发生什么后果呢？当然是MissingBackpressureException啦
     */
    private void demo1() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                Log.d(TAG, "e.requested():" + e.requested());
                e.onNext(1);
                Log.d(TAG, "e.requested():" + e.requested());
                e.onNext(2);
                Log.d(TAG, "e.requested():" + e.requested());
                e.onNext(3);
                Log.d(TAG, "e.requested():" + e.requested());
                e.onComplete();
                Log.d(TAG, "e.requested():" + e.requested());
            }
        }, BackpressureStrategy.ERROR)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        mSubscription = s;
//                        s.request(100);
                        s.request(2);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.d(TAG, "onError" + t.getLocalizedMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    private void demo2() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                Log.d(TAG, "e.requested():  " + e.requested());
            }
        }, BackpressureStrategy.ERROR).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        mSubscription = s;
                        s.request(1000);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext");
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


}
