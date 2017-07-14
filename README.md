# 简介(Brief Introduction)
一个旨在帮助快速搭建**Java中小型游戏服务器**的框架，通信底层采用Netty4.X，数据库采用MySql相关(框架其实并未与Mysql产生太多耦合，但建议使用Mysql)，生产消费者采用Disruptor等。**有想共同参与的或者向我吐槽的，留言**。

A framework designed to help building **Java midrange game server** quickly, the communication using Netty4.X, database using MySql (frame and Mysql actually did not have much coupling, it is recommended that you use Mysql), TaskQueue(MessageQueue) powered by Disruptor., please consider as appropriate. **Some want to participate in, or give me your advice, leave a message**.
# 环境要求(Environment)
Jdk8或以上

Jdk8 or above.
# 快速开始(Quick start)
使用服务器框架我们首先就是要关心如何快速的搭建出服务器的原始模型。我们需要构造一些必要的参数：端口和服务器名称。
Using the server framework, we first need to be concerned with how to quickly build the original model of the server. We need to construct some of the necessary parameters: port and server name.
```java

	BinaryServerConfig build = new BinaryServerConfigBuilder().port(8888).serverName("BinaryServerDemo").build();

```
然后我们需要构造一个监听器。
Then we need to construct a listener.
```java

	BinaryServerEventListener binaryServerEventListener = new BinaryServerEventListener() {
		// 当网络模块发生错误时
		@Override
		public void onExceptionCaught(Channel channel, Throwable cause) {
			cause.printStackTrace();
		}
	
		// 当一个Channel断开时
		@Override
		public void onChannelInactive(Channel channel) {
		}
	
		// 当一个Channel连接时
		@Override
		public void onChannelActive(Channel channel) {
	
		}
	
		// 当服务器完成绑定时
		@Override
		public void onServerBind(Channel channel) {
	
		}
	
		// 当链接有效时(通常在这里我们认为一个链接是有效的，因为它经过了验证)
		@Override
		public void onConnectionEffective(Channel channel) {
	
		}
	
		// 当接收到消息时
		@Override
		public void dispatchMessage(Message message) {
			message.getHandler().handle(message);
		}
	};

```
倒数第二步我们需要确定服务器可以处理哪些消息。让我们来创建第一个消息。
In the last second steps, we need to determine what messages the server can handle. Let's create the first message.
```java

	public class BinaryMessageDemo extends Message {
		public String info;
	
		// 消息编号
		@Override
		public short getMessageId() {
			return 1;
		}
	
		// 编码
		@Override
		public void encode() throws Exception {
			putString(this.info);
		}
	
		// 解码
		@Override
		public void decode() throws Exception {
			this.info = getString();
		}
	
	}

```
为上面的消息创建一个处理器，这里我们就简单的打印传输过来的内容即可。
Create a Handler for the message above, where we simply print the contents of the transmission.
```java

	public class BinaryHandlerDemo implements IHandler<BinaryMessageDemo> {
	
		@Override
		public void handle(BinaryMessageDemo msg) {
			System.out.println("server received message:" + msg.info);
		}
	
	}
	
```
构造一个消息工厂，把消息和对应的处理器注册进去。
Construct a `MessageFactory` that registers messages and corresponding Handlers.
```java

	MessageFactory factory = new MessageFactory();
	factory.registerMsg(BinaryHandlerDemo.class);
		
```
最后初始化一个服务器实例并绑定。收工！
Finally, initialize a server instance and bind it. Call it a day!
```java

	BinaryServer server = new BinaryServer(build, binaryServerEventListener, factory);
	server.bind();
		
```
下面看看客户端。因为我们现在不让客户端处理消息只发送消息，所以我们创建一个消息处理器传进消息工厂就行。现在开始构造客户端，填写好服务器地址和端口，当然还有客户端名称。你可以选择是否重连，我们这里就不展示了。
Look at the client below. Because we do not allow client processing messages only send messages, so we create a message into the message processor factory on the line. Now build the client, fill in the server address and port, and, of course, the client name. You can choose whether or not to reconnect, and we don't show it here.
```java

	BinaryClientConfig build = new BinaryClientConfigBuilder().remoteIp("127.0.0.1").remotePort(8888)
			.clientName("BinaryClientDemo").build();
				
```
接下来，在监听器里的回调函数`onConnectionEffective`里写发送消息给服务器的代码，因为一定要保证连接服务器是成功的我们才发送消息。
Next, write the code that sends the message to the server in the callback function `onConnectionEffective` in the listener, because we have to make sure the connection server is successful before we send the message.
```java

	BinaryClientEventListener binaryClientEventListener = new BinaryClientEventListener() {

			@Override
			public void onExceptionCaught(BinaryClient client, Throwable cause) {
				cause.printStackTrace();
			}

			@Override
			public void onConnectionEffective(BinaryClient client) {
				BinaryMessageDemo message = new BinaryMessageDemo();
				message.info = "Hello Limitart!";
				try {
					client.sendMessage(message, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onChannelInactive(BinaryClient client) {

			}

			@Override
			public void onChannelActive(BinaryClient client) {

			}

			@Override
			public void dispatchMessage(Message message) {
				message.getHandler().handle(message);
			}
		};
		
```
收工！
To be finished！
```java

	BinaryClient client = new BinaryClient(build, binaryClientEventListener, factory);
	client.connect();
		
```
让我们来看看效果吧,启动服务器，服务器绑定成功
Let's take a look at the effect, start the server, and make the server binding successful
```

	[INFO][BinaryServer]BinaryServerDemo nio init
	[INFO][BinaryServer]BinaryServerDemo bind at port:8888
	
```
启动客户端，客户端链接成功，并发送了消息
Start the client, the client link is successful, and the message is sent
```

	[INFO][BinaryClient]BinaryClientDemo nio init
	[INFO][BinaryClient]BinaryClientDemo start connect server：127.0.0.1:8888...
	[INFO][BinaryClient]BinaryClientDemo connect server：127.0.0.1:8888 success！

```
服务器验证链接成功并且收到了消息！是不是很棒！！！
The server verified that the link was successful and received the message! cool!!!!
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

