package com.nor.zookeeper.lock;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class ReadLocks {
	public ZooKeeper zk = null;
	public static final String root = "/locks";
	protected static Integer mutex=new Integer(-1);
	private String node;
	
	ReadLocks(){
		try {
			zk = new ZooKeeper("109.105.2.162:2181,109.105.2.162:2182,109.105.2.162:2183",10000, new Watcher(){
				@Override
				public void process(WatchedEvent event) {
					System.out.println("得到通知 "+event.getType());
					if (event.getType() == Event.EventType.NodeDeleted) {
			            synchronized (mutex) {
			                mutex.notify();
			            }
			        }
				}});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void getReadLock() throws KeeperException, InterruptedException {
		this.node = zk.create(root + "/read-", new byte[0],
				Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.println(node);
		
		while (true) {
			synchronized (mutex) {
				List<String> children = zk.getChildren(root, false);
				int min = Integer.parseInt(children.get(0).split("-")[1]);
				String flag = "";
				//当前最小节点是否为 本节点
				for (String ch : children) {
					if (ch.startsWith("write-"))
						return;
					else {
						String number = ch.split("-")[1];
						int tmp = Integer.parseInt(number);
						if (tmp <= min){
							min = tmp;
							flag = ch;
						}
					}
				}
				System.out.println("flag="+flag);
				if (this.node.equals(root+"/"+flag)) {
					System.out.println("获取到了锁，开始工作...");
					Thread.sleep(2000);
					zk.delete(this.node, 0);
					break;
				} else {
					System.out.println("wait start...");
					mutex.wait();
					System.out.println("wait end...");
				}
			}
		}
	}
	
	public static void main(String[] args) throws UnknownHostException, InterruptedException, KeeperException {
		// TODO Auto-generated method stub
		
		final ReadLocks le = new ReadLocks();
		le.zk.create(root, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);	
		
		final String node1 = le.zk.create(root+"/read-", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		final String node2 = le.zk.create(root+"/read-", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		
		Thread th=new Thread(){
			@Override
			public void run(){
				System.out.println("Thread ...");
				try {
					Thread.sleep(2000);
					le.zk.exists(node1, true);
					le.zk.delete(node1, 0);
					Thread.sleep(2000);
					le.zk.exists(node2, true);
					le.zk.delete(node2, 0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (KeeperException e) {
					e.printStackTrace();
				}
			}
		};
		th.start();
		le.getReadLock();
	}

}
