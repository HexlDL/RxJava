package com.hexl.rxjava2;

/**
 * Company: 
 * <p>
 * Description:
 *
 * @author hexl
 * @date 2017/8/29 on 11:03
 */

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

/**
 * DROP 与 LATEST 策略的不同处 (发送10000个事件)
 * 当上游发送事件给下游后,去调用 request 方法,默认执行128个事件
 * DROP   只能获取到最新的事件,后面的将被舍弃
 * LATEST 总是能获取到最后最新的事件, 例如这里我们总是能获得最后一个事件9999.
 */
public class RxJava8 extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public void start(View view) {
//        drop();
        latest();
    }

    public void request(View view) {
        request();
    }

    public void request() {
        mSubscription.request(128);
    }

    /**
     * DROP 策略
     */
    private void drop() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 10000; i++) {
                    e.onNext(i);
                }
            }
        }, BackpressureStrategy.DROP).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        mSubscription = s;
                        mSubscription.request(128);
                        Log.d(TAG, "mSubscription");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "integer:" + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.d(TAG, t.getLocalizedMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "complete");
                    }
                });
    }

    /**
     * LATEST 策略
     */
    private void latest() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 10000; i++) {
                    e.onNext(i);
                }
            }
        }, BackpressureStrategy.LATEST).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        mSubscription = s;
                        mSubscription.request(128);
                        Log.d(TAG, "mSubscription");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "integer:" + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.d(TAG, t.getLocalizedMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "complete");
                    }
                });
    }
}
