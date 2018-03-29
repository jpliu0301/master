package com.zlj.juc;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/*
 * 一、创建执行线程的方式三：实现Callable接口。
 *   相较于实现Runnable接口的区别：
 *   (1)、实现Callable接口时有泛型;
 *   (2)、实现Callable接口重写的是call()方法,有返回值，会抛出异常
 *   
 * 二、实现Callable接口的方式，需要FutureTask实现类的支持，用于接收运算的结果。FutureTask是Future接口的实现类
 */
public class TestCallable {
	public static void main(String[] args) {
		CallableDemo callableDemo = new CallableDemo();
		//实现Callable接口的方式，需要FutureTask实现类的支持，用于接收运算的结果。
		FutureTask<Integer> task = new FutureTask<>(callableDemo);
		
		new Thread(task).start();
		//接收线程运算后的结果
		try {
			//FutureTask 可用于闭锁(只有当所有运算操作都执行完毕后，才会执行task.get()操作)
			Integer sum = task.get();   //获取返回值sum
			System.out.println(sum);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
}

class CallableDemo implements Callable<Integer> {

	@Override
	public Integer call() throws Exception {
		int sum = 0;
		for (int i = 0; i <= 100; i++) {
			sum += i;
		}
		return sum;
	}
}

/*class RunnableDemo implements Runnable {

	@Override
	public void run() {
		
	}
}*/
