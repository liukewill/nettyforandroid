package com.kenan.nettyforandroid.netty;

import android.util.Log;

import com.kenan.nettyforandroid.NettyClient;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by kenan on 17/11/6.
 * 消息处理handler
 * 所有服务器消息均走 messageReceived 分发
 */

public class NettyClientHandler extends SimpleChannelHandler {


    private List<INettyHandler> listeners =new ArrayList<>();

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        notifySuccess(ctx,e);
        processMethod(ctx, e); //处理方式一
        Log.i(NettyClient.TAG,e.toString());
    }

    /**
     * @param ctx
     * @param e
     * @throws Exception
     */
    public void processMethod(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        notifyError(ctx,e);
        e.getCause().printStackTrace();
        ctx.getChannel().close();
        Log.i(NettyClient.TAG,"channelClosed--exception"+e.toString());

    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelClosed(ctx, e);
        notifyClosed(ctx,e);
        Log.i(NettyClient.TAG,"channelClosed"+e.toString());
    }


    public void addListener(INettyHandler listener){
        if(listener!=null&&!listeners.contains(listener)){
            listeners.add(listener);
        }
    }

    public void removeListener(INettyHandler listener){
        if(listener!=null&& listeners.contains(listener)){
            listeners.remove(listener);
        }
    }

    private void notifySuccess(ChannelHandlerContext ctx, MessageEvent e){
        for (INettyHandler nettyHandlerListener : listeners) {
            nettyHandlerListener.onSuccess(ctx,e);
        }
    }

    private void notifyError(ChannelHandlerContext ctx, ExceptionEvent e){
        for (INettyHandler nettyHandlerListener : listeners) {
            nettyHandlerListener.onError(ctx,e);
        }
    }

    private void notifyClosed(ChannelHandlerContext ctx, ChannelStateEvent e){
        for (INettyHandler nettyHandlerListener : listeners) {
            nettyHandlerListener.onClosed(ctx,e);
        }
    }


}
