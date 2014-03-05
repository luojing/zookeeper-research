package com.nor.zookeeper;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

public class ClientTestApi {

	public static void main(String[] args) {
		MyWatcher wc = new MyWatcher();
		
		try {
			ZooKeeper zk = new ZooKeeper("109.105.2.162:2181,109.105.2.162:2182,109.105.2.162:2183", 10000, wc);
			String path = "/testRootPath1";
			
			/*
			System.out.println("1. 创建一个目录节点");
			zk.create(path, "testRootData".getBytes(), Ids.OPEN_ACL_UNSAFE,
					CreateMode.PERSISTENT);
			
			System.out.println("2. 创建一个子目录节点");
			zk.create(path + "/testChildPathOne",
					"testChildDataOne".getBytes(), Ids.OPEN_ACL_UNSAFE,
					CreateMode.PERSISTENT);
			System.out.println("3. 创建另外一个子目录节点");
			zk.create(path + "/testChildPathTwo",
					"testChildDataTwo".getBytes(), Ids.OPEN_ACL_UNSAFE,
					CreateMode.PERSISTENT);
			*/
			
			System.out.println("1. /testRootPath1 getData: "+new String(zk.getData(path, true, null)));
			System.out.println("2. /testRootPath1/testChildPathOne getData: "+new String(zk.getData(path+"/testChildPathOne", true, null)));
			
			System.out.println("3. 取出子目录节点列表");
			System.out.println(zk.getChildren(path, true));

			System.out.println("4. 修改子目录节点数据");
			zk.setData(path + "/testChildPathOne",
					"modifyChildDataOne-1".getBytes(), -1);
			System.out.println("目录节点状态：[" + zk.exists(path, true) + "]");


			System.out.println("5. /testRootPath1/testChildPathTwo getData: "+new String(zk.getData(
					path + "/testChildPathTwo", true, null)));
			
			System.out.println("6. APIS");
			System.out.println("sessionid:"+zk.getSessionId());
			States states = zk.getState();
			System.out.println("isAlive: "+states.isAlive());
			System.out.println("isConnect: "+states.isConnected());
			System.out.println("name: "+states.name());
			System.out.println("ordinal: "+states.ordinal());
			System.out.println("toString: "+states.toString());
			Stat stat = zk.exists(path, false);
			List<ACL> li = zk.getACL(path, stat);
			System.out.println("ACL size: "+li.size());
			System.out.println("ACL content: "+li.get(0).toString());
			
			System.out.println("exist: "+zk.exists(path, true));
			/*
			System.out.println("7. 删除子目录节点");
			zk.delete(path + "/testChildPathTwo", -1);
			zk.delete(path + "/testChildPathOne", -1);
			
			System.out.println("8. 删除父目录节点");
			zk.delete(path, -1);
			*/
 
			// 关闭连接
			zk.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
