package com.nor.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class TestClient {

	public static void main(String[] args) throws Exception {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		String namespace="app1";

		CuratorFramework client = CuratorFrameworkFactory
				.builder()
				.namespace("app1")
				.retryPolicy(retryPolicy)
				.connectionTimeoutMs(2000)
				.connectString("109.105.2.162:2181,109.105.2.162:2182,109.105.2.162:2183")
				.build();
		String path = "/curatorroot";
		client.start();
		client.create().forPath(path, "This is root".getBytes());

		//node watcher: update/create/delete events, pull down the data, etc.
		NodeCache rootCache = new NodeCache(client, path, false);
		rootCache.getListenable().addListener(new NodeCacheListener(){
			@Override
			public void nodeChanged() throws Exception {
				System.out.println("node changed...");
			}});

		rootCache.start(true);
		//end
		
		//child watcher: Whenever a child is added, updated or removed
		PathChildrenCache rootChildren = new PathChildrenCache(client, path, true);
		rootChildren.getListenable().addListener(new PathChildrenCacheListener(){

			@Override
			public void childEvent(CuratorFramework client,
					PathChildrenCacheEvent event) throws Exception {
				System.out.println("child of /root/curatorroot/ changed event="+event.getType());
				
			}});
		rootChildren.start(StartMode.BUILD_INITIAL_CACHE);
		//end
		
		client.create().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(path + "/node_", "node first".getBytes());
		client.create().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(path + "/node_", "node second".getBytes());
		client.create().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(path + "/node_", "node third".getBytes());

		client.setData().forPath(path, "hello world".getBytes());

		System.out.println("Acl : " + client.getACL().forPath(path) + "...");
		System.out.println("data:" + new String(client.getData().forPath(path)));

		rootCache.close();
		rootChildren.close();
		client.close();
	}

}
