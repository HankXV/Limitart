[![](https://img.shields.io/badge/maven-v2.0--alpha-green.svg)](https://mvnrepository.com/artifact/org.slingerxv/limitart)
[![](https://img.shields.io/badge/license-Apache%202-green.svg)](http://www.apache.org/licenses/LICENSE-2.0)
# 什么是Limitart?
一个帮助您快速搭建起游戏服务器的框架
# 怎么参与？
	群：662555451
[CONTRIBUTING](/CONTRIBUTING.md)
## 环境要求
Jdk8或以上
# 快速开始
### Maven
	<dependency>
	    <groupId>org.slingerxv</groupId>
	    <artifactId>limitart</artifactId>
	    <version>2.0-alpha</version>
	</dependency>
### Gradle
	compile 'org.slingerxv:limitart:2.0-alpha'
	
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
	
# 游戏服务器开发思路
1.首先你需要对接好网络通信<br>
2.建议把消息打成jar文件，多个进程间共享，防止消息不一致，无法正常通信<br>
3.根据游戏的不同类型来制定线程模型，在游戏中制定线程模型基本抱着两个目的。第一，IO或者复杂计算不能阻塞玩家的操作。第二，如果玩家有数据交互，他们应当在同一个线程<br>
4.根据2的参考方式制定了线程模型，然后在服务器的`dispatchMessage`回调方法里把消息分发到不同线程中<br>
5.如果你不做分发，直接在`dispatchMessage`中执行handler，那么就默认使用了Netty的work线程，他只保证了一个channel一定会在同一个线程运行。如果你所制作的游戏中玩家没有强力的数据交互，则可以使用默认线程，但在交互操作的时候需要注意线程问题<br>
6.线程间通信的消息队列推荐使用`DisruptorTaskQueue`，如果你有使用类似地图或者房间的线程需求(既一组需要交互的玩家要在一个线程里)，推荐使用`AutoGrowthTaskQueueGroup`，如果你不知道你该怎么使用线程，那么推荐你使用`FunctionalTaskQueueGroup`<br>
7.如果你需要使用控制台来操作服务器，那么可以使用`ConsoleServer`或者`HttpServer`嵌入游戏服务器中来进行交互<br>
8.作者不提倡滥用线程，所以请使用者预估好使用场景，再做相应的线程安排<br>
9.在`org.slingerxv.limitart.game`包下是属于游戏逻辑层的抽象，比如背包、道具、帮会、扑克等，后面会慢慢增加<br>
10.如果你要做排行榜，推荐使用`FrequencyReadRankMap`或`FrequencyWriteRankMap`，推荐排行榜存储量为10万数量级及以下<br>
11.游戏服务器的热更新请参考`org.slingerxv.limitart.script`<br>
12.游戏中常用的唯一Id生成请参考`org.slingerxv.limitart.util.UniqueIdUtil`<br>
13.带有`@beta`标记的代表此API是新加入的测试版<br>
# 更新日志
## v2.0-release
	1.增加敏感词过滤(BadWordUtil)
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