package com.zlj.juc;

/**
 * 模拟CAS算法：
 * 
 * @author bashen
 *
 */
public class TestCompareAndSwap {
	public static void main(String[] args) {
		final CompareAndSwap cas = new CompareAndSwap();
		for (int i = 0; i < 10; i++) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
						int expectedValue = cas.get();   //获取预估值
						boolean flag = cas.compareAndSet(expectedValue, (int)(Math.random() * 10) + 1);
						System.out.println(flag);
					}				
			}).start();
		}
	}
}

class CompareAndSwap {
	private int value;   //内存值
	
	//获取内存值
	public synchronized int get() {
		return value;
	}
	
	//比较                                                                                                                    预估值                                              新值
	public synchronized int compareAndSwap(int expectedValue, int newValue) {
		int oldValue = this.value;
		if(oldValue == expectedValue) {
			this.value = newValue;
		}
		
		return oldValue;
	}
	
	//设置
	public synchronized boolean compareAndSet(int expectedValue, int newValue) {
		return expectedValue == compareAndSwap(expectedValue, newValue);
	}
}
