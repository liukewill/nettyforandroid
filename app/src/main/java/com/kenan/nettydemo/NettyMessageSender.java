package com.kenan.nettydemo;

import android.util.Log;

import com.kenan.nettyforandroid.NettyClient;
import com.kenan.nettyforandroid.netty.INettySender;
import com.kenan.nettyforandroid.protocol.NettyProtocolUtil;
import com.kenan.nettyforandroid.protocol.NettyRequest;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

/**
 * Created by kenan on 18/2/2.
 *
 * 请求发送器
 *
 * 长连接固定需要发送
 * 1. 验证sendAuth()
 * 2. 心跳sendHeartBeat()
 *
 * 项目中业务逻辑请求发送，可参考
 * sendNewOrderResponse()
 */

public class NettyMessageSender implements INettySender {

    public static com.kenan.nettyforandroid.NettyClient nettyClient= com.kenan.nettyforandroid.NettyClient.getInstance();


    public void sendHeartBeat(){

        NettyRequest nettyRequest = NettyRequestFactory.getHeartBeatRequest();
        ChannelBuffer channelBuffer = NettyProtocolUtil.encodeRequest(NettyRequestFactory.ENCODE.UTF_8, nettyRequest);
        Log.i(NettyClient.TAG, nettyRequest.toString());
        nettyClient.sendMessage(channelBuffer, new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {

                } else {

                }
            }
        });
    }

    public void sendAuth(){
        NettyRequest nettyRequest=NettyRequestFactory.getAuthRequest();
        ChannelBuffer channelBuffer=NettyProtocolUtil.encodeRequest(NettyRequestFactory.ENCODE.UTF_8, nettyRequest);
        Log.i(NettyClient.TAG,nettyRequest.toString());
        nettyClient.sendMessage(channelBuffer, new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if(channelFuture.isSuccess()){

                }else{
                }
            }
        });
    }

    public static void sendNewOrderResponse(){
        NettyRequest nettyRequest=NettyRequestFactory.getOrderRequest();
        ChannelBuffer channelBuffer=NettyProtocolUtil.encodeRequest(NettyRequestFactory.ENCODE.UTF_8, nettyRequest);
        Log.i(NettyClient.TAG,nettyRequest.toString());
        nettyClient.sendMessage(channelBuffer, new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if(channelFuture.isSuccess()){

                }else{
                }
            }
        });
    }
}
