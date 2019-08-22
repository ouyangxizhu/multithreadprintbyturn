package com.ouyangxizhu.multithreadprintbyturn;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CreateConditionFactory {
    private static volatile CreateConditionFactory instance = null;
    private ConcurrentHashMap<String, Condition> conditionConcurrentHashMap;

    private CreateConditionFactory() {
        conditionConcurrentHashMap = new ConcurrentHashMap();
    }

    public static CreateConditionFactory getInstance() {
        if (instance == null) {
            synchronized (CreateConditionFactory.class) {
                if (instance == null) {
                    instance = new CreateConditionFactory();
                }
            }
        }
        return instance;
    }

    public void putCondition(int num, ReentrantLock reentrantLock) {
        for (int i = 0; i < num; i++) {
            Condition condition = reentrantLock.newCondition();
            String key = getKey(i, reentrantLock);
            conditionConcurrentHashMap.put(key, condition);
        }
    }

    public Condition getCondition(int num, ReentrantLock reentrantLock) {
        String key = getKey(num, reentrantLock);
        return conditionConcurrentHashMap.get(key);
    }

    private String getKey(int num, ReentrantLock reentrantLock) {
        return reentrantLock.toString() + num;
    }
}
