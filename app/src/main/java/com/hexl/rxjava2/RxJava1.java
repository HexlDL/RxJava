package com.hexl.rxjava2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Company: 辽宁众信同行软件开发有限公司
 * <p>
 * Description: 初次学习了解 RxJava
 *              注意: 只有当上游和下游建立连接之后, 上游才会开始发送事件.
 *              也就是调用了subscribe() 方法之后才开始发送事件.
 *
 * @author hexl
 * @date 2017/8/27 on 20:45
 */
public class RxJava1 extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    /*//创建一个上游 Observable
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        });

        //创建一个下游 Observer
        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "subscribe");
            }

            @Override
            public void onNext(Integer value) {
                Log.d(TAG, "value:  " + value);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "error");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };

        //建立连接
        observable.subscribe(observer);*/

        //RxJava 的链式调用写法
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                // OnComplete 事件建议只发送一次,如多次发送,
                // 不会执行第二次发送事件,
                // 在 OnComplete 事件后发送的事件 Observer是接收不到的
                Log.d(TAG, "emit 1");
                e.onNext(1);
                Log.d(TAG, "emit 2");
                e.onNext(2);
                Log.d(TAG, "emit 3");
                e.onNext(3);
                Log.d(TAG, "emit Complete");
                e.onComplete();
                Log.d(TAG, "emit 4");
                e.onNext(4);
                Log.d(TAG, "emit 5");
                e.onNext(5);
//                e.onComplete();
            }
        }).subscribe(new Observer<Integer>() {

            private Disposable mDisposable;
            private int i;

            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "subscribe");
                mDisposable = d;
            }

            @Override
            public void onNext(Integer value) {
                Log.d(TAG, "value:  " + value);
                i++;
                if (i == 2) {
                    Log.d(TAG, "disposable");
                    mDisposable.dispose();
                    Log.d(TAG, "is Disposed :  " + mDisposable.isDisposed());
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "error");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        });

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("HelloWorld");
                e.onComplete();
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        });
    }

}
