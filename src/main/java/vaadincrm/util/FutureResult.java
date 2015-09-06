package vaadincrm.util;

import diag.Watch;

import java.util.concurrent.*;

/**
 * Created by someone on 20/08/2015.
 */
final public class FutureResult<T> extends FutureTask<T> {
    private static final Object SS = "SS";
    private static final int COUNT = 100_000;

    public FutureResult() {
        super(() -> null);
    }

    public void signal(final T value) {
        set(value);
    }

    public void signalError(final Throwable throwable) {
        setException(throwable);
    }

    public static void main(String... args) throws Exception {
        final ArrayBlockingQueue<FutureResult> queue = new ArrayBlockingQueue<>(1000000);
        new Thread(() -> {
            Watch watch = new Watch().start();
            for (int i = 0; i < COUNT; i++) {
                try {
                    final FutureResult poll = queue.take();
//                    System.out.println("POLL: " + poll);
                    if (poll != null) {
                        poll.signal(SS);
//                        System.out.println("SIGNALLED: " + poll);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("RECEIVER: " + watch.end().elapsed());
        }).start();

        new Thread(() -> {
            final Watch watch = new Watch().start();
            for (int i = 0; i < COUNT; i++) {
                final FutureResult<Object> result = new FutureResult<>();
//                System.out.println("SENDING: " + i);
                queue.offer(result);
                try {
//                    System.out.println("SENT: " + i);
                    result.get();
//                    System.out.println("GOT : " + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("SENDER: " + watch.end().elapsed());
        }).start();
    }
}
