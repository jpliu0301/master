package com.zlj.juc;

import java.util.concurrent.CountDownLatch;

/*
 * CountDownLatch(倒计时闭锁)：闭锁,在完成某些运算时,只有当其他线程的运算操作全部完成时，当前运算才继续执行。
 */
public class TestCuntDownLatch {
    public static void main(String[] args) {
		final CountDownLatch latch = new CountDownLatch(10);
		LatchDemo demo = new LatchDemo(latch);
		long start = System.currentTimeMillis();
		
		for (int i = 0; i < 10; i++) {
			new Thread(demo).start();
		}
		
		try {
			latch.await();     //等待其他线程的运算都执行完毕之后(即latch.countDown()减为0时)，才继续执行下面的操作
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();   //主线程
		System.out.println("耗时时间为：" + (end - start));
	}
}

class LatchDemo implements Runnable{
	private CountDownLatch latch;
	
	public LatchDemo(CountDownLatch latch) {
		this.latch = latch;
	}

	@Override
	public void run() {
		synchronized (this) {
			try {
				for (int i = 0; i < 50000; i++) {
					if(i % 2 == 0) {
						System.out.println(i);
					}
				}
			} finally {
				latch.countDown();    //每次执行完之后latch减1(此例中latch为10),当减为0时，就可以继续执行当前线程的运算操作
			}
		}
	}
}
