package com.hexl.rxjava2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;

/**
 * Company: 辽宁众信同行软件开发有限公司
 * <p>
 * Description:zip 操作符
 * 最终下游收到的事件数量 是和上游中发送事件最少的那一根水管的事件数量 相同.
 * 这个也很好理解, 因为是从每一根水管 里取一个事件来进行合并, 最少的 那个肯定就最先取完 ,
 * 这个时候其他的水管尽管还有事件 , 但是已经没有足够的事件来组合了, 因此下游就不会收到剩余的事件了.
 *
 * @author hexl
 * @date 2017/8/28 on 08:55
 */
public class RxJava4 extends AppCompatActivity{
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        zip();
    }

    /**
     * zip发送的事件数量跟上游中发送事件最少的那一根水管的事件数量是有关的,
     * 在这个例子里
     * 第一根水管发送了四个事件和 onComplete 事件,
     * 第二根水管发送了三个事件和 onComplete 事件,
     * 这个时候第二根水管已经发现已经没有足够的事件进行组合了,因此下游就不会收到剩余的事件
     */
    private void zip() {
        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {

                Log.d(TAG, "emit 1");
                e.onNext(1);
                Thread.sleep(1000);

                Log.d(TAG, "emit 2");
                e.onNext(2);
                Thread.sleep(1000);

                Log.d(TAG, "emit 3");
                e.onNext(3);
                Thread.sleep(1000);

//                Log.d(TAG, "emit 4");
//                e.onNext(4);
//                Thread.sleep(1000);

//                Log.d(TAG, "emit onComplete  1");
//                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {

                Log.d(TAG, "emit A");
                e.onNext("A");
                Thread.sleep(1000);

                Log.d(TAG, "emit B");
                e.onNext("B");
                Thread.sleep(1000);

                Log.d(TAG, "emit C");
                e.onNext("C");
                Thread.sleep(1000);

                Log.d(TAG, "emit onComplete 2");
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(Integer integer, String s) throws Exception {
                return integer + s;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe");
            }

            @Override
            public void onNext(String value) {
                Log.d(TAG, "onNext : " + value);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        });
    }
}
