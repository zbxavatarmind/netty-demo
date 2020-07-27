package com.netty.demo.test.pair4.single.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Acceptor implements Runnable{

	private final Selector selector;
	
	private final ServerSocketChannel serverSocketChannel;
	
	public Acceptor(Selector selector,ServerSocketChannel serverSocketChannel) {
		this.selector=selector;
		this.serverSocketChannel = serverSocketChannel;
		
	}
	@Override
	public void run() {
		
		try {
			SocketChannel socketChannel = serverSocketChannel.accept();
			if(socketChannel!=null) {
				System.out.println("客户端:"+socketChannel.getRemoteAddress()+"连接成功！");
				new Handler(selector,socketChannel);
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}

}
