package common;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import javax.xml.bind.annotation.XmlRootElement;

import m.Server;


@XmlRootElement
public class Player {
  private String name;
  private String addr;
  private int port;
  private int id;
  
public int getId() {
	return id;
}

public void setId(int id) {
	this.id = id;
}

public Player(){
};  
  
public Player(String name,String addr, int port){
	this.name=name;
	this.addr=addr;
	this.port=port;
}
  
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getAddr() {
	return addr;
}
public void setAddr(String addr) {
	this.addr = addr;
}
public int getPort() {
	return port;
	
}
public void setPort(int port) {
	this.port = port;
}

@Override 
public String toString(){
	return "Player-"+this.id+" "+this.name+" - "+this.addr+":"+this.port;
	
}

}
