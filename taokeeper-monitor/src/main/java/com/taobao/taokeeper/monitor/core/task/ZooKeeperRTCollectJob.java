package com.taobao.taokeeper.monitor.core.task;

import com.taobao.taokeeper.common.GlobalInstance;
import com.taobao.taokeeper.dao.ZooKeeperClusterDAO;
import com.taobao.taokeeper.model.ZooKeeperCluster;
import common.toolkit.java.exception.DaoException;
import common.toolkit.java.util.ObjectUtil;
import common.toolkit.java.util.collection.CollectionUtil;
import common.toolkit.java.util.collection.ListUtil;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Created with IntelliJ IDEA.
 * User: dingjoey
 * Date: 14-1-7
 * Time: 13:48
 * To change this template use File | Settings | File Templates.
 */
public class ZooKeeperRTCollectJob extends TimerTask {
    private static final Logger LOG_rtMonitor = LoggerFactory.getLogger("rtMonitor");

    private WebApplicationContext wac;

    private static volatile Map<Integer, Map<String, Map<String, String>>> rtStatus = new HashMap<Integer, Map<String, Map<String, String>>>();

    private static volatile Map<Integer, Map<String, String>> clustRTStatus = new HashMap<Integer, Map<String, String>>();


    public ZooKeeperRTCollectJob() {
        wac = ContextLoader.getCurrentWebApplicationContext();
    }

    public static Map<Integer, Map<String, Map<String, String>>> getRtStatus() {
        return rtStatus;
    }

    public static Map<Integer, Map<String, String>> getClustRTStatus() {
        return clustRTStatus;
    }

    private List<ZooKeeperCluster> getMonitorCluster() throws DaoException {
        ZooKeeperClusterDAO zooKeeperClusterDAO = (ZooKeeperClusterDAO) wac.getBean("zooKeeperClusterDAO");

        Map<Integer, ZooKeeperCluster> zooKeeperClusterMap = GlobalInstance.getAllZooKeeperCluster();

        List<ZooKeeperCluster> zooKeeperClusterSet = null;

        if (null == zooKeeperClusterMap) {
            zooKeeperClusterSet = zooKeeperClusterDAO.getAllDetailZooKeeperCluster();
        } else {
            zooKeeperClusterSet = new ArrayList<ZooKeeperCluster>();
            zooKeeperClusterSet.addAll(zooKeeperClusterMap.values());
        }

        return zooKeeperClusterSet;
    }

    private class launchCollectRTJob implements Runnable {
        private final ZooKeeperCluster cluster;

        private launchCollectRTJob(ZooKeeperCluster cluster) {
            this.cluster = cluster;
        }

