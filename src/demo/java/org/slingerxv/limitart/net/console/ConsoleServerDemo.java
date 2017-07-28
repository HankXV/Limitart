package org.slingerxv.limitart.net.console;

import java.security.NoSuchAlgorithmException;

import org.slingerxv.limitart.funcs.Proc3;

public class ConsoleServerDemo {
	public static void main(String[] args)
			throws CommandDuplicatedException, NoSuchAlgorithmException, ConsoleUserDuplicatedException {
		ConsoleUser consoleUser = new ConsoleUser();
		consoleUser.setUsername("hank");
		consoleUser.setPass("123456");
		new ConsoleServer.ConsoleServerBuilder().user(consoleUser)
				.cmd("hello", new Proc3<ConsoleUser, String, String[]>() {

					@Override
					public void run(ConsoleUser t1, String t2, String[] t3) {
						System.out.println(t2);
					}
				}).build().startServer();
	}
}
