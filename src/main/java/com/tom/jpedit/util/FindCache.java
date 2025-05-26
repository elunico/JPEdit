package com.tom.jpedit.util;

public class FindCache {
  public FindCache(int current) {
    this.current = current;
  }

  private int current;

  public int getLastStop() {
    return current;
  }

  public void setLastStop(int lastStop) {
    this.current = lastStop;
  }
}
