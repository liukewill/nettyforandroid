# nettyforandroid
A socket client used in android base on netty

## 基于Netty实现长连接

1. 结合Android项目使用
2. 封装了netty Api,简化接入流程
3. `NettyClient`支持连接，关闭，验证，心跳，动态重启，异常监听，数据打包与发送，接收与解析。
4. 内置可扩展数据协议，支持自定义数据协议
5. `NettyClient`客户端设计为单例，建议单进程维护一个长连接。若项目需要实现多个长连接，请在多进程环境执行。


### 初始化
	
				
	NettyClientBuilder nettyClientBuilder=new NettyClientBuilder();//客户端构建器
	nettyClientBuilder.setHOST(HOST)//设置IP
                .setPORT(PORT)//设置端口
                .setHear_beat_time(heat_beat_time)//设置心跳间隔
                .setiNettySender(new NettyMessageSender())//消息发送器
                .setiNettyHandler(new NettyMessageHandler())//消息接收器
                .setContext(context)//设置Context
                .build();

### 启动 
	NettyClient.getInstance().start();
### 关闭
	NettyClient.getInstance().close();
		
### 消息发送
	NettyClient.getInstance().sendMessage(//Your Message);
      
### 消息回调
	public interface INettyHandler {
    	void onConnectSuccess();//连接成功 
    	void onConnectFail();连接失败
    	void onSuccess(ChannelHandlerContext ctx, MessageEvent e);//消息success
    	void onError(ChannelHandlerContext ctx, ExceptionEvent e);//消息error
    	void onClosed(ChannelHandlerContext ctx, ChannelStateEvent e);//长连接关闭
	}  
### 其他使用
参照NettyDemo   
      
      
## Usage
Setp 1.Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	        compile 'com.github.liukewill:nettyforandroid:1.0.0'
	}
	
Proguard

	-keep class com.kenan.nettyforandroid.* {*;}


	
