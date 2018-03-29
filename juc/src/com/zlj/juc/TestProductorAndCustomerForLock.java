package com.zlj.juc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 * 生产者和消费者案例
 */
public class TestCustomerAndProductorForLock  {
	public static void main(String[] args) {
		Clerk clerk = new Clerk();
		Productor productor = new Productor(clerk);
		Customer customer = new Customer(clerk);
		new Thread(productor,"生产者A").start();
		new Thread(customer,"消费者B").start();
		new Thread(productor,"生产者C").start();
		new Thread(customer,"消费者D").start();
	}
}

//店员
class Clerk {
	private int product = 0;
	private Lock lock = new ReentrantLock();
	private Condition condition = lock.newCondition();
	
	//进货
	public void get() {
		lock.lock();
		try {
			//为了避免虚假唤醒的问题，this.wait()应该总是使用在循环中
			while(product >= 1) {
				System.out.println("货已满，不用再生产了！");
				try {
					condition.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println(Thread.currentThread().getName() + ": " + ++product);
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}
	
	//卖货
	public void sale() {
		lock.lock();
		try {
			while(product <= 0) {
				System.out.println("货不够了，需要生产！");
				try {
					condition.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println(Thread.currentThread().getName() + ": " + product--);
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}
}

//生产者
class Productor implements Runnable{
	private Clerk clerk;
	
	public Productor(Clerk clerk) {
        this.clerk = clerk;
	}

	@Override
	public void run() {
		for (int i = 0; i < 20; i++) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			clerk.get();
		}
	}
}

//消费者
class Customer implements Runnable{
    private Clerk clerk;
	
	public Customer(Clerk clerk) {
        this.clerk = clerk;
	}
	@Override
	public void run() {
		for (int i = 0; i < 20; i++) {
			clerk.sale();
		}
	}
}
