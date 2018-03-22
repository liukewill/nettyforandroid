package com.kenan.nettyforandroid;

import android.content.Context;
import android.util.Log;

import com.kenan.nettyforandroid.netty.INettyHandler;
import com.kenan.nettyforandroid.netty.INettySender;
import com.kenan.nettyforandroid.netty.NettyClientDecoder;
import com.kenan.nettyforandroid.netty.NettyClientPipelineFactory;
import com.kenan.nettyforandroid.timer.TimerSchedule;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kenan on 17/11/6.
 * 长连接客户端流程：
 * 1.连接
 * 2.授权
 * 3.心跳
 * 4.发送消息Request  数据打包
 * 5.接收消息Response 数据解析
 *
 * 长连接操作：
 * 1.start() 启动
 * 2.close() 关闭
 * 3.sendMessage() 向服务器发送消息
 * 4.startHeartBeat() 启动心跳定时器 建议验证通过后，回调中开启
 * 5.checkStatus()  用于外部定时检测
 *
 * 协议
 * 项目中默认的数据协议扩展性较好，如有需求可自行定义数据
 * 参考：NettyRequest NettyResponse
 */

public class NettyClient implements INettySender,INettyHandler {

    public static String TAG = "NettyClient";

    /**
     * Netty for android context
     */
    public Context context;

    /**
     * 长连接 通道
     */
    protected Channel channel;


    /**
     *长连接 NIO操作
     */
    protected ChannelFuture future;


    /**
     * netty channel工厂
     */
    protected NioClientSocketChannelFactory clientSocketChannelFactory;

    /**
     *netty 启动器
     */
    protected ClientBootstrap clientBootstrap;//netty长连接启动器

    /**
     * Socket ip
     */
    public String HOST;

    /**
     * Socket port
     */
    public int PORT=-1;


    /**
     * Socket 心跳 default 10s
     */
    public long hear_beat_time = 10 * 1000;


    /**
     * 长连接消息回调 需要自定义
     */
    protected INettyHandler nettyHandler;

    /**
     * 服务端解码器 需要自定义
     */
    protected FrameDecoder nettyDecoder =new NettyClientDecoder();

    /**
     * 本地记录是否连接，不可靠，用于记录
     */
    protected boolean isConnect = false;

    /**
     * 单线程池 启动长连接
     */
    protected ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

    /**
     * 心跳定时器 default hear_beat_time 10s
     */
    protected TimerSchedule mRingTimerSchedule;

    /**
     * NettyClient单例
     */
    private static NettyClient nettyClient = new NettyClient();

    /**
     * Netty 发送验证和心跳
     */
    private INettySender iNettySender;

    private NettyClient() {
    }


    public static NettyClient getInstance() {
        return nettyClient;
    }

    /**
     * 启动长连接
     * @throws Exception 参数异常检测
     */
    public void start() throws IllegalArgumentException {
        Log.i(TAG, "run-" + "***********start**********");

        if(HOST==null){
            throw new IllegalArgumentException("please set the HOST before start()!");
        }

        if(PORT==-1){
            throw new IllegalArgumentException("please set the PORT before start()!");
        }

        if(nettyDecoder ==null){
            throw new IllegalArgumentException("please set the nettyDecoder before start()!");
        }

        if(nettyHandler ==null){
            throw new IllegalArgumentException("please set the nettyHandler before start()!");
        }

        if(iNettySender==null){
            throw new IllegalArgumentException("please set the iNettySender before start()!");
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run-" + System.currentTimeMillis());
                startProcess();
            }
        };

