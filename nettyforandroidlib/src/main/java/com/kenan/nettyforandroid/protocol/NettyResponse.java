package com.kenan.nettyforandroid.protocol;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kenan on 17/11/6.
 */

public class NettyResponse implements Serializable {
    private byte encode;// 数据编码格式。已定义：0：UTF-8，1：GBK，2：GB2312，3：ISO8859-1
    private byte encrypt;// 加密类型。0表示不加密
    private byte extend1;// 用于扩展协议。暂未定义任何值
    private byte extend2;// 用于扩展协议。暂未定义任何值
    private int sessionid;// 会话ID
    private int result;// 结果码
    private int length;// 数据包长

    private Map<String,String> values=new HashMap<String, String>();

    private String ip;

    public void setValue(String key, String value){
        values.put(key, value);
    }

    public String getValue(String key){
        if (key==null) {
            return null;
        }
        return values.get(key);
    }

    public byte getEncode() {
        return encode;
    }

    public void setEncode(byte encode) {
        this.encode = encode;
    }

    public byte getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(byte encrypt) {
        this.encrypt = encrypt;
    }

    public byte getExtend1() {
        return extend1;
    }

    public void setExtend1(byte extend1) {
        this.extend1 = extend1;
    }

    public byte getExtend2() {
        return extend2;
    }

    public void setExtend2(byte extend2) {
        this.extend2 = extend2;
    }

    public int getSessionid() {
        return sessionid;
    }

    public void setSessionid(int sessionid) {
        this.sessionid = sessionid;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "NettyResponse [encodePack=" + encode + ", encrypt=" + encrypt + ", extend1=" + extend1 + ", extend2=" + extend2
                + ", sessionid=" + sessionid + ", result=" + result + ", length=" + length + ", values=" + values + ", ip=" + ip + "]";
    }

}
