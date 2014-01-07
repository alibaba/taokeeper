package com.taobao.taokeeper.monitor.core.task;

import com.taobao.taokeeper.common.GlobalInstance;
import com.taobao.taokeeper.dao.ZooKeeperClusterDAO;
import com.taobao.taokeeper.model.ZooKeeperCluster;
import common.toolkit.java.exception.DaoException;
import common.toolkit.java.util.ObjectUtil;
import common.toolkit.java.util.collection.CollectionUtil;
import common.toolkit.java.util.collection.ListUtil;
import org.apache.zookeeper.*;
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

    private WebApplicationContext wac;

    private static volatile Map<Integer, Map<String, String>> rtStatus = new HashMap<Integer, Map<String, String>>();

    public ZooKeeperRTCollectJob() {
        wac = ContextLoader.getCurrentWebApplicationContext();
    }

    public static Map<Integer, Map<String, String>> getRtStatus() {
        return rtStatus;
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

    private void collectRT(ZooKeeperCluster cluster) throws IOException, InterruptedException {

        if (ObjectUtil.isBlank(cluster) || CollectionUtil.isBlank(cluster.getServerList())) {
            return;
        }

        Map<String, String> update = new HashMap<String, String>();

        CountDownLatch connectSignal = new CountDownLatch(1);

        long st = System.currentTimeMillis();
        ZooKeeper zkClient = new ZooKeeper(ListUtil.toString(cluster.getServerList()), 5000, new DefaultWatcher(connectSignal));
        connectSignal.await();
        long rt = System.currentTimeMillis() - st;
        update.put("createSession", String.valueOf(rt));
        //System.out.println("createSession:" + String.valueOf(rt));

        final int cnt = 50;
        st = System.currentTimeMillis();
        for (int i = 0; i < cnt; i++) {
            try {
                zkClient.create("/qiaoyi.dingqy" + i, "rtMonitor".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } catch (KeeperException e) {
            }
        }
        rt = System.currentTimeMillis() - st;
        update.put("create", String.valueOf(rt / cnt));
        //System.out.println("create:" + String.valueOf(rt / cnt));

        st = System.currentTimeMillis();
        for (int i = 0; i < cnt; i++) {
            try {
                zkClient.exists("/qiaoyi.dingqy" + i, false);
            } catch (KeeperException e) {
            }
        }
        rt = System.currentTimeMillis() - st;
        update.put("exists", String.valueOf(rt / cnt));
        //System.out.println("exists:" + String.valueOf(rt / cnt));

        st = System.currentTimeMillis();
        for (int i = 0; i < cnt; i++) {
            try {
                zkClient.setData("/qiaoyi.dingqy" + i, "rtMonitor".getBytes(), -1);
            } catch (KeeperException e) {
            }
        }
        rt = System.currentTimeMillis() - st;
        update.put("setData", String.valueOf(rt / cnt));
        //System.out.println("setData:" + String.valueOf(rt / cnt));

        st = System.currentTimeMillis();
        for (int i = 0; i < cnt; i++) {
            try {
                zkClient.getData("/qiaoyi.dingqy" + i, false, null);
            } catch (KeeperException e) {
            }
        }
        rt = System.currentTimeMillis() - st;
        update.put("getData", String.valueOf(rt / cnt));
        //System.out.println("getData:" + String.valueOf(rt / cnt));

        st = System.currentTimeMillis();
        for (int i = 0; i < cnt; i++) {
            try {
                zkClient.delete("/qiaoyi.dingqy" + i, -1);
            } catch (KeeperException e) {
            }
        }
        rt = System.currentTimeMillis() - st;
        update.put("delete", String.valueOf(rt / cnt));
        //System.out.println("delete:" + String.valueOf(rt / cnt));

        st = System.currentTimeMillis();
        for (int i = 0; i < cnt; i++) {
            try {
                zkClient.getChildren("/qiaoyi.dingqy" + i, false);
            } catch (KeeperException e) {
            }
        }
        rt = System.currentTimeMillis() - st;
        update.put("getChildren", String.valueOf(rt / cnt));
        //System.out.println("getChildren:" + String.valueOf(rt / cnt));

        rtStatus.put(cluster.getClusterId(), update);
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
                collectRT(cluster);
            }

            Thread.sleep(500l);

        } catch (DaoException e) {
        } catch (InterruptedException e) {
        } catch (IOException e) {
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
