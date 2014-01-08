package com.taobao.taokeeper.model;

/**
 * Description: Model: ZooKeeperStat
 *
 * @author yinshi.nc
 * @Date 2011-10-28
 */
public class ZooKeeperStatusV2 extends ZooKeeperStatus {

    private RWStatistics rwps;
    public static class RWStatistics{


        private double getChildren2;
        private double createSession;
        private double closeSession;
        private double setData;
        private double setWatches;
        private double getChildren;
        private double delete;
        private double create;
        private double exists;
        private double getData;

        public RWStatistics(double getChildren2, double createSession, double closeSession, double setData,double setWatches,
                            double getChildren,
                            double delete,
                            double create,
                            double exists,
                            double getData){
            this.setGetChildren2( getChildren2 );
            this.setCreateSession( createSession );
            this.setCloseSession( closeSession );
            this.setSetData( setData );
            this.setSetWatches( setWatches );
            this.setGetChildren( getChildren );
            this.getDelete();
            this.setCreate( create );
            this.setExists( exists );
            this.setGetData( getData );
        }

        public double getGetChildren2() {
            return getChildren2;
        }

        public void setGetChildren2( double getChildren2 ) {
            this.getChildren2 = getChildren2;
        }

        public double getCreateSession() {
            return createSession;
        }

        public void setCreateSession( double createSession ) {
            this.createSession = createSession;
        }

        public double getCloseSession() {
            return closeSession;
        }

        public void setCloseSession( double closeSession ) {
            this.closeSession = closeSession;
        }

        public double getSetData() {
            return setData;
        }

        public void setSetData( double setData ) {
            this.setData = setData;
        }

        public double getSetWatches() {
            return setWatches;
        }

        public void setSetWatches( double setWatches ) {
            this.setWatches = setWatches;
        }

        public double getGetChildren() {
            return getChildren;
        }

        public void setGetChildren( double getChildren ) {
            this.getChildren = getChildren;
        }

        public double getDelete() {
            return delete;
        }

        public void setDelete( double delete ) {
            this.delete = delete;
        }

        public double getCreate() {
            return create;
        }

        public void setCreate( double create ) {
            this.create = create;
        }

        public double getExists() {
            return exists;
        }

        public void setExists( double exists ) {
            this.exists = exists;
        }

        public double getGetData() {
            return getData;
        }

        public void setGetData( double getData ) {
            this.getData = getData;
        }

        @Override
        public String toString() {
            return "RWStatistics [getChildren2=" + getChildren2 + ", createSession=" + createSession + ", closeSession=" + closeSession
                    + ", setData=" + setData + ", setWatches=" + setWatches + ", getChildren=" + getChildren + ", delete=" + delete
                    + ", create=" + create + ", exists=" + exists + ", getData=" + getData + "]";
        }

    }




    public RWStatistics getRwps() {
        return rwps;
    }




    public void setRwps( RWStatistics rwps ) {
        this.rwps = rwps;
    }


}