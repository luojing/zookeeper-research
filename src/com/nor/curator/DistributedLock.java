package com.nor.curator;

import java.util.concurrent.TimeUnit;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class DistributedLock {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		final CuratorFramework client = CuratorFrameworkFactory.newClient(
				"109.105.2.162:2181,109.105.2.162:2182,109.105.2.162:2183",
				retryPolicy);
		client.start();
		
		final String lockPath = "/_lock";
		final InterProcessMutex lock = new InterProcessMutex(client, lockPath);
		
		Thread t1 = new Thread() {
			@Override
			public void run() {
				try {
					for (int i = 0; i < 20; i++) {
						if (lock.acquire(2, TimeUnit.MILLISECONDS)) {
							// do some work inside of the critical section here
							System.out.println("thread 1...");
							lock.release();
							Thread.sleep(1000);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};
		
		Thread t2 = new Thread() {
			@Override
			public void run() {
				try {
					for (int i = 0; i < 20; i++) {
						if (lock.acquire(2, TimeUnit.MILLISECONDS)) {
							// do some work inside of the critical section here
							System.out.println("thread 2...");
							lock.release();
							Thread.sleep(2000);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};
		
		Thread t3 = new Thread() {
			@Override
			public void run() {
				try {
					for (int i = 0; i < 20; i++) {
						if (lock.acquire(2, TimeUnit.MILLISECONDS)) {
							// do some work inside of the critical section here
							System.out.println("thread 3...");
							lock.release();
							Thread.sleep(3000);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};
		
		
		t1.start();
		t2.start();
		t3.start();

	}

}
