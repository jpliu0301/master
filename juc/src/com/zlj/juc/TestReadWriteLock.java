package com.zlj.juc;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
 * ReadWriteLock：读写锁
 * 
 * 写写 / 读写  -> 需要"互斥"
 * 读读  -> 不需要"互斥"
 */
public class TestReadWriteLock {
	public static void main(String[] args) {
		ReadWriteLockDemo readWriteLockDemo = new ReadWriteLockDemo();
		new Thread(new Runnable() {
			@Override
			public void run() {
				readWriteLockDemo.set((int)(Math.random() * 100) + 1);
			}
		},"write:").start();
		
		for (int i = 0; i < 100; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					readWriteLockDemo.get();
				}
			},"read:").start();
		}
	}
}

class ReadWriteLockDemo {
	private int number = 0;
	private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	
	//读
	public void get() {
		readWriteLock.readLock().lock();   //加锁
		try {
			System.out.println(Thread.currentThread().getName() + ": " + number);
		} finally {
			readWriteLock.readLock().unlock();  //释放锁
		}
	}
	
	//写 
	public void set(int number) {
		readWriteLock.writeLock().lock();  //加锁
		try {
			System.out.println(Thread.currentThread().getName() + " , 设置的值为：" + number);
			this.number = number;
		} finally {
			readWriteLock.writeLock().unlock();   //释放锁
		}
	}
}
