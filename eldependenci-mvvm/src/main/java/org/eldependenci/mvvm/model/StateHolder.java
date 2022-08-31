package org.eldependenci.mvvm.model;

/**
 * 狀態屬性管理器,
 */
public interface StateHolder {

    /**
     * 手動通知狀態變更，在設置為手動更新時需要使用
     */
    void notifyStateChanged();

}
