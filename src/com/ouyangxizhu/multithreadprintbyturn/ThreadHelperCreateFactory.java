package com.ouyangxizhu.multithreadprintbyturn;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;

public class ThreadHelperCreateFactory {
    private static volatile ThreadHelperCreateFactory instance = null;
    private ConcurrentHashMap<String, ThreadHelper> threadHelperConcurrentHashMap;

    private ThreadHelperCreateFactory() {
        threadHelperConcurrentHashMap = new ConcurrentHashMap();
    }

    public static ThreadHelperCreateFactory getInstance() {
        if (instance == null) {
            synchronized (ThreadHelperCreateFactory.class) {
                if (instance == null) {
                    instance = new ThreadHelperCreateFactory();
                }
            }
        }
        return instance;
    }

    public void putThreadHelper(List<Condition> conditionList, List<String> printStringList) {
        List<ThreadHelper> threadHelperList = new ArrayList<>();
        for (int i = 0; i < conditionList.size(); i++) {
            ThreadHelper threadHelper = new ThreadHelper(conditionList.get(i), printStringList.get(i));
            threadHelperList.add(threadHelper);
            String key = getKey(i, conditionList.get(i));
            threadHelperConcurrentHashMap.put(key, threadHelper);
        }
        for (int i = 0; i < threadHelperList.size(); i++) {
            threadHelperList.get(i).setNext(threadHelperList.get((i + 1)% threadHelperList.size()));
        }
    }

    public ThreadHelper getThreadHelper(int num, Condition condition) {
        String key = getKey(num, condition);
        return threadHelperConcurrentHashMap.get(key);
    }

    private String getKey(int num, Condition condition) {
        return condition.toString() + num;
    }
}
