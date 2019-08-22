package com.ouyangxizhu.multithreadprintbyturn;

import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PrintDemo3 {

    private static ReentrantLock lock;
    private volatile String name;

    public PrintDemo3(ReentrantLock reentrantLock, String name) {
        this.lock = reentrantLock;
        this.name = name;

    }

    public void loop(ThreadHelper threadHelper) {
        lock.lock();
        try {

            if (!name.equals(Thread.currentThread().getName())) {
                threadHelper.getCondition().await();
            }
            System.out.print(Thread.currentThread().getName());
            this.name = threadHelper.getNext().getPrintString();
            threadHelper.getNext().getCondition().signal();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();//释放锁
        }
    }

    public static void main(String[] args) {
        int threadNum = 3;
        int printTimes = 10;

        HashMap<Integer, ThreadHelper> threadHelperHashMap = new HashMap<>();
        ReentrantLock reentrantLock = new ReentrantLock();
        PrintDemo3 printDemo = new PrintDemo3(reentrantLock, "B");

        Condition c1 = reentrantLock.newCondition();
        Condition c2 = reentrantLock.newCondition();
        Condition c3 = reentrantLock.newCondition();

        ThreadHelper a = new ThreadHelper(c1, "A");
        ThreadHelper b = new ThreadHelper(c2, "B");
        ThreadHelper c = new ThreadHelper(c3, "C");

        a.setNext(b);
        b.setNext(c);
        c.setNext(a);

        threadHelperHashMap.put(0, a);
        threadHelperHashMap.put(1, b);
        threadHelperHashMap.put(2, c);

        for (int i = 0; i < threadNum; i++) {
            ThreadHelper threadHelper = threadHelperHashMap.get(i);
            new Thread(()->{
                for (int j = 1; j <= printTimes; j++) {
                    printDemo.loop(threadHelper);
                }
            }, threadHelper.getPrintString()).start();
        }

    }
}


