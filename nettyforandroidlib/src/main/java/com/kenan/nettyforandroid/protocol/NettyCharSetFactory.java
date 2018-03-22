package com.kenan.nettyforandroid.protocol;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

/**
 * Created by kenan on 17/11/6.
 */

public class NettyCharSetFactory {

    static String[] charsetArray={"UTF-8","GBK","GB2312","ISO8859-1"};
    /**
     * ：0：UTF-8，1：GBK，2：GB2312，3：ISO8859-1
     * @param encode
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public static Charset getCharset(int  encode)  {

        Charset charset=null;

        try {
            switch(encode)
            {
                case 0:
                    charset = Charset.forName(charsetArray[0]);
                    break;
                case 1:
                    charset = Charset.forName(charsetArray[1]);
                    break;
                case 2:
                    charset = Charset.forName(charsetArray[2]);
                    break;
                case 3:
                    charset = Charset.forName(charsetArray[3]);
                    break;
            }

        } catch (IllegalCharsetNameException e) {
            e.printStackTrace();
        } catch (UnsupportedCharsetException e) {
            e.printStackTrace();
        }
        return charset;
    }
}
