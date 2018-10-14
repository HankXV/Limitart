[![Maven](https://img.shields.io/badge/maven-v3.0--alpha-green.svg)](https://mvnrepository.com/artifact/org.slingerxv/limitart)
[![Apache](https://img.shields.io/badge/license-Apache%202-green.svg)](http://www.apache.org/licenses/LICENSE-2.0)
![JDK](https://img.shields.io/badge/jdk-1.8-green.svg)

![Image text](./limitart-logo-128.png)

Limitart是以Netty为基础开发的一套可快速实现轻量级服务器的框架(包括游戏服务器,中间件等)，依赖非常少的第三方库、代码量少、上手容易，让你可以非常快速的开发出服务器原型。
#### 3.X快速开始
1.二进制通信快速开始

 ```java
        //创建一个消息
        public class BinaryMessageDemo extends BinaryMessage {
            public String content = "hello limitart!";
    
            @Override
            public Short id() {
                return BinaryMessages.createID(0X00, 0X01);
            }
    
        }
````
为这个消息创建处理器
```java
    @MapperClass
    public class BinaryManagerDemo {
    	@Mapper(BinaryMessageDemo.class)
    	public void doMessageDemo(BinaryRequestParam param) {
    		BinaryMessageDemo msg = param.msg();
    		System.out.println(msg.content);
    	}
    }
```

```java
    //让消息工厂实例化注册消息处理器 注意：这里可以调用Router.create("[包名]","[自定义实例]")的接口来配合脚本加载器(ScriptLoader)或单例注入(Singletons)来初始化
    Router router = Router.empty().registerMapperClass(BinaryManagerDemo.class);
```
配置服务器实体
```java
    BinaryEndPoint.builder(true)
    				.router(router)
    				.build()
    				.start(AddressPair.withPort(8888));
```
开启客户端连接并发送消息
```java
    BinaryEndPoint.builder(false)
           .router(Router.empty()).onConnected((s, state) -> {
        if (state) {
            try {
                s.writeNow(new BinaryMessageDemo());
            } catch (Exception e) {
            }
        }
     }).build().start(AddressPair.withIP("127.0.0.1", 8888));
```
服务器日志+结果

    [main] INFO BinaryMessageFactory - register msg BinaryMessageDemo at BinaryManagerDemo
    [main] INFO AbstractNettyServer - Limitart-Binary-Server nio init
    [nioEventLoopGroup-2-1] INFO AbstractNettyServer - Limitart-Binary-Server bind at port:8888
    [nioEventLoopGroup-3-1] INFO AbstractNettyServer - /127.0.0.1:54062 connected！
    hello limitart!
消息编码

	消息长度(short,包含消息体长度+2)+消息ID(short)+消息体
	
2.Google Protobuf通信快速开始

创建跟1无异，只是入口变为ProtobufEndPoint
 
3.简单HTTP通信开始
```java
        HTTPEndPoint.builder().onMessageIn((s, i) -> {
            if (i.getUrl().equals("/limitart")) {
                return "hello limitart!".getBytes(StandardCharsets.UTF_8);
            }
            return null;
        }).build().start(AddressPair.withPort(8080));
```
通过浏览器访问 http://127.0.0.1:8080/limitart

得到结果 hello limitart!
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