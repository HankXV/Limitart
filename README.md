[![Build Status](https://travis-ci.org/HankXV/Limitart.svg?branch=master)](https://travis-ci.org/HankXV/Limitart)
# 简介(Brief Introduction)
一个旨在帮助快速搭建**Java中小型游戏服务器**的框架，通信底层采用Netty4.X，数据库采用MySql相关(框架其实并未与Mysql产生太多耦合，但建议使用Mysql)，生产消费者采用Disruptor等。**有想共同参与的或者向我吐槽的，留言**。

This is a framework that designed to help to build a **Java Midrange Game Server** quickly. Its communicating interface based on Netty4.X, the database is using MySql (This framework is actually not much coupling with MySql, but it still recommonded to use MySql.), and the TaskQueue(MessageQueue) based on Disruptor. **If one have some advice or want be one of us, please leave any messages to us, and we are always welcome**
## 环境要求(Environment)
Jdk8或以上

Jdk8 or above.
# 快速开始(Quick start)
第一步我们需要确定服务器可以处理哪些消息。让我们来创建第一个消息。
First, we neet to determine which messages the server can handle. Let's create a message.
```java

	public class BinaryMessageDemo extends Message {
		// 传递的信息 transfer of information
		public String info;
	
		// 消息编号 message id
		@Override
		public short getMessageId() {
			return 1;
		}
	}

```
为上面的消息创建一个处理器，这里我们就简单的打印传输过来的内容即可。
Create a Handler for the message. Here, we just simply print the contents of transmission.
```java

	public class BinaryHandlerDemo implements IHandler<BinaryMessageDemo> {
	
		@Override
		public void handle(BinaryMessageDemo msg) {
			System.out.println("server received message:" + msg.info);
		}
	
	}
	
```
构造一个消息工厂，把消息的处理器注册进去。
Construct a `MessageFactory` that registers message's handler.
```java

	MessageFactory factory = new MessageFactory();
	factory.registerMsg(BinaryHandlerDemo.class);
		
```
最后初始化一个服务器实例并绑定。收工！
Finally, initialize a server instance and bind it. 
```java

		BinaryServer server = new BinaryServer.BinaryServerBuilder()
				// 指定端口 port
				.addressPair(new AddressPair(8888))
				// 注册消息  register factory
				.factory(messageFactory).build();
		server.startServer();
		
```
下面看看客户端。因为我们现在不让客户端处理消息只发送消息，所以我们创建一个消息处理器传进消息工厂就行。现在开始构造客户端，填写好服务器地址和端口，当然还有客户端名称。你可以选择是否重连，我们这里就不展示了。添加监听器`onConnectionEffective`并在里面写发送消息给服务器的代码。
Look at the client below. Because we do not allow client processing messages only send messages, so we create a message into the message processor factory on the line. Now build the client, fill in the server address and port, and, of course, the client name. You can choose whether or not to reconnect, and we don't show it here.Write the code that sends the message to the server in the listener `onConnectionEffective`.
```java

		MessageFactory factory = new MessageFactory();
		BinaryClient client = new BinaryClient.BinaryClientBuilder().remoteAddress(new AddressPair("127.0.0.1", 8888))
				.factory(factory).onConnectionEffective(c -> {
					BinaryMessageDemo message = new BinaryMessageDemo();
					message.info = "Hello Limitart!";
					try {
						c.sendMessage(message, null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}).build();
		client.connect();
				
```
收工！
To be finished！
```java

		BinaryClient client = new BinaryClient(config);
		client.connect();
		
```
让我们来看看效果吧,启动服务器，服务器绑定成功,启动客户端，客户端链接成功，并发送了消息,服务器验证链接成功并且收到了消息！是不是很棒！！！
Start the client, the client link is successful, and the message is sent.The server verified that the link was successful and received the message! cool!!!!
```

	server received message:Hello Limitart!

```
# 模块介绍(Package Introduction)
## 网络通信(Network Communication:net)
### 二进制服务器的链接过程(The Link Process Of The Binary Server)
客户端与服务器建立Socket链接之后服务器会发送加密验证码到客户端，客户端解密后发送结果到服务器，服务器验证完毕，Socket才会稳定下来。如果客户端长时间不发送正确的结果，则踢掉链接。这个措施主要是防止无效链接过多，增加恶意攻击服务器的成本。

When the client and the server establish the Socket link, the server sends the encrypted authentication code to the client, and the client decrypts the result and sends it to the server. After the server is verified, the Socket will be stabilized. If the client is not a long time to send the right results, then kicked off the link. This measure is mainly to prevent invalid links too much, increase the cost of malicious attack server.
### 二进制消息（Binary Message）
每个消息必须继承Message类，并且消息只支持基本类型和消息元类型(`MessageMeta`)以及前两者类型的List或数组。消息元类型就是所谓的消息里携带对象，List的长度不能超过一个short的长度。每个消息必须有一个唯一ID来标识，长度为一个short。消息里面的decode和encode编码必须顺序一致。二进制服务器构造的时候需要一个消息工厂(`MessageFactory`)，消息工厂里面需要初始化消息处理器(`IHandler`)。当二进制服务器收到消息时，会去寻找相应的消息处理器作为回调。
### 二进制消息默认编码（Binary Message default decode/encode）
message length(include message id,short)
	+
message id(short)
	+
message body
### 二进制消息ID(Binary Message ID)
由于消息的ID是一个short类型的，所以我们建议消息ID的确定方式为模块编号+模块内消息编号。比如登录模块ID为1，登录消息ID为1，那么编号就应该为0X0101。再举个例子，战斗模块的ID为10，攻击消息的ID为1，那么编号就应该为0X0A01。

Since the ID of the message is a short type, we recommend that the message ID be determined by module number + message number in the module. For example, the login module ID is 1, and the login message ID is 1, then the number should be 0X0101. For another example, the ID of the combat module is 10, and the ID of the attack message is 1, then the number should be 0X0A01.
### 二进制消息处理器（Message Handler）
每个处理器必须要实现`IHandler`接口，然后注册进消息工厂(`MessageFactory`)，IHandler被认为是单例模式，所以不要在`IHandler`的实现类里缓存任何非全局的数据。

Each processor must implement the `IHandler` interface, then register the message into the factory (`MessageFactory`), IHandler is considered to be a singleton, so do not cache any non global implementation of `IHandler` class data.
### 反射构造消息工厂(Constructing A MessageFactory By Reflection)
在实际开发中，总是去关心注册消息会显得很麻烦。这里提供了一个扫描包并且自动注册的功能(`MessageFactory.createByPackage`),这个方法会去扫描指定包内的所有`IHandler`(不会包含内部类和匿名类)。

In practical development, it is always troublesome to care about registration messages. Here's a scan package and automatic registration function (`MessageFactory.createByPackage`) that scans all the `IHandler` in the specified package (not including an internal class and an anonymous class).
### 二进制服务器配置(Binary Server Config)
`BinaryServerConfig`是构造二进制服务器(`BinaryServer`)必要的配置选项，主要用于确定端口、编码器/解码器以及链接验证码公钥(见上文链接过程中提到的验证码)。在二进制服务器(`BinaryServer`)中会有各种事件的回调函数，包括Netty原生回调和自定义回调。1.`onServerBind` 服务器绑定端口成功 。2.`onConnectionEffective` 客户端链接验证成功（一个链接是否有效，或者说要判断此链接的消息是否可以正式交由本服务器处理）。 3.`dispatchMessage` 分发消息，因为不同的应用，线程模型不同，所以我们需要把消息放到不同线程来执行(参考taskqueue)，并且可能会对消息有统计行为，比如arpg游戏的大概会按地图来分线程。

`BinaryServerConfig` is a binary structure server (`BinaryServer`) the necessary configuration options, mainly used to determine the port, encoder / decoder and link verification code key (see above link process mentioned in the verification code). In the binary server (`BinaryServer`), there are callback functions for various events, including Netty native callbacks and custom callbacks. 1.`onServerBind` server binding port successful. The 2.`onConnectionEffective` client link validation succeeds (whether a link is valid, or to judge whether the message of that link can be formally processed by the server). 3.`dispatchMessage` to distribute messages, because different applications, different threading models, so we need to put the message into the different thread to execute (see taskqueue), and may have the statistical behavior of the messages, such as the ARPG game will probably follow the map to thread.
### 二进制客户端配置(Binary Client Config)
`BinaryClient`的接口跟`BinaryServer`类似，通过构造函数传相应参数即可。
## RPC服务(RPC Service)
## 脚本(Script Hot Fix)
### Jar包脚本加载(JarScriptLoader)
### 文件脚本加载(FileScriptLoader)
## 消息队列(Task Queue)
很多异步任务都需要由消息队列做中间层来完成线程的跳转，游戏中应用到的场景可能是消息的分发和异步IO任务(如异步数据库保存)的处理，这里提供两种实现，`DisruptorTaskQueue`和`LinkedBlockingTaskQueue`，前者底层为Disruptor快速无锁的环形队列，吞吐量很高，后者为Java原生的并发阻塞队列，推荐使用前者。
## 消息队列组(Task Queue Group)
由于任务处理线程可能有动态增长的需要，这里加入一个简单的消息队列组来完成任务，其功能类似于Java自身的ThreadPoolExecutor,但是不同之处在于，消息队列组可以确定某个到来的任务只能由特定的线程执行，比如同一战场的玩家任务一定要在同一个线程执行，同一个地图的玩家任务也一定要在同一个线程执行，有交互行为的任务要在同一线程执行等。线程租中每个执行的单位需要继承`AutoGrowthEntity`以帮助他们找到自己所在的线程。
## 游戏常用集合类(Game Collections)
### 排行榜(IRankMap)
`IRankMap`目前有两个实现(`FrequencyReadRankMap`、`FrequencyWriteRankMap`)前者是需要随时更新排名信息的，后者是一次性结算排名的，不同场景使用不同实现。排行元数据需要实现`IRankObj`接口。
### 限制型Map(ConstraintMap)
Map内存Object类型，Map的Key为指定类型，此Map没有特殊性，只不过在接口上做了限制的处理，比如获取int类型为`getInt()`、获取byte为`getByte()`,这种Map在Json和上下文参数的应用上比较友好。
## 游戏常用功能抽象(Abstract Game Function)
## 常用工具(Utils)
### 唯一ID生成工具(UniqueIdUtil)
生成Java自带的UUID或者使用`createUUID`来创建一个long型的唯一ID，后者的唯一ID按照区域划分，最多支持16位区域数量和每秒16位数量的并发。
### 计时器(TimerUtil)
粗略的间隔执行计时器，单线程执行。
### 定时任务作业(SchedulerUtil)
内部使用quartz实现，支持cron表达式和时分秒配置，计时比较精确，单线程执行。
