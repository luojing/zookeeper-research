zookeeper-research
==================
ZooKeeper Recipes and Solutions

http://zookeeper.apache.org/doc/trunk/recipes.html

### Deploy fake zookeeper cluster
1. Download zookeeper, http://www.apache.org/dyn/closer.cgi/zookeeper/
2. make 3 copies
  - tar zxvf zookeeper-3.4.5.tar.gz
  - cp -r zookeeper-3.4.5 /opt/zookeeper2181/
  - cp -r zookeeper-3.4.5 /opt/zookeeper2182/
  - cp -r zookeeper-3.4.5 /opt/zookeeper2183/
3. Configuration

cat zookeeper2181/conf/zoo.cfg
```
tickTime=2000
initLimit=10
syncLimit=5
dataDir=/tmp/zookeeper1
clientPort=2181
server.1=109.105.2.162:2887:3887 
server.2=109.105.2.162:2888:3888
server.3=109.105.2.162:2889:3889
```

cat zookeeper2182/conf/zoo.cfg
```
tickTime=2000
initLimit=10
syncLimit=5
dataDir=/tmp/zookeeper1
clientPort=2182
server.1=109.105.2.162:2887:3887 
server.2=109.105.2.162:2888:3888
server.3=109.105.2.162:2889:3889
```

cat zookeeper2183/conf/zoo.cfg
```
tickTime=2000
initLimit=10
syncLimit=5
dataDir=/tmp/zookeeper1
clientPort=2183
server.1=109.105.2.162:2887:3887 
server.2=109.105.2.162:2888:3888
server.3=109.105.2.162:2889:3889
```

cat /opt/run.sh
```bash
#!/bin/sh
echo "1" > /tmp/zookeeper1/myid
echo "2" > /tmp/zookeeper2/myid
echo "3" > /tmp/zookeeper3/myid

cd /opt/zookeeper2181/
./bin/zkServer.sh start
cd /opt/zookeeper2182/
./bin/zkServer.sh start
cd /opt/zookeeper2183/
./bin/zkServer.sh start
```

cat /opt/stop.sh
```bash
#!/bin/sh
cd /opt/zookeeper2181/
./bin/zkServer.sh stop
cd /opt/zookeeper2182/
./bin/zkServer.sh stop
cd /opt/zookeeper2183/
./bin/zkServer.sh stop
```

4. ./run.sh will start the cluster.
