package cn.klxx.multithread;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ForwardLee
 * @description 使用synchronized关键字，wait()和notify()方法实现阻塞队列
 */

public class MyBlockingQueue {

    //队列容器
    private final LinkedList<Object> list = new LinkedList<Object>();

    //队列大小,使用AtomicInteger可以保证在多线程获取队列大小时线程安全
    private AtomicInteger count = new AtomicInteger(0);

    //队列最大长度
    private int maxSize;

    //队列最小长度
    private int minSize = 0;

    //实现业务模型的锁
    private Object lock = new Object();

    public MyBlockingQueue(int length){
        this.maxSize = length;
    }

    /**
     * @description 队列存数据
     * @param obj
     */
    public void putObj(Object obj){
        synchronized (lock){
            if (count.get()==maxSize) {
                try {
                    lock.wait();//队列长度已经达到最大值，不能再继续存储数据，则线程处于等待状态，直至队列收到有可用位置的通知。
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            list.push(obj);
            count.incrementAndGet();
            lock.notify();//如果已经有线程等待获取队列数据，此时就通知取数据线程有数据了。
            System.out.println("存入对象："+obj);


        }
    }

    /**
     * @description 获取队列中的数据
     * @return 返回队列的第一个元素
     */
    public Object getObject(){
        Object retVal = null;
        synchronized (lock){
            if (count.get()==0) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            retVal = list.getFirst();
            count.decrementAndGet();
            lock.notify();//队列长度小于maxSize了，有存储空间了，就通知存储线程可以放入数据了。
            System.out.println("取出对象："+retVal);

        }

        return retVal;
    }

    public static void main(String[] args) {
        final MyBlockingQueue myBlockingQuene = new MyBlockingQueue(5);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Object var1 = myBlockingQuene.getObject();
            }
        },"t1").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                myBlockingQuene.putObj("abc1");
                myBlockingQuene.putObj("abc2");
                myBlockingQuene.putObj("abc3");
                myBlockingQuene.putObj("abc4");
                myBlockingQuene.putObj("abc5");
                myBlockingQuene.putObj("abc6");
                System.out.println("此时队列长度L1="+myBlockingQuene.count.get());
                myBlockingQuene.putObj("abc7");//这个对象放不了，线程就处于等待状态了。
            }
        },"t2").start();

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Object var2 = myBlockingQuene.getObject();//t2线程中的abc7对象此时才能放入队列。
                System.out.println("此时队列长度L2="+myBlockingQuene.count.get());
            }
        },"t3").start();


    }
}
