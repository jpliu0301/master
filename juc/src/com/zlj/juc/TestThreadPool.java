package com.zlj.juc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 一、线程池：提供了一个线队列，队列中保存着所有等待状态的线程。避免了频繁的创建与销毁所造成的额外开销，提高了响应的速度。
 * 
 * 二、线程池的体系结构：
 *   java.util.concurrent.Executor：负责线程的使用与调度的根接口
 *        |---(**)ExecutorService 子接口： 线程池的主要接口
 *               |--- ThreadPoolExecutor 线程池的实现类
 *               |--- ScheduledExecutorService 子接口：负责线程的调度
 *                     |--- ScheduledThreadPoolExecutor : 继承了ThreadPoolExecutor，实现了ScheduledExecutorService接口，所以该实现类即具备了线程池的功能，又有线程调度的功能。
 *                     
 * 三、工具类：Executors
 * ExecutorService  newFixedThreadPool()： 创建固定大小的线程池;
 * ExecutorService  newCachedThreadPool():缓存线程池，线程池的数量不固定，可以根据需求自动的更改数量;
 * ExecutorService  newSingleThreadExecutor():创建单个线程池。线程池中只有一个线程;
 * 
 * ScheduledExecutorService  newScheduledThreadPool():创建固定大小的线程，可以延迟或定时的执行任务。
 */

//线程池案例
public class TestThreadPool {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ThreadPoolDemo threadPoolDemo = new ThreadPoolDemo();
		//new Thread(threadPoolDemo).start();
		//new Thread(threadPoolDemo).start();
		
		//创建线程池
		ExecutorService pool = Executors.newFixedThreadPool(5);
		
		/*
		 * 实现Callable的做法
		 */
		List<Future<Integer>> list = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			Future<Integer> future = pool.submit(new Callable<Integer>() {
				private int sum;

				@Override
				public Integer call() throws Exception {
					for (int j = 0; j <= 100; j++) {
						sum += j;
					}
					return sum;
				}
			});
			//System.out.println(future.get());
			list.add(future);
		}
		for (Future<Integer> future : list) {
			System.out.println(future.get());
		}
		
		pool.shutdown();
		
		
		/*
		 * 实现Runnable接口的做法
		 */
		/*
		
		//给线程池中的线程分配任务
		for (int i = 0; i < 10; i++) {
			pool.submit(threadPoolDemo);
		}
		
		//关闭线程池
		pool.shutdown();
		*/
    }
}

class ThreadPoolDemo implements Runnable{
	private int i = 0;
	
	@Override
	public void run() {
		while(i <= 100) {
			System.out.println(Thread.currentThread().getName() + " : " + i++);
		}
	}
}
