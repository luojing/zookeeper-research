package com.nor.zookeeper;
import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class CreateWiSeqential {

	public static void main(String[] args) {
		MyWatcher wc = new MyWatcher();
		
		try {
			ZooKeeper zk = new ZooKeeper("109.105.2.162:2181,109.105.2.162:2182,109.105.2.162:2183", 10000, wc);
			String path = "/client1";
			
			zk.create(path, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			System.out.println("1. 创建一个目录节点");
			
	        byte[] value1={0,0,0,1};
	        byte[] value2={0,0,0,2};
	        byte[] value3={0,0,0,3};
	        byte[] value4={0,0,0,4};
			zk.create(path+"/element", value1, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
			zk.create(path+"/element", value2, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
			zk.create(path+"/element", value3, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
			zk.create(path+"/element", value4, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
			/*
			 * ls /client1
			 * [element0000000000, element0000000001, element0000000002, element0000000003]
			 */
			
			System.out.println("3. 取出子目录节点列表");
			
			
			List<String> list = zk.getChildren(path, false);
			System.out.println(list);
			
			Integer min = new Integer(list.get(0).substring(7));
			String element = list.get(0);
            for(String s : list){
                Integer tempValue = new Integer(s.substring(7));
                if(tempValue < min){
                	min = tempValue;
                	element=s;
                }
            }
            
			byte [] ele0 = zk.getData(path+"/"+element, false, null);
			System.out.println(element+": "+new String(ele0));

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
