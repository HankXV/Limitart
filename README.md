[![Build Status](https://travis-ci.org/HankXV/Limitart.svg?branch=master)](https://travis-ci.org/HankXV/Limitart)
# Brief Introduction
This is a framework that designed to help to build a **Java Midrange Game Server** quickly. Its communicating interface based on Netty4.X and the TaskQueue(MessageQueue) based on Disruptor. **If one have some advice or want be one of us, please leave any messages to us, and we are always welcome**
## Environment
Jdk8 or above.
# Quick Start
First, we neet to determine which messages the server can handle. Let's create a message.
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
Create a Handler for the message. Here, we just simply print the contents of transmission.
```java

	public class BinaryHandlerDemo implements IHandler<BinaryMessageDemo> {
	
		@Override
		public void handle(BinaryMessageDemo msg) {
			System.out.println("server received message:" + msg.info);
		}
	
	}
	
```
Construct a `MessageFactory` that registers message's handler.
```java

	MessageFactory factory = new MessageFactory().registerMsg(BinaryHandlerDemo.class);
		
```
Finally, initialize a server instance and bind it. 
```java

			new BinaryServer.BinaryServerBuilder()
				// port
				.addressPair(new AddressPair(8888))
				// register factory
				.factory(messageFactory).build();
				.startServer();
		
```
Look at the client below. Because we do not allow client processing messages only send messages, so we create a message into the message processor factory on the line. Now build the client, fill in the server address and port, and, of course, the client name. You can choose whether or not to reconnect, and we don't show it here.Write the code that sends the message to the server in the listener `onConnectionEffective`.
```java

			new BinaryClient.BinaryClientBuilder()
				.remoteAddress(new AddressPair("127.0.0.1", 8888))
				.factory(new MessageFactory())
				.onConnectionEffective(c -> {
					BinaryMessageDemo message = new BinaryMessageDemo();
					message.info = "Hello Limitart!";
					c.sendMessage(message, null);
				}).build()
				.connect();
				
```
finished！The client link is successful, and the message is sent.The server verified that the link was successful and received the message! cool!!!!
```

	server received message:Hello Limitart!

```
