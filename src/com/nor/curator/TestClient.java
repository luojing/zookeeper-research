package com.nor.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class TestClient {

	public static void main(String[] args) throws Exception {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		CuratorFramework client = CuratorFrameworkFactory.newClient("109.105.2.162:2181,109.105.2.162:2182,109.105.2.162:2183", retryPolicy);
		String path="/curatorroot";
		
		client.start();
		
		client.create().forPath(path);
		client.setData().forPath(path, "hello world".getBytes());

		System.out.println(client.getACL().forPath(path));
		System.out.println("data:"+new String(client.getData().forPath(path)));
		
		client.close();
	}

}