Each message must inherit the Message class, and the message supports only the base type and the message meta type (`MessageMeta`), as well as the List or array of the previous two types. Message meta types are so-called message carrying objects, and the length of List cannot exceed the length of a short. Each message must have a unique ID to identify the length of a short. The decode and encode encoding in the message must be consistent in sequence. When a binary server is constructed, you need a message factory (`MessageFactory`) that requires the initialization of the message handler (`IHandler`) in the message factory. When a binary server receives a message, it looks for the corresponding message handler as a callback.
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
## 数据库相关(Database)
### 数据库表检查器(Database Table Checker)
数据库表检查器的作用在于当服务器检测到数据库表结构和自身的对象结构不吻合时，应当立即关闭服务器，以免更糟糕的错误发生。要使用表结构检查器，需要用到表检查注解(`@TableCheck`)放在类上，字段检查注解(`@FieldCheck`)放在字段上，最后调用数据库表检查器(`TableChecker`)来注册需要检查的类返回检查结果。在项目中应当养成写检查的良好习惯。
### 数据库日志系统(Database Logger)
数据库日志系统主要用作统计，方便后台查看营收等游戏数据。每个日志都对应了一个日志结构(继承自`AbstractLog`)，他会指定滚动时间，与数据库对应的字段类型检查用注解`@LogColumn`放在字段上，日志的名字为类名的小写加滚动后缀。滚动的类型有日表、月表、年表、不滚动4种，按需配置。日志系统每次启动前都会检查所有表结构是否正确，如果不正确在可修正的范围内给与修正，如果无法修正则抛出异常。使用`LogDBServerConfig`来构造日志系统，需要指定扫描的日志包名、线程以及数据库编码之类的配置，建议线程数量不要太多，1-3个足够。
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
