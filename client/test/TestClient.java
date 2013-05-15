package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.bind.JAXBException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;

import client.Main;
import client.ToyClient;

public class TestClient {

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

	public String generateRandomPlayer() {
		return "" + UUID.randomUUID().toString() + "\n" + ++port + "\n";

	}

	int port = 10000;

	@Test
	public void test() {
		
	/*	  String clients[]={ generateRandomPlayer()+"0\n partita\n",
		  generateRandomPlayer()+"1\n", generateRandomPlayer()+"1\ntestquit",
		  generateRandomPlayer()+"1\n",
		 
		  }; for(String i:clients){ new customTest(i).start(); }
	*/
		
		new customTest("anac\n0\naaaa\n").start();
	}

}
