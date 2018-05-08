package cn.klxx.multithread;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Object方法wait()、notify()两个方法需要配合sychronized关键字使用，
 * wait()方法释放锁，notify()方法占有锁，所以要先调用wait()方法，再调用notify()方法
 */
public class MyCountDownLatch {

static List list = new ArrayList();

public void addString(String a){
    list.add(a);
    System.out.println("list add string!");
}

public int getSize(){
    return list.size();
}

static final Object lock = new Object();

    public static void main(String[] args) {

        final MyCountDownLatch myCountDownLatch = new MyCountDownLatch();

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (lock){
                        for (int i = 0; i < 10; i++) {
                            myCountDownLatch.addString("abc");
                            Thread.sleep(500);
                            if(list.size()==5){
                                lock.notify();
                                System.out.println("发出通知...");
                            }
                        }
                        System.out.println("执行完这句话释放锁...");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"t1") ;

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (lock) {
                        if (list.size()!=5) {
                            lock.wait();//此时t2处于等待中，不再继续往下执行代码，直到收到通知才继续执行
                        }
                        System.out.println("t2 收到停止通知");
                        throw new RuntimeException();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },"t2") ;
        /**
         * t2必须先启动，因为wait方法不会占有锁，不影响t1执行；如果t1先启动会占有锁，而t2则不能使用锁无法执行。
         * 用wait和notify配合关键字sychronized关键字，使用等notify所在的sychronized关键字代码块执行完，
         * wait所在的sychronized代码块才会继续执行，这样的最大问题就是收到通知的时间严重滞后于发送通知的时间。
         * 所以要借助CountDownLatch来解决这个问题，CountDownLatch使用countDown方法发送完通知后也会继续执行，
         * 但是await方法会马上收到通知继续执行代码，避免了收到通知的严重滞后问题。
         */
        t2.start();
        t1.start();

        try {
            Thread.sleep(10000);
            list.clear();
            System.out.println("-----------------------------");
            System.out.println("list.size:"+list.size());
            System.out.println("-----------------------------");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 10; i++) {
                        list.add("abc");
                        System.out.println("list add string!");
                        Thread.sleep(500);
                        if (list.size()==5) {
                            System.out.println("发送通知");
                            countDownLatch.countDown();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "t3");

        Thread t4 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (list.size()!=5) {
                        System.out.println("t4 等待...");
                        countDownLatch.await();
                    }
                    System.out.println("list.size:"+list.size()+" ,t4 停止");
                    throw new RuntimeException();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "t4");

        t3.start();
        t4.start();


    }

}
