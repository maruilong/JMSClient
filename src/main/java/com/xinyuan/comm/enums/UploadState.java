package com.xinyuan.comm.enums;

public enum UploadState {
    /**
     * 未发送
     */
    UNSENT(1, "未发送"),

    /**
     * 已发送
     */
    SENT(2, "已发送"),

    /**
     * 已下载
     */
    DOWNLOADED(3, "已下载"),

    /**
     * 已执行
     */
    EXECUTED(4, "已执行");

    private Integer state;

    private String name;

    public UploadState getByState(Integer state) {
        for (UploadState uploadState : UploadState.values()) {
            if (uploadState.state.equals(state)) {
                return uploadState;
            }
        }
        return UNSENT;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    UploadState(Integer state, String name) {
        this.state = state;
        this.name = name;
    }
}
