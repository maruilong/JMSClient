package com.xinyuan.comm.enums;


/**
 * @author liang
 */

public enum DownloadState {


    /**
     * 已下载
     */
    DOWNLOADED(1, "已下载"),
    /**
     * 已执行
     */
    EXECUTED(2, "已执行");

    private Integer state;

    private String name;

    public DownloadState getByState(Integer state) {
        for (DownloadState downloadState : DownloadState.values()) {
            if (downloadState.state.equals(state)) {
                return downloadState;
            }
        }
        return DOWNLOADED;
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

    DownloadState(Integer state, String name) {
        this.state = state;
        this.name = name;
    }

}
