package com.kenan.nettydemo;

import android.content.Context;

import com.kenan.nettyforandroid.NettyClient;
import com.kenan.nettyforandroid.NettyClientBuilder;

/**
 * Created by kenan on 18/2/2.
 * 长连接客户端
 *
 * 1.设置端口 IP 心跳间隔
 * 2.构建消息请求器，消息接受器
 * 3.添加业务逻辑
 * 4.done
 *
 */

public class NettyManager {

    public static final String TAG="NETTY-CLIENT";

    /**
     * 长连接实例
     */
    public static NettyClient nettyClient=NettyClient.getInstance();

    /**
     * IP
     */
    public static final String HOST ="10.19.160.27";

    /**
     * PORT
     */
    public static final int PORT =8060;

    /**
     * 心跳时间 ms
     */
    public static long heat_beat_time =10*1000;


    private static NettyManager nettyManager=new NettyManager();

    private NettyManager(){
    }

    public static NettyManager getInstance(){
        return nettyManager;
    }

    /**
     * 初始化NettyClient
     * @param context
     */
    public void init(Context context){
        NettyClientBuilder nettyClientBuilder=new NettyClientBuilder();
        nettyClientBuilder.setHOST(HOST)
                .setPORT(PORT)
                .setHear_beat_time(heat_beat_time)
                .setiNettySender(new NettyMessageSender())
                .setiNettyHandler(new NettyMessageHandler())
                .setContext(context)
                .build();
    }


    /**
     * 开启
     */
    public void start(){
        try {
            nettyClient.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭
     */
    public void close(){
        nettyClient.close();
    }

    public NettyClient getNettyClient(){
        return nettyClient;
    }
}
