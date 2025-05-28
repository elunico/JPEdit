package com.tom.jpedit.util;

public class FindCache {
    private int current;

    public FindCache(int current) {
        this.current = current;
    }

    public int getLastStop() {
        return current;
    }

    public void setLastStop(int lastStop) {
        this.current = lastStop;
    }
}
