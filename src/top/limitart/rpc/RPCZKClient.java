package top.limitart.rpc;

import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.limitart.base.Proc1;
import top.limitart.net.AddressPair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hank
 * @version 2018/10/19 0019 16:07
 */
public class RPCZKClient {
    private static Logger LOGGER = LoggerFactory.getLogger(RPCZKClient.class);
    private ZooKeeper zk;

    public RPCZKClient connect(AddressPair addressPair, Proc1<WatchedEvent> callback) throws IOException {
        zk = new ZooKeeper(addressPair.toString(), 5000, event -> callback.run(event));
        return this;
    }

    public RPCZKClient stop() throws InterruptedException {
        if (zk != null) {
            zk.close();
        }
        return this;
    }

    public void provideService(RPCServiceName name, AddressPair addressPair) {
        try {
            byte[] bytes = addressPair.toString().getBytes();
            String s = zk.create(name.toString(), bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            LOGGER.info("create zookeeper node ({} => {})", s, addressPair);
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("create node error", e);
        }
    }
    private void subscribeServices() {
        try {
            List<String> nodeList = zk.getChildren("/", new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getType() == Event.EventType.NodeChildrenChanged) {
                        subscribeServices();
                    }
                }
            });
            List<String> dataList = new ArrayList<>();
            for (String node : nodeList) {
//                byte[] bytes = zk.getData(Constant.ZK_REGISTRY_PATH + "/" + node, false, null);
//                dataList.add(new String(bytes));
            }
            LOGGER.debug("node data: {}", dataList);
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("", e);
        }
    }
}
