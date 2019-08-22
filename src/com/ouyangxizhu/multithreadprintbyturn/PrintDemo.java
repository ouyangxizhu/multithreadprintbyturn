package com.ouyangxizhu.multithreadprintbyturn;

import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PrintDemo {
    private static ReentrantLock lock;
    private volatile Character numChar;
    private int threadNum;

    public PrintDemo(ReentrantLock reentrantLock,  Character numChar, int threadNum) {
        this.lock = reentrantLock;
        this.numChar = numChar;
        this.threadNum = threadNum;
    }


    public void loop(String name, Condition waitCondition, Condition signalCondition) {
        lock.lock();
        try {
            String curName = numChar + "";
            if (!curName.equals(Thread.currentThread().getName())) {
                waitCondition.await();
            }
            System.out.print(Thread.currentThread().getName());
            this.numChar = (char)((numChar - 'A' + 1)% threadNum + 'A');
            signalCondition.signal();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();//释放锁
        }
    }

    public static void main(String[] args) {
        int threadNum = 4;
        int printTimes = 10;

        HashMap<Integer, Condition> conditionHashMap = new HashMap<>();
        ReentrantLock reentrantLock = new ReentrantLock();
        PrintDemo printDemo = new PrintDemo(reentrantLock, 'A', threadNum);

        Condition c1 = reentrantLock.newCondition();
        Condition c2 = reentrantLock.newCondition();
        Condition c3 = reentrantLock.newCondition();
        Condition c4 = reentrantLock.newCondition();

        conditionHashMap.put(0, c1);
        conditionHashMap.put(1, c2);
        conditionHashMap.put(2, c3);
        conditionHashMap.put(3, c4);

        for (int i = 0; i < threadNum; i++) {
            char ch = (char) ('A' + i);
            String name = ch + "";
            Condition cur = conditionHashMap.get((i + threadNum)%threadNum);
            Condition next = conditionHashMap.get((i + 1 + threadNum)%threadNum);

            new Thread(()->{
                for (int j = 1; j <= printTimes; j++) {
                    printDemo.loop(name, cur, next);
                }
            }, name).start();
        }
    }
}



