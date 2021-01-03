package org.dreamcat.common.hc.okhttp;

/**
 * Create by tuke on 2018/10/16
 */
public interface ProgressListener {

    void onProgress(long total, long current, long count, boolean done);
}
