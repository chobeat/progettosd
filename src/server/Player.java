package server;

import java.net.InetAddress;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Player {
  private String name;
  public int getID() {
	return ID;
}

  
public void setID(int iD) {
	ID = iD;
}
private InetAddress address;
  private int ID;
  
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public InetAddress getAddress() {
	return address;
}
public void setAddress(InetAddress address) {
	this.address = address;
}
  
}
