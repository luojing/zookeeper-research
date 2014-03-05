package com.nor.zookeeper.queue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;


public class FIFOQueue {
	private ZooKeeper zk = null;
    private Integer mutex;
	private String root;

	FIFOQueue(String root) {
		this.root = root;
		this.mutex = new Integer(-1);
		try {
			zk = new ZooKeeper("109.105.2.162:2181,109.105.2.162:2182,109.105.2.162:2183",10000, new Watcher(){
				@Override
				public void process(WatchedEvent event) {
					synchronized (mutex) {
			            mutex.notify();
			        }
				}});
			Stat s = zk.exists(root, false);
			if (s == null) {
				zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
			}
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}
	
	/**
     * 生产者
     *
     * @param i
     * @return
     */

    boolean produce(int i) throws KeeperException, InterruptedException{
        ByteBuffer b = ByteBuffer.allocate(4);
        byte[] value;
        b.putInt(i);
        value = b.array();
        zk.create(root + "/element", value, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.PERSISTENT_SEQUENTIAL);
        System.out.println(zk.getChildren(root, false));
        return true;
    }

    /**
     * 消费者
     *
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    int consume() throws KeeperException, InterruptedException{
        int retvalue = -1;
        Stat stat = null;
        while (true) {
            synchronized (mutex) {
                List<String> list = zk.getChildren(root, true);
                if (list.size() == 0) {
                	System.out.println("consume: wait()");
                	mutex.wait();
                } else {
                    Integer min = new Integer(list.get(0).substring(7));
                    String element = list.get(0);
                    for(String s : list){
                        Integer tempValue = new Integer(s.substring(7));
                        if(tempValue < min) min = tempValue;
                    }
                    byte[] b = zk.getData(root + "/"+element,false, stat);
                    zk.delete(root + "/"+element, 0);
                    ByteBuffer buffer = ByteBuffer.wrap(b);
                    retvalue = buffer.getInt();
                    return retvalue;
                }
            }
        }
    }

	public static void main(String[] args) {
		final FIFOQueue q = new FIFOQueue("/app1");
		int i;
        final Integer max = new Integer(50);

        
        Thread pro = new Thread(){
        	@Override
    	    public void run() {
        		System.out.println("Producer");
                for (int i = 0; i < max; i++){
                    try{
                        q.produce(10 + i);
                        Thread.sleep(2000);
                    } catch (KeeperException e){
                        e.printStackTrace();
                    } catch (InterruptedException e){
                    	e.printStackTrace();
                    }
                }
    	    }
        };
        
        Thread con = new Thread(){
        	@Override
    	    public void run() {
        		  System.out.println("Consume");
        	        for (int i = 0; i < max; i++) {
        	            try{
        	                int r = q.consume();
        	                Thread.sleep(1000);
        	                System.out.println("Item: " + r);
        	            } catch (KeeperException e){
        	                i--;
        	                e.printStackTrace();
        	            } catch (InterruptedException e){
        	            	e.printStackTrace();
        	            }
        	        }
    	    }
        };
        
        pro.start();
        con.start();
      
	}

}
