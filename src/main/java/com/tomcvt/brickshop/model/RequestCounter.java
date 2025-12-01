package com.tomcvt.brickshop.model;

public class RequestCounter {
    private int count;
    private long windowStartMillis;

    public RequestCounter() {
        this.count = 1;
        this.windowStartMillis = System.currentTimeMillis();
    }

    public int getCount() {
        return count;
    }
    public void increment() {
        this.count++;
    }

    public long getWindowStartMillis() {
        return windowStartMillis;
    }

    public void resetWindow() {
        this.count = 1;
        this.windowStartMillis = System.currentTimeMillis();
    }
}
