package com.kenan.nettydemo;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.kenan.nettyforandroid.NettyClient;
import com.kenan.nettyforandroid.netty.INettyHandler;
import com.kenan.nettyforandroid.protocol.NettyResponse;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;


/**
 * Created by kenan on 18/2/2.
 *
 * 消息接收器
 *
 */

public class NettyMessageHandler implements INettyHandler {

    Handler handler=new Handler(Looper.getMainLooper());//消息回调到主线程

    @Override
    public void onConnectSuccess() {

    }

    @Override
    public void onConnectFail() {

    }

    @Override
    public void onSuccess(ChannelHandlerContext ctx, MessageEvent e) {
        Log.i(NettyClient.TAG,e.getMessage().toString());

        final NettyResponse response=(NettyResponse)e.getMessage();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(NettyRequestFactory.COMMOND.AUTH==response.getResult()){
                    NettyRequestFactory.setTicket(response.getValue(NettyRequestFactory.CONSTANT.TICKET_KEY));
                    Log.i(NettyClient.TAG,"AUTH-RESPONSE");
                    //验证通过  开启心跳
                    com.kenan.nettyforandroid.NettyClient.getInstance().startHeartBeat();

                }

                if(NettyRequestFactory.COMMOND.HEART_BEAT==response.getResult()){
                    //心跳通过
                    Log.i(NettyClient.TAG,"HB-RESPONSE");
                }

                if(NettyRequestFactory.COMMOND.NEW_ORDER==response.getResult()){
                    //业务逻辑编码
                    Log.i(NettyClient.TAG,"NEW-ORDER-RESPONSE");
                    NettyMessageSender.sendNewOrderResponse();
                }
            }
        });

    }

    @Override
    public void onError(ChannelHandlerContext ctx, ExceptionEvent e) {

    }

    @Override
    public void onClosed(ChannelHandlerContext ctx, ChannelStateEvent e) {

    }
}
