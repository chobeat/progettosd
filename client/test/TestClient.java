package test;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;

import client.Main;
import client.ToyClient;

public class TestClient {
	
	  @Rule
	  public TextFromStandardInputStream systemInMock = TextFromStandardInputStream.emptyStandardInputStream();
	
	@Test
	public void test()  {
	
		Main m1=new Main();
		Main m2=new Main();
		Main m3=new Main();
		
	
	
		try {
			systemInMock.provideText("anacleto\n3333\n0\nsss\n");
			
			m1.runClient();
			systemInMock.provideText("abafillilo\n3456\n1\n");
			m2.runClient();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	 
	}

}
