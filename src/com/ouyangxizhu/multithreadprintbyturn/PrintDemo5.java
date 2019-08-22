package com.ouyangxizhu.multithreadprintbyturn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PrintDemo5 {

    private ReentrantLock lock;
    private volatile String name;

    public PrintDemo5(ReentrantLock reentrantLock, String name) {
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
        int threadNum = 4;
        int printTimes = 10;

        HashMap<Integer, ThreadHelper> threadHelperHashMap = new HashMap<>();
        ReentrantLock reentrantLock = new ReentrantLock();
        PrintDemo5 printDemo = new PrintDemo5(reentrantLock, "B");

        CreateConditionFactory conditionFactory = CreateConditionFactory.getInstance();
        conditionFactory.putCondition(threadNum, reentrantLock);

        ArrayList<Condition> conditions = new ArrayList<>();
        for (int i = 0; i < threadNum; i++) {
            conditions.add(conditionFactory.getCondition(i, reentrantLock));
        }

        List<String> printStrings = Arrays.asList("A", "C", "B", "D");
        ThreadHelperCreateFactory threadHelperCreateFactory = ThreadHelperCreateFactory.getInstance();
        threadHelperCreateFactory.putThreadHelper(conditions, printStrings);

        for (int i = 0; i < threadNum; i++) {
            ThreadHelper threadHelper = threadHelperCreateFactory.getThreadHelper(i, conditions.get(i));
            new Thread(()->{
                for (int j = 0; j < printTimes; j++) {
                    printDemo.loop(threadHelper);
                }
            }, threadHelper.getPrintString()).start();
        }

    }
}


