package com.hexl.rxjava2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Company: 辽宁众信同行软件开发有限公司
 * <p>
 * Description: 主要学习了 Flowable 操作符
 *              解决在异步线程中上游发送事件下游可以接收到不会导致 oom
 * <p>
 * @author hexl
 * @date 2017/8/28 on 09:39
 */
public class RxJava7 extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //同步
//        flowable();
        //异步
        asyncFlowable();
    }

    private void asyncFlowable() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
               /*
                Log.d(TAG, "emit 1");
                emitter.onNext(1);
                Log.d(TAG, "emit 2");
                emitter.onNext(2);
                Log.d(TAG, "emit 3");
                emitter.onNext(3);
                Log.d(TAG, "emit complete");
                emitter.onComplete();
                */
                //上游连续发送128个事件
                for (int i = 0; i < 128; i++) {
                    Log.d(TAG, "i:       " + i);
                    emitter.onNext(i);
                }
            }
        }, BackpressureStrategy.ERROR).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        /*
                         * 这次没有调用 Subscription 的 request 方法也没有抛出异常,
                         * 并且上游发送的事件下游也没有接收到
                         */
                        mSubscription = s;
                        mSubscription.request(128);
                    }

                    @Override
                    public void onNext(Integer s) {
                        Log.d(TAG, "" + s);
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


    /**
     * flowable 操作符
     */
    private void flowable() {
        Flowable<Integer> upStream = Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "emit 1");
                emitter.onNext(1);
                Log.d(TAG, "emit 2");
                emitter.onNext(2);
                Log.d(TAG, "emit 3");
                emitter.onNext(3);
                Log.d(TAG, "emit complete");
                emitter.onComplete();
            }
        }, BackpressureStrategy.ERROR);


        Subscriber<Integer> downStream = new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                Log.d(TAG, "onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer s) {
                Log.d(TAG, "" + s);
            }

            @Override
            public void onError(Throwable t) {
                Log.d(TAG, "onError");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };

        upStream.subscribe(downStream);
    }

}