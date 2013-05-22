package distributed;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

import javax.xml.bind.JAXBException;

import communication.JoinRingMessage;
import communication.JoinRingReplyMessage;
import communication.SetMeNextMessage;
import communication.TokenMessage;

public class TokenManager {
	
	public Peer prev;
	public Peer next;
	public PeerManager pm;
	public TokenManager(PeerManager p){pm=p;}
	
	public void onTokenReceived(TokenMessage t) throws IOException{
		System.out.println("Sono "+pm.main.me+ " e ricevo token con counter "+t.counter);
try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		TokenMessage newToken=new TokenMessage();
		newToken.counter=t.counter+1;
		try {
			pm.send(newToken, next.player.getPort());
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void onJoinRingMessageReceived(JoinRingMessage jrm){
	
		Peer p=pm.joinPeerList(jrm.sender);
		
		JoinRingReplyMessage reply=new JoinRingReplyMessage();
		reply.sender=pm.main.me;
		reply.newPrev=prev.player;
		try {
			pm.send(reply, jrm.sender.getPort());
		} catch (IOException | JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		prev=p;
	}
	
	public void joinRing() throws IOException, JAXBException{
		//i'm alone
		if(pm.connectionList.size()<2){
			next=prev=pm.connectionList.get(pm.main.me.getPort());
			TokenMessage newToken=new TokenMessage();
			newToken.counter=0;
			newToken.iddo=new Random().nextInt()%100;
			pm.send(newToken, pm.main.me.getPort());
		}
		else
		{
			Peer target=(Peer) pm.connectionList.values().toArray()[0];
			if(target.player.getPort()==pm.main.me.getPort())
				target=(Peer) pm.connectionList.values().toArray()[1];
			JoinRingMessage jrm= new JoinRingMessage();
			jrm.sender=pm.main.me;
			pm.send(jrm, target.player.getPort());
			next=target;
		}
		
	}
	
	public void onJoinRingReplyMessageReceived(JoinRingReplyMessage joinRingReplyMessage) {
		prev=pm.connectionList.get(joinRingReplyMessage.newPrev.getPort());
		SetMeNextMessage smn=new SetMeNextMessage();
		smn.sender=pm.main.me;
		try {
			pm.send(smn,prev.player.getPort());
		} catch (IOException | JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void onSetMeNextMessageReceived(SetMeNextMessage smn){
		next=pm.joinPeerList(smn.sender);
		System.out.println("SetMeNextReceived next="+next.player+". Prev="+ prev.player);
	}
}
