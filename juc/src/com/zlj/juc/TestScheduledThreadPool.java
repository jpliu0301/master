package com.zlj.juc;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java.util.concurrent.Future;

/*
 * 线程调度案例
 * ScheduledExecutorService  newScheduledThreadPool():创建固定大小的线程，可以延迟或定时的执行任务。
 */
public class TestScheduledThreadPool {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ScheduledExecutorService pool = Executors.newScheduledThreadPool(5);
		for (int i = 0; i < 10; i++) {
			Future<Integer> future = pool.schedule(new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					int sum = new Random().nextInt(100);  //生成100以内的随机数
	                System.out.println(Thread.currentThread().getName() + " : " + sum);
					return sum;
				}
			//延迟3，最后一个参数指定延迟什么，是秒，还是分钟，还是小时
			}, 3, TimeUnit.SECONDS);
			
			System.out.println(future.get());
		}
		pool.shutdown();
	}
}
 