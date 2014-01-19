package com.jingfm.api.model.message;

/**
 * Created by Edmond on 13-11-1.
 */
public class UserOfflineMessageDTO {
    //{"cmbt":"","frd":"Luccy","frd_id":"100134","t":"atfd","ts":1383286227097}
    private String cmbt;
    private String frd;
    private String frd_id;
    private String t;
    private long ts;

    public String getCmbt() {
        return cmbt;
    }

    public void setCmbt(String cmbt) {
        this.cmbt = cmbt;
    }

    public String getFrd() {
        return frd;
    }

    public void setFrd(String frd) {
        this.frd = frd;
    }

    public String getFrd_id() {
        return frd_id;
    }

    public void setFrd_id(String frd_id) {
        this.frd_id = frd_id;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        if(ts == null) return;
        this.ts = ts.longValue();
    }
}
