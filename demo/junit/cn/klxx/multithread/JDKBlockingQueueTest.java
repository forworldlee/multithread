package cn.klxx.multithread;

import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;


/**
 * @description JDK实现的阻塞和无阻塞队列测试
 * @author ForwardLee
 */
public class JDKBlockingQueueTest {
    /**
     * 有界阻塞队列
     */
    @Test
    public void testArrayBlockingQueue() throws Exception {
        ArrayBlockingQueue<String> arrayBlockingQueue = new ArrayBlockingQueue<String>(5);
        arrayBlockingQueue.add("a");
        arrayBlockingQueue.add("b");
        arrayBlockingQueue.add("c");
        arrayBlockingQueue.add("d");
        arrayBlockingQueue.add("e");
        arrayBlockingQueue.add("f");
    }

    /**
     * @descripiton 可以是有界阻塞队列，也可以是无解阻塞队列，关键看实例化队列对象用的构造方法是否初始化队列大小
     */
    @Test
    public void testLinkedBlockingDeque() throws Exception {

        LinkedBlockingDeque<String> linkedBlockingDeque = new LinkedBlockingDeque<String>();//new LinkedBlockingDeque<String>(2)
        linkedBlockingDeque.add("a");
        linkedBlockingDeque.offer("b");
        linkedBlockingDeque.add("c" );
    }

    /**
     * @description 同步阻塞队列，主要应用场景在于多线程之间的线程切换，例如线程池的实现。
     * 不能直接往队列中存数据。要往队列中存数据，前提是必须要有一个线程等待取数据
     * Executors.newCachedThreadPool()
     *
     * Creates a thread pool that creates new threads as needed, but
     * will reuse previously constructed threads when they are
     * available, and uses the provided
     * ThreadFactory to create new threads when needed.
     * {@param threadFactory the factory to use when creating new threads
     * @return the newly created thread pool
     * @throws NullPointerException if threadFactory is null}
     *
     * public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
     * return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
     *             60L, TimeUnit.SECONDS,
     *               new SynchronousQueue<Runnable>(),
     *               threadFactory);
     * }
     */
    @Test
    public void testSynchronousQueue() throws Exception {

        final SynchronousQueue<String> synchronousQueue = new SynchronousQueue<String>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String peek = synchronousQueue.take();
                    System.out.println(peek);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            public void run() {
                synchronousQueue.add("a");
            }
        }).start();
    }

    /**
     * PriorityBlockingQueue是带优先级的无界阻塞队列，每次出队都返回优先级最高的元素，
     * 是二叉树最小堆的实现，研究过数组方式存放最小堆节点的都知道，直接遍历队列元素是无序的。
     * 队列存储的对象必须实现Comparable接口
     * @throws Exception
     */
    @Test
    public void testPriorityBlockingQueue() throws Exception {
        PriorityBlockingQueue<ObjectWithComparable>  priorityBlockingQueue= new PriorityBlockingQueue<ObjectWithComparable>();
        ObjectWithComparable o1 = new ObjectWithComparable();
        o1.setId(3);
        priorityBlockingQueue.add(o1);

        ObjectWithComparable o2 = new ObjectWithComparable();
        o2.setId(4);
        priorityBlockingQueue.add(o2);

        ObjectWithComparable o3 = new ObjectWithComparable();
        o3.setId(2);
        priorityBlockingQueue.add(o3);

        for (ObjectWithComparable objectWithComparable : priorityBlockingQueue) {
            System.out.println(priorityBlockingQueue.take());
        }

    }

    private class ObjectWithComparable implements Comparable<ObjectWithComparable>{
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public int compareTo(ObjectWithComparable o) {
            return this.id>o.id? 1:(this.id<o.id? -1:0);
        }

        @Override
        public String toString() {
            return "ObjectWithComparable{" +
                    "id=" + id +
                    '}';
        }
    }
}