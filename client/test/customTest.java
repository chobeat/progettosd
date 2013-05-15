package test;
import java.io.ByteArrayInputStream;

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

			m1.runClient();
			System.out.println("out");
		} catch (Exception e) {
			System.out.println("excpt");
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}
}