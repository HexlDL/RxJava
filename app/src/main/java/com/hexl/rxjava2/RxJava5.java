package com.hexl.rxjava2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Company: 辽宁众信同行软件开发有限公司
 * <p>
 * Description: 遗留了一个问题,在异步线程时上游不断发送事件,就会导致 oom
 * 这个问题将到 RxJava6 解决
 * <p>
 *
 * @author hexl
 * @date 2017/8/28 on 09:39
 */
public class RxJava5 extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        zip();
        zip2();
    }

    /**
     * 这个就不会导致 oom
     * 因为zip() 是运行在不同线程中的.(异步)
     * 而 zip2() 是运行在同一线程中的.(同步)
     * 异步:上游不会管下游是否执行完成,会一直发送事件,所以导致 oom
     * 同步:上游发送事件一次后,下游就会执行一次
     */
    private void zip2() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; ; i++) {
                    e.onNext(i);
                }
            }
        })/*.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
          */.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Thread.sleep(2000);
                Log.d(TAG, "integer:" + integer);
            }
        });
    }

    /**
     * 这个会运行后会导致 oom
     * 在 RxJava6 中会解决这个问题
     */
    private void zip() {
        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; ; i++) {
                    e.onNext(i);
                }
            }
        }).subscribeOn(Schedulers.io());

        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("A");
            }
        }).subscribeOn(Schedulers.io());


        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(Integer integer, String s) throws Exception {
                return integer + s;
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d(TAG, "throwable:" + throwable);
            }
        });
    }
}