        singleThreadExecutor.execute(runnable);
    }

    /**
     * 关闭长连接
     */
    public void close() {
        Log.i(TAG, "run-" + "************close**********");
        try {
            shutdown(future);
            stopHeartBeat();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 向服务器发送消息
     * 1.验证
     * 2.心跳
     * 3.业务消息
     * 都通过此方法发送
     * @param channelBuffer
     * @param listener
     * @return
     */
    public boolean sendMessage(ChannelBuffer channelBuffer, ChannelFutureListener listener) {
        boolean flag = channel != null && isConnect;
        if (flag)
            channel.write(channelBuffer).addListener(listener);
        return flag;
    }

    /**
     * 动态控制 开关，心跳时间
     * 可用于外部 定时或动态 检测长连接状态
     * @param canopen 动态控制开关
     */
    public void checkStatus(boolean canopen,long hear_beat_time) {
        try {
            Log.i(TAG, "isConnect:" + isConnect() + "****isOpen:" + canopen);
            this.hear_beat_time=hear_beat_time;
            if (isConnect()) {
                if (!canopen) {
                    this.close();
                }
            } else {
                if (canopen) {
                    this.start();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动流程
     * 1.关闭上一个连接
     * 2.重新create factory ，bootstrap （为避免链路复用导致bug）
     * 3.连接
     */
    private void startProcess() {
        try {
            //重启前关闭强停前一个连接
            if(future!=null) {
                //关闭
                shutdown(future);

                //避免同步操作future导致异常case
                Thread.sleep(500);
            }

            //channel 工厂
            clientSocketChannelFactory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());

            //client 启动器
            clientBootstrap = new ClientBootstrap(clientSocketChannelFactory);

            //执行connect
            ChannelFuture future = connect();
            Log.i(TAG, "future state is " + future.isSuccess());

            //重置channel
            channel = future.getChannel();

            //连接成功 发送验证
            if (future.isSuccess()) {
                nettyHandler.onConnectSuccess();
                sendAuth();
            }else{
                nettyHandler.onConnectFail();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Netty连接Api
     * @return
     * @throws Exception
     */
    private synchronized ChannelFuture connect() throws Exception {
        if (!isConnect()) {

            /**
             * 注意：由于XLClientHandler中有状态的成员变量，因此不能采用默认共享ChannelPipeline的方式
             * 例如，下面的代码形式是错误的：
             * ChannelPipeline pipeline=clientBootstrap.getPipeline();
             * pipeline.addLast("handler", new NettyClientHandler());
             */
            clientBootstrap.setPipelineFactory(new NettyClientPipelineFactory(nettyDecoder,this)); //只能这样设置
            /**
             * 请注意，这里不存在使用“child.”前缀的配置项，客户端的SocketChannel实例不存在父级Channel对象
             */
            clientBootstrap.setOption("tcpNoDelay", true);
            clientBootstrap.setOption("keepAlive", true);

            future = clientBootstrap.connect(new InetSocketAddress(HOST, PORT));
            /**
             * 阻塞式的等待，直到ChannelFuture对象返回这个连接操作的成功或失败状态
             */
            future.awaitUninterruptibly();
            /**
             * 如果连接失败，我们将打印连接失败的原因。
             * 如果连接操作没有成功或者被取消，ChannelFuture对象的getCause()方法将返回连接失败的原因。
             */
            if (!future.isSuccess()) {
                future.getCause().printStackTrace();
                isConnect = false;
            } else {
                Log.i(TAG, "client is connected to server " + HOST + ":" + PORT);
                isConnect = true;
            }
        }
        return future;
    }

    /**
     * 关闭客户端
     *
     * @param future
     * @throws Exception
     */
    private void shutdown(ChannelFuture future) throws Exception {
        try {
            /**
             * 主动关闭客户端连接，会阻塞等待直到通道关闭
             */
            future.getChannel().close().awaitUninterruptibly();
            //future.getChannel().getCloseFuture().awaitUninterruptibly();
            /**
             * 释放ChannelFactory通道工厂使用的资源。
             * 这一步仅需要调用 releaseExternalResources()方法即可。
             * 包括NIO Secector和线程池在内的所有资源将被自动的关闭和终止。
             */
            clientBootstrap.releaseExternalResources();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, e.getMessage());
        } finally {
            Log.i(TAG, "client is shutdown to server " + HOST + ":" + PORT);
        }
    }


    public boolean isConnect() {
        if (channel == null) {
            return false;
        }
        return channel.isConnected();
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }

    public void sendHeartBeat() {
        if(iNettySender!=null) {
           iNettySender.sendHeartBeat();
        }
    }

    public void sendAuth() {
        if(iNettySender!=null) {
            iNettySender.sendAuth();
        }
    }

    public void stopHeartBeat() {
        mRingTimerSchedule.stop();
    }

    public void startHeartBeat() {
        Log.i(TAG, "start--HB-TIMER---" + hear_beat_time);
        if(mRingTimerSchedule==null) {
            mRingTimerSchedule = new TimerSchedule(context, mTimerScheduleCallback);
        }

        mRingTimerSchedule.start(5 * 1000, hear_beat_time, hear_beat_time, hear_beat_time);
    }

    private TimerSchedule.TimerScheduleCallback mTimerScheduleCallback = new TimerSchedule.TimerScheduleCallback() {
        @Override
        public void doSchedule() {
            Log.i(TAG, "send-HB------------------" + hear_beat_time);
            iNettySender.sendHeartBeat();
        }
    };


    public String getTAG() {
        return TAG;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    public String getHOST() {
        return HOST;
    }

    public void setHOST(String HOST) {
        this.HOST = HOST;
    }

    public int getPORT() {
        return PORT;
    }

    public void setPORT(int PORT) {
        this.PORT = PORT;
    }

    public long getHear_beat_time() {
        return hear_beat_time;
    }

    public  void setHear_beat_time(long hear_beat_time) {
        this.hear_beat_time = hear_beat_time;
    }
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public FrameDecoder getNettyDecoder() {
        return nettyDecoder;
    }

    public void setNettyDecoder(FrameDecoder nettyDecoder) {
        this.nettyDecoder = nettyDecoder;
    }

    public INettyHandler getNettyHandler() {
        return nettyHandler;
    }

    public void setNettyHandler(INettyHandler nettyHandler) {
        this.nettyHandler = nettyHandler;
    }

    public INettySender getNettySender() {
        return iNettySender;
    }

    public void setNettySender(INettySender iNettySender) {
        this.iNettySender = iNettySender;
    }

    @Override
    public void onConnectSuccess() {
        this.nettyHandler.onConnectSuccess();
    }

    @Override
    public void onConnectFail() {
        this.nettyHandler.onConnectFail();
    }

    @Override
    public void onSuccess(ChannelHandlerContext ctx, MessageEvent e) {
        this.nettyHandler.onSuccess(ctx,e);
    }

    @Override
    public void onError(ChannelHandlerContext ctx, ExceptionEvent e) {
        this.nettyHandler.onError(ctx,e);
    }

    @Override
    public void onClosed(ChannelHandlerContext ctx, ChannelStateEvent e) {
        this.nettyHandler.onClosed(ctx,e);
        this.setConnect(false);
    }
}
