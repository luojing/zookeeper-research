package com.nor.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class MyWatcher implements Watcher {

	@Override
	public void process(WatchedEvent event) {
		System.out.println("--> 已经触发了" + event.getType() + "事件！");
	}

}
