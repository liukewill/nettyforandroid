package com.kenan.nettyforandroid.netty;

/**
 * Created by kenan on 18/1/25.
 * 长连接 消息发送
 */

public interface INettySender {
    /**
     * 验证 连接成功后进行
     */
    void sendAuth();

    /**
     * 发送心跳 验证通过后进行
     */
    void sendHeartBeat();
}
