package com.zlj.juc;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 一、i++ 的原子性问题：i++ 的操作实际上分为三个步骤："读 - 改  - 写"
 *                 int i = 10;
 *                 i = i++;    //10
 *                 
 *                 int temp = i;
 *                 i = i + 1;
 *                 i = temp;
 *                 
 * 二、原子变量：在java.util.concurrent.atomic 包下提供了一些原子变量。
 *    1、volatile 保证内存可见性;
 *    2、CAS(Compare-And-Swap)算法保证数据变量的原子性。
 *       (1)、CAS算法是一种硬件对并发的支持，针对多处理器操作而设计的处理器中的一种特殊指令，用于管理对共享数据的并发访问。
 *       (2)、CAS是一种无锁的非阻塞算法的实现。
 *       (3)、CAS包含了3个操作数：
 *       		①、需要读写的内存值：V
 *       		②、进行比较的值(预估值)：A
 *       		③、拟写入的新值(更新值)：B
 *       (4)、当且仅当 V 的值等于  A 时，CAS 通过原子方式用新值 B 来更新V的值，否则不会执行任何操作。
 * @author bashen
 *
 */
public class TestAtomicDemo {
	public static void main(String[] args) {
		AtomicDemo ad = new AtomicDemo();
		for(int i = 0; i < 10; i++) {
			new Thread(ad).start();
		}
	}
}

class AtomicDemo implements Runnable {
	//private volatile int serialNumber = 0;
	AtomicInteger atomicInteger = new AtomicInteger(); 

	@Override
	public void run() {
        try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
        System.out.println(getSerialNumber());
	}
	
	public int getSerialNumber() {
		//return serialNumber++;        //输出的10个数字有重复的，不能保证原子性
		return atomicInteger.getAndIncrement();   //CAS算法保证了数据变量的原子性，输出的10个数字无一重复
	}
}
