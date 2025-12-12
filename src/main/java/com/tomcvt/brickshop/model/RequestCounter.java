package com.tomcvt.brickshop.model;

public class RequestCounter {
    private int count;
    private long windowStartMillis;
    private int smallCount;
    private long banWindowStartMillis;

    public RequestCounter() {
        this.count = 1;
        this.windowStartMillis = System.currentTimeMillis();
        this.smallCount = 1;
        this.banWindowStartMillis = System.currentTimeMillis();
    }

    public int getCount() {
        return count;
    }
    public int getSmallCount() {
        return smallCount;
    }
    public void increment() {
        this.count++;
        this.smallCount++;
    }

    public long getWindowStartMillis() {
        return windowStartMillis;
    }

    public long getBanWindowStartMillis() {
        return banWindowStartMillis;
    }

    public void resetWindow() {
        this.count = 1;
        this.windowStartMillis = System.currentTimeMillis();
    }

    public void resetBanWindow() {
        this.smallCount = 1;
        this.banWindowStartMillis = System.currentTimeMillis();
    }
}
