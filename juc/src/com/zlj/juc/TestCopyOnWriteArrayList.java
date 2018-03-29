package com.zlj.juc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * CopyOnWriteArrayList / CopyOnWriteArraySet   "写入并复制"
 * 
 * 注意：
 * 1、当添加操作多时，不适合用CopyOnWriteArrayList/Set，因为每写入一次就会进行一次复制，效率低，开销大。
 * 2、当并发迭代多时，可以选择使用。
 */
public class TestCopyOnWriteArrayList {
	public static void main(String[] args) {
		HelloThead helloThead = new HelloThead();
		for (int i = 0; i < 10; i++) {
			new Thread(helloThead).start();
		}
	}
}

class HelloThead implements Runnable {
	//private static List<String> list = Collections.synchronizedList(new ArrayList<>());  //会出现并发修改异常：ConcurrentModificationException
	
	private static CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();   //使用CopyOnWriteArrayList，则不会出现并发修改异常
	
	static {
		list.add("AA");
		list.add("BB");
		list.add("CC");
	}

	@Override
	public void run() {
		Iterator<String> iterator = list.iterator();
		
		while(iterator.hasNext()) {
			//迭代的同时进行添加操作
			System.out.println(iterator.next());
			list.add("DD");
		}
	}
}