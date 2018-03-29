package com.zlj.juc;

/*
 * 线程8锁：
 * 
 * 题目：判断打印的是"one" or "two"
 * 
 * 1、两个普通的synchronized同步方法，两个线程分别去调用getOne()和getTwo()方法，最后输出结果 ： // one  two
 * 2、给getOne()方法添加Thread.sleep()方法，最后输出结果为： // one  two
 * 3、添加普通方法getThree(),最后输出的结果为： // Three one two (前提是给getOne()方法添加Thread.sleep()方法)
 *   如果都没有添加Thread.sleep()方法，则顺序为： one two three
 *   如果给getTwo()方法添加Thread.sleep()方法,则输出的结果为： // one three two
 * 4、两个普通的synchronized同步方法，两个对象，如果都没有加Thread.sleep()方法，则结果为：one two
 *   如果其中有一个方法添加了Thread.sleep()方法，则该方法输出的结果在最后
 * 5、修改getOne()方法为静态方法，(即一个静态方法，一个非静态方法，一个对象)给getOne()方法添加Thread.sleep()方法, 结果为： // two one
 *    否则结果为： // one two
 * 6、修改两个方法均为静态同步方法，一个对象，结果为：// one two
 * 7、修改两个方法均为静态同步方法，两个对象， 结果为：  //  one two  (静态方法与对象无关)
 * 8、一个静态同步方法，一个非静态同步方法，两个对象，结果为： //如果都没有加Thread.sleep()方法，则结果为：one two
 *   如果其中有一个方法添加了Thread.sleep()方法，则该方法输出的结果在最后
 * 
 * 总结：
 * (1)、同一个对象，不论是否添加Thread.sleep()方法，调用普通的同步方法，或者调用两个均为静态的同步方法，按调用的顺序输出结果
 *     1、2、6、7
 * (2)、两个同为普通同步方法或者一个静态，一个非静态，两个对象，如果没有加Thread.sleep()方法，则结果为：按调用的顺序输出
 *      如果其中有一个方法添加了Thread.sleep()方法，则该方法输出的结果在最后
 *      4、8
 * (3)、添加普通方法的输出结果如 --> 3
 * (4)、一个静态同步，一个非静态同步，一个对象，输出结果如： ---> 5
 * 线程8锁的关键：
 * (1)、非静态方法的锁默认为this, 静态方法的锁为对应的Class实例
 * (2)、某一个时刻内，只能有一个线程持有锁，无论是有几个方法。
 */
public class TestThread8Moniter {
	public static void main(String[] args) {
		Thread8MoniterDemo thread8MoniterDemo = new Thread8MoniterDemo();
		Thread8MoniterDemo thread8MoniterDemo2 = new Thread8MoniterDemo();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				Thread8MoniterDemo.getOne();
			}
		}).start();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				thread8MoniterDemo2.getTwo();
			}
		}).start();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				thread8MoniterDemo.getThree();
			}
		}).start();
	}
}

class Thread8MoniterDemo {
	public static synchronized void getOne() {
		/*try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		System.out.println("one");
	}
	
	public synchronized void getTwo() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("two");
	}
	
	public void getThree() {
		System.out.println("three");
	}
}