package com.netty.demo.test.pair4.submodel.client;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;


public class Connector implements Runnable{

	private final Selector selector;
	
	private final SocketChannel socketChannel;
	
	Connector(Selector selector,SocketChannel socketChannel){
		this.selector=selector;
		this.socketChannel=socketChannel;
	}
	@Override
	public void run() {
		try {
			if(socketChannel.finishConnect()) {
				System.out.println("连接:"+socketChannel.getRemoteAddress()+"已经建立！");
				new Handler(selector,socketChannel);
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}

}
