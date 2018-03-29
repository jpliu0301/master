package com.zlj.juc;

/**
 * 一、Volatile关键字：当多个线程进行操作共享数据时，可以保证内存中的数据时可见的。
 *                 相较与synchronized是一种较为轻量级的同步策略。
 *                 
 * 注意：
 * 1、volatile:不具备"互斥性",即如果有多个线程同时抢占一个资源，如果有一个线程抢占了资源，其他线程只能处于等待状态;
 * 2、volatile:不能保证变量的"原子性"
 * 
 * @author bashen
 *
 */
public class TestVolatile {
	public static void main(String[] args) {
		ThreadDemo td = new ThreadDemo();
		new Thread(td).start();
		
		while(true) {
			if(td.isFlag()) {
				System.out.println("===========");
				break;
			}
		}
	}
}

class ThreadDemo implements Runnable {
	private volatile boolean flag = false;

	@Override
	public void run() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		flag = true;
		System.out.println("flag = " + isFlag());
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}
}
