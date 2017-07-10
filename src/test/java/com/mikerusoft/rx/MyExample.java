package com.mikerusoft.rx;

import org.junit.Test;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

/**
 * @author Grinfeld Mikhail
 * @since 7/10/2017
 */
public class MyExample {

    @Test
    public void testme() throws Exception {
        HeapDumper hd = new HeapDumper();
        hd.start();
        Thread.sleep(5000L);

        hd.cancel();

        System.out.println("Canceled");

        Thread.sleep(5000L);
        System.out.println("Again");
        hd.start();
        Thread.sleep(5000L);
    }


    public static class HeapDumper {
        private long lastDump;
        private Subscription subscribe;


        public void start() {
            subscribe =
                Observable.interval(1, TimeUnit.SECONDS).skipWhile(aLong -> System.currentTimeMillis() - lastDump < 3000L)
                        .take(3)
                        .subscribe(
                    i -> Observable.defer(() -> Observable.just(heapDum())).subscribeOn(Schedulers.io()).doOnError(Throwable::printStackTrace).subscribe()
            );
        }


        public boolean heapDum() {
            System.out.println(Thread.currentThread().getName() + " HeapEd DumpEd");
            return false;
        }

        public void cancel() {
            subscribe.unsubscribe();
        }

    }
}
