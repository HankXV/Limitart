package org.slingerxv.limitart.net.console;

import java.security.NoSuchAlgorithmException;

import org.slingerxv.limitart.funcs.Proc3;
import org.slingerxv.limitart.net.telnet.CommandDuplicatedException;
import org.slingerxv.limitart.net.telnet.TelnetServer;
import org.slingerxv.limitart.net.telnet.TelnetUser;
import org.slingerxv.limitart.net.telnet.TelnetUserDuplicatedException;

public class ConsoleServerDemo {
	public static void main(String[] args)
			throws CommandDuplicatedException, NoSuchAlgorithmException, TelnetUserDuplicatedException {
		TelnetUser consoleUser = new TelnetUser();
		consoleUser.setUsername("hank");
		consoleUser.setPass("123456");
		new TelnetServer.ConsoleServerBuilder().user(consoleUser)
				.cmd("hello", new Proc3<TelnetUser, String, String[]>() {

					@Override
					public void run(TelnetUser t1, String t2, String[] t3) {
						System.out.println(t2);
					}
				}).build().startServer();
	}
}
