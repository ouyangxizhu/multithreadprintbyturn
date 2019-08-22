package com.ouyangxizhu.multithreadprintbyturn;

import java.util.concurrent.locks.Condition;

public class ThreadHelper {
    private ThreadHelper next;
    private Condition condition;
    private String printString;

    public ThreadHelper(Condition condition, String printString) {
        this.condition = condition;
        this.printString = printString;
    }

    public void setNext(ThreadHelper next) {
        this.next = next;
    }

    public ThreadHelper getNext() {
        return next;
    }

    public Condition getCondition() {
        return condition;
    }

    public String getPrintString() {
        return printString;
    }
}
