package com.kenan.nettyforandroid.netty;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

/**
 * Created by kenan on 17/11/6.
 * Netty通道工厂
 * 集成  解码器Decoder  @see NettyClientDecoder
 *      消息处理器Handler @see NettyClientHandler
 *
 * 在NettyClient中初始化
 */

public class NettyClientPipelineFactory implements ChannelPipelineFactory {
    public static final String HANDLER="handler";
    public static final String DECODER="nettyDecoder";
    INettyHandler nettyHandlerListener=null;
    FrameDecoder frameDecoder;

    public NettyClientPipelineFactory(FrameDecoder frameDecoder, INettyHandler handlerListener){
        this.nettyHandlerListener=handlerListener;
        this.frameDecoder=frameDecoder;
    }
    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline= Channels.pipeline();

        //集成解码器decoder，解决数据
        pipeline.addLast(DECODER, frameDecoder);

        //集成消息处理器
        NettyClientHandler nettyClientHandler=new NettyClientHandler();

        //添加消息处理回调到 NettyClient
        nettyClientHandler.addListener(nettyHandlerListener);

        pipeline.addLast(HANDLER, nettyClientHandler);

        return pipeline;
    }

}
