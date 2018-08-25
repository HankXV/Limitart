[![Maven](https://img.shields.io/badge/maven-v3.0--alpha-green.svg)](https://mvnrepository.com/artifact/org.slingerxv/limitart)
[![Apache](https://img.shields.io/badge/license-Apache%202-green.svg)](http://www.apache.org/licenses/LICENSE-2.0)
![JDK](https://img.shields.io/badge/jdk-1.8-green.svg)
#### 轻量级快速开发轻量级服务器 Limitart
![Image text](./limitart-logo-128.png)

Limitart是以Netty为基础开发的一套可快速实现轻量级服务器的框架(包括游戏服务器,中间件等)，依赖非常少的第三方库、代码量少、上手容易，让你可以非常快速的开发出服务器原型。
#### 3.X快速开始
1.涉及到的类

    BinaryServer:服务器主体
    BinaryMeta:二进制消息元，主要封装了序列化操作
    BinaryMessage:二进制消息，是网络传输的对象，可以传输基本类型和BinaryMeta以及他们的列表
    BinaryHandler:消息处理方法，负责把指定的消息路由到指定的方法
    BinaryManager:消息处理类注解，负责管理一个模块的所有处理方法，即一组BinaryHandler
    BinaryRequestParam:所有消息处理方法必须声明的参数
    BinaryMessageFactory:消息工厂，负责管理所有服务器需要处理的消息

2.创建一个消息
```java
    public class BinaryMessageDemo extends BinaryMessage {
    	public String content = "hello limitart!";

    	@Override
    	public short messageID() {
    		return BinaryMessages.createID(0X00, 0X01);
    	}

    }
````
3.为这个消息创建处理器
```java
    @BinaryManager
    public class BinaryManagerDemo {
    	@BinaryHandler(BinaryMessageDemo.class)
    	public void doMessageDemo(BinaryRequestParam param) {
    		BinaryMessageDemo msg = param.msg();
    		System.out.println(msg.content);
    	}
    }
```
4.让消息工厂实例化注册消息处理器
```java
    BinaryMessageFactory factory = BinaryMessageFactory.createEmpty().registerManager(BinaryManagerDemo.class);
    // 注意：这里可以调用BinaryMessageFactory.create("[包名]","[自定义实例]")的接口来配合脚本加载器(ScriptLoader)来初始化
```
5.配置服务器实体
```java
    new BinaryServer.BinaryServerBuilder()
    				.factory(factory)
    				.build()
    				.startServer();
```
6.开启客户端连接并发送消息
```java
    new BinaryClient.BinaryClientBuilder().remoteAddress(new AddressPair("127.0.0.1", 8888))
            .factory(BinaryMessageFactory.createEmpty()).onConnected((BinaryClient cl, Boolean state) -> {
        if (state) {
            try {
                cl.sendMessage(new BinaryMessageDemo());
            } catch (Exception e) {
            }
        }
    }).build().connect();
```
7.服务器日志+结果

    [main] INFO org.slingerxv.limitart.net.binary.BinaryMessageFactory - register msg org.slingerxv.limitart.net.BinaryMessageDemo at org.slingerxv.limitart.net.BinaryManagerDemo
    [main] INFO org.slingerxv.limitart.net.AbstractNettyServer - Limitart-Binary-Server nio init
    [nioEventLoopGroup-2-1] INFO org.slingerxv.limitart.net.AbstractNettyServer - Limitart-Binary-Server bind at port:8888
    [nioEventLoopGroup-3-1] INFO org.slingerxv.limitart.net.AbstractNettyServer - /127.0.0.1:54062 connected！
    hello limitart!
#### 消息编码

	消息长度(short,包含消息体长度+2)+消息ID(short)+消息体
#### 模块介绍
    base 基础包	
    collections 主要是一些游戏中常用的数据结构
    fsm 有限状态机
    game 游戏相关逻辑整理
    logging 日志通用接口
    net 网络通信
    reflectasm modified from https://github.com/EsotericSoftware/reflectasm
    script 脚本热更新
    singleton 轻量单例依赖注入
    concurrent 并发相关
    util 常用的工具包
    
#### 2.X快速开始
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
				.factory(factory).build()
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

	server received message:Hello Limitart!

#### 更新日志
##### v3.0-alpha
	1.对于3.0，主要的改动是网络模块开发由常规的接口、虚拟类的方式更改为注解的方式，隐藏更多细节。
	2.在之后的改进中，方向也会变为面向接口开发和事件选择性处理等
	3.脚本热加载去掉Groovy，采用原生实现
	4.动态编译类型脚本增加自动热加载机制
	5.增加一些常用集合(临时实现)
	6.增加二进制消息xml协议文件定义示例(binary_msg_proto.xml)和解析(BinaryMessages.readBinaryMessageProtoFile)
	7.增加一些游戏中常用的数学算法
##### v2.1.0-release
	1.脚本热加载去掉Groovy，采用原生实现
	2.动态编译类型脚本增加自动热加载机制
	3.增加一些常用集合(临时实现)
	4.增加一些游戏服务器常用的数学算法
##### v2.0.1-release
	1.修复热加载jar包不能替换旧jar包的问题
	2.热加载的脚本，如果父类是class，那么不能引用父类protected的字段，作用域由于加载器不同而无法引用，建议使用public方法获取或者把父类字段定义为public
	3.状态机计时从State移动到状态机本身
##### v2.0-release
	1.增加敏感词过滤(BadWordUtil)
	2.语言本地化(I18NStrings)
	3.自定义编码部分增加了protobuf的压缩算法
	4.ConsoleServer改名为TelnetServer
	5.增加ProtoBuf服务器支持
##### v2.0-alpha
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