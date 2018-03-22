package com.kenan.nettydemo;

import com.kenan.nettydemo.utils.MD5Utils;
import com.kenan.nettyforandroid.protocol.NettyProtocolUtil;
import com.kenan.nettyforandroid.protocol.NettyRequest;

import org.jboss.netty.buffer.ChannelBuffer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kenan on 17/11/9.
 *
 * 请求编码工厂类
 * 验证请求 getAuthRequest()
 * 心跳请求 getHeartBeatRequest()
 * ...
 * 本例中扩展了项目中业务逻辑需要的字段
 * 可参考本例 自行扩展
 */

public class NettyRequestFactory {

    /**
     * 编码类型
     */
    public interface ENCODE{
        byte UTF_8=0;
        byte GBK=1;
        byte GB2312=2;
        byte ISO88591=3;
    }

    /**
     * 加密类型
     */
    public interface ENCRYPT{
        byte NO_ENCRYPT=0;
        byte ENCRYPT=1;
    }

    /**
     * 命令
     */
    public interface COMMOND{
        byte HEART_BEAT=1;
        byte AUTH=2;
        byte NEW_ORDER=3;
        byte NEW_ORDER_RESPONSE=4;
    }

    /**
     * 扩展字段1
     */
    public interface EXTEND1{
        byte NONE=0;
    }

    /**
     * 扩展字段2
     */
    public interface EXTEND2{
        byte NONE=0;
    }


    /**
     * 业务请求自定义编码
     * 可结合项目自定义实现
     */
    public interface CONSTANT{
        String HEART_BEAT_KEY="heartbeat";
        String HEART_BEAT_VALUE="k";
        String TICKET_KEY="ticket";
        String CUID_KEY="cuid";
        String WID_KEY="wid";
        String CODE_KEY="code";
        String TIME_KEY="time";
        String _757b="757b";
    }


    public static NettyRequest getHeartBeatRequest(){
        Map<String,String> pack=new HashMap<>();
        pack.put(CONSTANT.HEART_BEAT_KEY, CONSTANT.HEART_BEAT_VALUE);
        pack.put(CONSTANT.TICKET_KEY,getTicket());

        NettyRequest nettyRequest=new NettyRequest.Builder()
                .setEncode(ENCODE.UTF_8)
                .setEncrypt(ENCRYPT.NO_ENCRYPT)
                .setExtend1(EXTEND1.NONE)
                .setExtend2(EXTEND2.NONE)
                .setSessionid(getSessionId())
                .setCommand(COMMOND.HEART_BEAT)
                .setLength(getLength(pack))
                .setParams(pack)
                .build();

        return nettyRequest;
    }

    public static NettyRequest getAuthRequest(){
        Map<String,String> pack=new HashMap<>();
        pack.put(CONSTANT.CUID_KEY,getCuid());
        pack.put(CONSTANT.WID_KEY,getWid());
        pack.put(CONSTANT.CODE_KEY,getCode());
        pack.put(CONSTANT.TIME_KEY,getTimestamp_10()+"");

        NettyRequest nettyRequest=new NettyRequest.Builder()
                .setEncode(ENCODE.UTF_8)
                .setEncrypt(ENCRYPT.NO_ENCRYPT)
                .setExtend1(EXTEND1.NONE)
                .setExtend2(EXTEND2.NONE)
                .setSessionid(getSessionId())
                .setCommand(COMMOND.AUTH)
                .setLength(getLength(pack))
                .setParams(pack)
                .build();

        return nettyRequest;
    }

    public static NettyRequest getOrderRequest(){
        Map<String,String> pack=new HashMap<>();
        pack.put(CONSTANT.HEART_BEAT_KEY, CONSTANT.HEART_BEAT_VALUE);
        pack.put(CONSTANT.TICKET_KEY,getTicket());

        NettyRequest nettyRequest=new NettyRequest.Builder()
                .setEncode(ENCODE.UTF_8)
                .setEncrypt(ENCRYPT.NO_ENCRYPT)
                .setExtend1(EXTEND1.NONE)
                .setExtend2(EXTEND2.NONE)
                .setSessionid(getSessionId())
                .setCommand(COMMOND.NEW_ORDER_RESPONSE)
                .setLength(getLength(pack))
                .setParams(pack)
                .build();

        return nettyRequest;
    }


    public static int getSessionId(){
        return getTimestamp_10();
    }

    public static int getTimestamp_10(){
        long time= System.currentTimeMillis()/1000;
        return  new Long(time).intValue();
    }

    public static int getLength(Map map){
        ChannelBuffer channelBuffer= NettyProtocolUtil.encodePack(0,map);
        return  channelBuffer.readableBytes();
    }

    public static String getCode(){
        String md5 = MD5Utils.getMD5String(getCuid() + getWid() + getTimestamp_10() + CONSTANT._757b);
        String sub="";
        try{
            sub=md5.substring(8,16);
        }catch (Exception e){
            e.printStackTrace();
        }
        return sub;
    }


    /**
     * 业务字段  可结合实际项目扩展
     * @return
     */
    public static String getTicket(){
        //业务字段
        return  "Ticket";
    }

    public static void setTicket(String ticket){
        //业务字段
    }

    public static String getCuid(){
        //业务字段
        return "cuid";
    }

    public static String getWid(){
        //业务字段
        return "wid";
    }


}
