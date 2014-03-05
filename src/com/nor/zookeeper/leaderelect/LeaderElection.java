package com.nor.zookeeper.leaderelect;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class LeaderElection {
	protected ZooKeeper zk = null;
	protected String root;
	protected static Integer mutex=new Integer(-1);
	
	LeaderElection(final String root){
		this.root = root;
		try {
			zk = new ZooKeeper("109.105.2.162:2181,109.105.2.162:2182,109.105.2.162:2183",10000, new Watcher(){
				@Override
				public void process(WatchedEvent event) {
					if (event.getPath().equals(root + "/leader") && event.getType() == Event.EventType.NodeCreated) {
			            System.out.println("得到通知");
			            synchronized (mutex) {
			                mutex.notify();
			            }
			            following();
			        }
				}});
			zk.create(root, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	void findLeader() throws InterruptedException, UnknownHostException,
			KeeperException {
		synchronized (mutex) {
			byte[] leader = null;
			if(zk.exists(root + "/leader", false) != null){
				leader = zk.getData(root + "/leader", true, null);
			}
			
			if (leader != null) {
				following();
			} else {
				String newLeader = null;
				byte[] localhost;
				localhost = InetAddress.getLocalHost().getAddress();
				newLeader = zk.create(root + "/leader", localhost,
						ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
				if (newLeader != null) {
					leading();
				} else {
					mutex.wait();
				}
			}
		}
	}
	
	void following() {
        System.out.println("成为组成员");
    }
	
	void leading() {
        System.out.println("成为领导者");
    }

	public static void main(String[] args) throws UnknownHostException, InterruptedException, KeeperException {
		// TODO Auto-generated method stub
		LeaderElection le = new LeaderElection("/GroupMembers");
		le.findLeader();
	}

}
