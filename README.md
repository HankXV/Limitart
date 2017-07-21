[![Codacy Badge](https://api.codacy.com/project/badge/Grade/98c8a0ea3b43404aa8a820078e2c6e12)](https://www.codacy.com/app/104381832/Limitart?utm_source=github.com&utm_medium=referral&utm_content=HankXV/Limitart&utm_campaign=badger)
[![Build Status](https://travis-ci.org/HankXV/Limitart.svg?branch=master)](https://travis-ci.org/HankXV/Limitart)
# Brief Introduction
This is a framework that designed to help you build a **Java Game Server** quickly.
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
