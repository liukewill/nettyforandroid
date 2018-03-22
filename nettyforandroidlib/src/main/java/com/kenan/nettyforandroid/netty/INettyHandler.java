package com.kenan.nettyforandroid.netty;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;

/**
 * Created by kenan on 18/1/25.
 * 长连接 消息回调
 */

public interface INettyHandler {
    /**
     * 连接成功
     */
    void onConnectSuccess();

    /**
     * 连接失败
     */
    void onConnectFail();

    /**
     * 消息发送成功
     * @param ctx
     * @param e
     */
    void onSuccess(ChannelHandlerContext ctx, MessageEvent e);

    /**
     * 消息发送失败
     * @param ctx
     * @param e
     */
    void onError(ChannelHandlerContext ctx, ExceptionEvent e);

    /**
     * 长连接断开
     * @param ctx
     * @param e
     */
    void onClosed(ChannelHandlerContext ctx, ChannelStateEvent e);
}
