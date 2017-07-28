[![Codacy Badge](https://api.codacy.com/project/badge/Grade/98c8a0ea3b43404aa8a820078e2c6e12)](https://www.codacy.com/app/104381832/Limitart?utm_source=github.com&utm_medium=referral&utm_content=HankXV/Limitart&utm_campaign=badger)
[![Build Status](https://travis-ci.org/HankXV/Limitart.svg?branch=master)](https://travis-ci.org/HankXV/Limitart)
# 什么是Limitart?
一个帮助您快速搭建起游戏服务器或者是中小型网络服务的框架
## 环境要求
Jdk8或以上
# 快速开始
首先，我们需要定义一个网络通信的消息结构体

	```java
	
		public class BinaryMessageDemo extends Message {
			// transfer of information
			public String info;
		
			//  message id
			@Override
			public short getMessageId() {
				return 1;
			}
		}
	
	```

再创建一个处理上面消息的处理器

	```java
	
		public class BinaryHandlerDemo implements IHandler<BinaryMessageDemo> {
		
			@Override
			public void handle(BinaryMessageDemo msg) {
				System.out.println("server received message:" + msg.info);
			}
		
		}
		
	```

初始化一个消息工厂把消息处理器注册进去

	```java
	
		MessageFactory factory = new MessageFactory().registerMsg(BinaryHandlerDemo.class);
			
	```

最后实例化一个服务器并且开启服务

	```java
	
				new BinaryServer.BinaryServerBuilder()
					// port
					.addressPair(new AddressPair(8888))
					// register factory
					.factory(messageFactory).build();
					.startServer();
			
	```

初始化一个客户端，在客户端链接验证通过后发送上面的消息给服务器

	```java
	
				new BinaryClient.BinaryClientBuilder()
					.remoteAddress(new AddressPair("127.0.0.1", 8888))
					.onConnectionEffective(c -> {
						BinaryMessageDemo message = new BinaryMessageDemo();
						message.info = "Hello Limitart!";
						c.sendMessage(message, null);
					}).build()
					.connect();
					
	```

最后服务器收到了消息

	```
	
		server received message:Hello Limitart!
	
	```
	
# 消息编码
如果你不使用此框架里而是其他语言的客户端，你需要了解次框架的默认链接过程和编码模式。

	`客户端--socket-->服务器--发送验证码-->客户端--解析结果-->服务器--检查并发送成功消息-->客户端`
		
完成此过程后，服务器和客户端都会触发`onConnectionEffective`回调。其中链接验证消息参考：`org.slingerxv.limitart.net.binary.message.impl.validate`包，验证码加密解密参考`org.slingerxv.limitart.util.SymmetricEncryptionUtil`<br>
消息二进制编码为：

	```
	消息长度(short,包含消息体长度+2)+消息ID(short)+消息体
	```
# 更新日志
## v2.0-alpha
