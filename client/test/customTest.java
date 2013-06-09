package test;

import java.io.ByteArrayInputStream;
import com.sun.jersey.api.client.ClientHandlerException;

import client.Main;

public class customTest extends Thread {
	String input;

	public customTest(String input) {
		this.input = input;

	}

	@Override
	public void run() {

		try {
			Main m1 = new Main(new ByteArrayInputStream(input.getBytes()));
			try {
				m1.runClient();
			} catch (ClientHandlerException e) {
				System.out.println("Accendi il server...");
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
	}
}