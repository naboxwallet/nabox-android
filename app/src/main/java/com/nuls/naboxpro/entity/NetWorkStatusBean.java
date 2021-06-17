package com.nuls.naboxpro.entity;

public class NetWorkStatusBean {


    /**
     * chain : NERVE
     * lastCheckSyncHeight : 5383684
     * newCheckSyncHeight : 5383745
     * checkFailStartTime : 0
     * running : true
     */

    private String chain;
    private Number lastCheckSyncHeight;
    private Number newCheckSyncHeight;
    private Number checkFailStartTime;
    private boolean running;

    public String getChain() {
        return chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }

    public Number getLastCheckSyncHeight() {
        return lastCheckSyncHeight;
    }

    public void setLastCheckSyncHeight(Number lastCheckSyncHeight) {
        this.lastCheckSyncHeight = lastCheckSyncHeight;
    }

    public Number getNewCheckSyncHeight() {
        return newCheckSyncHeight;
    }

    public void setNewCheckSyncHeight(Number newCheckSyncHeight) {
        this.newCheckSyncHeight = newCheckSyncHeight;
    }

    public Number getCheckFailStartTime() {
        return checkFailStartTime;
    }

    public void setCheckFailStartTime(Number checkFailStartTime) {
        this.checkFailStartTime = checkFailStartTime;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
