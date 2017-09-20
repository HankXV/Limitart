[![](https://img.shields.io/badge/maven-v2.0--release-green.svg)](https://mvnrepository.com/artifact/org.slingerxv/limitart)
[![](https://img.shields.io/badge/license-Apache%202-green.svg)](http://www.apache.org/licenses/LICENSE-2.0)
![](https://img.shields.io/badge/jdk-1.8-green.svg)
# 快速开始
### Maven
	<dependency>
	    <groupId>org.slingerxv</groupId>
	    <artifactId>limitart</artifactId>
	    <version>2.0-release</version>
	</dependency>
### Gradle
	compile 'org.slingerxv:limitart:2.0-release'
	
首先，我们需要定义一个网络通信的消息类

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

再创建一个处理消息的处理器

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

初始化一个客户端，在客户端链接验证通过后发送消息给服务器

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
如果你不使用此框架而是其他地方的客户端，你需要了解此框架的默认链接过程和编码模式（C#可以参考另一个项目：`LimitartCS`）。

	客户端--socket-->服务器--发送验证码-->客户端--解析结果-->服务器--检查并发送成功消息-->客户端
		
完成此过程后，服务器和客户端都会触发`onConnectionEffective`回调。如果客户端在一定时间内未完成验证，其链接会被踢掉。其中链接验证消息参考：`org.slingerxv.limitart.net.binary.message.impl.validate`包，验证码加密解密参考`org.slingerxv.limitart.util.SymmetricEncryptionUtil`<br>
消息二进制编码为：

	消息长度(short,包含消息体长度+2)+消息ID(short)+消息体
	
# 更新日志
## v2.0.1-release
	1.修复热加载jar包不能替换旧jar包的问题
	2.热加载的脚本，如果父类是class，那么不能引用父类protected的字段，作用域由于加载器不同而无法引用，建议使用public方法获取或者把父类字段定义为public
	3.状态机计时从State移动到状态机本身
## v2.0-release
	1.增加敏感词过滤(BadWordUtil)
	2.语言本地化(I18NStrings)
	3.自定义编码部分增加了protobuf的压缩算法
	4.ConsoleServer改名为TelnetServer
	5.增加ProtoBuf服务器支持
## v2.0-alpha
	1.注册消息通过Handler直接注册，而不用指定消息Id和消息本身
	2.可以通过扫描包的形式直接注册所有消息
	3.消息可以自动序列化了，使用者不用关心序列化过程
	4.增加排行榜数据结构
	5.回调函数离散化和Lambda化
	6.增加扑克和德州扑克的部分操作
	7.使用注解方式处理消息(beta版本)
	8.增加了jar包脚本的加载和FTP远程加载jar包的方式
	9.增加了2种消息队列
	10.增加了消息队列组
	11.增加了Telnet服务器(ConsoleServer)
	12.增加心跳检查和消息接收速度检查