        @Override
        public void run() {
            try {
                collectRT(cluster);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    private void collectRT(ZooKeeperCluster cluster) throws IOException, InterruptedException {

        if (ObjectUtil.isBlank(cluster) || CollectionUtil.isBlank(cluster.getServerList())) {
            return;
        }

        Map<String, Map<String, String>> serverRTUpdate = new HashMap<String, Map<String, String>>();

        for (String server : cluster.getServerList()) {
            Map<String, String> update = new HashMap<String, String>();

            CountDownLatch connectSignal = new CountDownLatch(1);

            long st = System.currentTimeMillis();
            ZooKeeper zkClient = new ZooKeeper(server, 5000, new DefaultWatcher(connectSignal));
            connectSignal.await();
            long rt = System.currentTimeMillis() - st;
            update.put("createSession", String.valueOf(rt));

            final int cnt = 30;
            st = System.currentTimeMillis();
            for (int i = 0; i < cnt; i++) {
                try {
                    zkClient.create("/qiaoyi.dingqy" + i, "rtMonitor".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                } catch (KeeperException e) {
                }
            }
            rt = System.currentTimeMillis() - st;
            update.put("create", String.valueOf(rt / cnt));

            st = System.currentTimeMillis();
            for (int i = 0; i < cnt; i++) {
                try {
                    zkClient.exists("/qiaoyi.dingqy" + i, false);
                } catch (KeeperException e) {
                }
            }
            rt = System.currentTimeMillis() - st;
            update.put("exists", String.valueOf(rt / cnt));

            st = System.currentTimeMillis();
            for (int i = 0; i < cnt; i++) {
                try {
                    zkClient.setData("/qiaoyi.dingqy" + i, "rtMonitor".getBytes(), -1);
                } catch (KeeperException e) {
                }
            }
            rt = System.currentTimeMillis() - st;
            update.put("setData", String.valueOf(rt / cnt));

            st = System.currentTimeMillis();
            for (int i = 0; i < cnt; i++) {
                try {
                    zkClient.getData("/qiaoyi.dingqy" + i, false, null);
                } catch (KeeperException e) {
                }
            }
            rt = System.currentTimeMillis() - st;
            update.put("getData", String.valueOf(rt / cnt));

            st = System.currentTimeMillis();
            for (int i = 0; i < cnt; i++) {
                try {
                    zkClient.delete("/qiaoyi.dingqy" + i, -1);
                } catch (KeeperException e) {
                }
            }
            rt = System.currentTimeMillis() - st;
            update.put("delete", String.valueOf(rt / cnt));

            st = System.currentTimeMillis();
            for (int i = 0; i < cnt; i++) {
                try {
                    zkClient.getChildren("/qiaoyi.dingqy" + i, false);
                } catch (KeeperException e) {
                }
            }
            rt = System.currentTimeMillis() - st;
            update.put("getChildren", String.valueOf(rt / cnt));

            serverRTUpdate.put(server, update);

            LOG_rtMonitor.warn("[rt-check] servers : " + server
                    + " --- createSession : " + update.get("createSession")
                    + ", create : " + update.get("create")
                    + ", delete : " + update.get("delete")
                    + ", getChildren : " + update.get("getChildren")
                    + ", setData : " + update.get("setData")
                    + ", getData : " + update.get("getData")
                    + ", exists : " + update.get("exists")
            );
        }

        rtStatus.put(cluster.getClusterId(), serverRTUpdate);

        // for cluster rt monitor
        // XXX todo refactor
        Map<String, String> update = new HashMap<String, String>();

        CountDownLatch connectSignal = new CountDownLatch(1);

        long st = System.currentTimeMillis();
        ZooKeeper zkClient = new ZooKeeper(ListUtil.toString(cluster.getServerList()), 10000, new DefaultWatcher(connectSignal));
        connectSignal.await();
        long rt = System.currentTimeMillis() - st;
        update.put("createSession", String.valueOf(rt));

        final int cnt = 30;
        st = System.currentTimeMillis();
        for (int i = 0; i < cnt; i++) {
            try {
                zkClient.create("/qiaoyi.dingqy" + i, "rtMonitor".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } catch (KeeperException e) {
            }
        }
        rt = System.currentTimeMillis() - st;
        update.put("create", String.valueOf(rt / cnt));

        st = System.currentTimeMillis();
        for (int i = 0; i < cnt; i++) {
            try {
                zkClient.exists("/qiaoyi.dingqy" + i, false);
            } catch (KeeperException e) {
            }
        }
        rt = System.currentTimeMillis() - st;
        update.put("exists", String.valueOf(rt / cnt));

        st = System.currentTimeMillis();
        for (int i = 0; i < cnt; i++) {
            try {
                zkClient.setData("/qiaoyi.dingqy" + i, "rtMonitor".getBytes(), -1);
            } catch (KeeperException e) {
            }
        }
        rt = System.currentTimeMillis() - st;
        update.put("setData", String.valueOf(rt / cnt));

        st = System.currentTimeMillis();
        for (int i = 0; i < cnt; i++) {
            try {
                zkClient.getData("/qiaoyi.dingqy" + i, false, null);
            } catch (KeeperException e) {
            }
        }
        rt = System.currentTimeMillis() - st;
        update.put("getData", String.valueOf(rt / cnt));

        st = System.currentTimeMillis();
        for (int i = 0; i < cnt; i++) {
            try {
                zkClient.delete("/qiaoyi.dingqy" + i, -1);
            } catch (KeeperException e) {
            }
        }
        rt = System.currentTimeMillis() - st;
        update.put("delete", String.valueOf(rt / cnt));

        st = System.currentTimeMillis();
        for (int i = 0; i < cnt; i++) {
            try {
                zkClient.getChildren("/qiaoyi.dingqy" + i, false);
            } catch (KeeperException e) {
            }
        }
        rt = System.currentTimeMillis() - st;
        update.put("getChildren", String.valueOf(rt / cnt));

        LOG_rtMonitor.warn("[cluster-rt-check] servers : " + ListUtil.toString(cluster.getServerList())
                + " --- createSession : " + update.get("createSession")
                + ", create : " + update.get("create")
                + ", delete : " + update.get("delete")
                + ", getChildren : " + update.get("getChildren")
                + ", setData : " + update.get("setData")
                + ", getData : " + update.get("getData")
                + ", exists : " + update.get("exists")
        );

        clustRTStatus.put(cluster.getClusterId(), update);
    }

    private class DefaultWatcher implements Watcher {

        CountDownLatch connectSignal = null;

        private DefaultWatcher(CountDownLatch connectSignal) {
            this.connectSignal = connectSignal;
        }

        @Override
        public void process(WatchedEvent event) {
            if (event.getState() == Event.KeeperState.SyncConnected && connectSignal != null) {
                connectSignal.countDown();
            }
        }
    }

    @Override
    public void run() {
        try {
            List<ZooKeeperCluster> clusters = getMonitorCluster();

            if (clusters == null) return;

            for (ZooKeeperCluster cluster : clusters) {
                Thread t = new Thread(new launchCollectRTJob(cluster));
                t.start();
            }

            Thread.sleep(500l);

        } catch (DaoException e) {
        } catch (InterruptedException e) {
        }
        return;

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ZooKeeperRTCollectJob job = new ZooKeeperRTCollectJob();
        ZooKeeperCluster cluster = new ZooKeeperCluster();
        cluster.setServerList(Arrays.asList(new String[]{"10.232.102.188:2181", "10.232.102.189:2181", "10.232.102.190:2181"}));
        job.collectRT(cluster);
    }
}
