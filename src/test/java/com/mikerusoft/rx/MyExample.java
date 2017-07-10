package com.mikerusoft.rx;

import org.junit.Test;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
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
        System.out.println("Changing lastDump");
        hd.setLastDump(System.currentTimeMillis());
        Thread.sleep(5000L);
    }


    public static class HeapDumper {
        private long lastDump;
        private Subscription subscribe;


        public void start() {
            subscribe =
                // we could move Scheduler to interval method
                Observable.interval(1, TimeUnit.SECONDS, Schedulers.io())
                    .map(tick -> {
                        System.out.println("Emitted tick: " + tick);
                        return tick;
                    })
                    // skipWhile is not suitable for us, since it's not recurring action.
                    // It skips until condition true first time and then it doesn't check it,
                    // so let's use simple filter, by checking that last update was more then 3 seconds ago
                    // and it's 3rd tick
                    .filter(tick -> System.currentTimeMillis() - lastDump >= 3000L && tick % 3 == 0)
                    // after filter was passed we'll do action only 3 times
                    .take(3)
                    // convert i into boolean which is returned by performing heap dump
                    .map(i -> {
                        System.out.println("Tick: " + i); return heapDum(); })
                    .subscribe();
        }

        public void setLastDump(long lastDump) {
            this.lastDump = lastDump;
        }

        public boolean heapDum() {
            System.out.println(Thread.currentThread().getName() + " HeapEd DumpEd");
            return true;
        }

        public void cancel() {
            subscribe.unsubscribe();
        }

    }
}
