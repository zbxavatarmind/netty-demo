package com.netty.demo.test.pair4.async.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOClient implements Runnable{
	
	private Selector selector;
	
	private SocketChannel socketChannel;
	
	NIOClient(String ip,int port){
		try {
			selector = Selector.open();
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
			socketChannel.connect(new InetSocketAddress(ip,port));
			System.out.println("客户端selector:"+selector);
			SelectionKey sk = socketChannel.register(selector, SelectionKey.OP_CONNECT);
			System.out.println("客户端Sk:"+sk);
			sk.attach(new Connector(selector, socketChannel));
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) {
				selector.select();
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = selectedKeys.iterator();
				while(iterator.hasNext()) {
					dispatch(iterator.next());
					iterator.remove();
				}
//				selectedKeys.clear();
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	
	void dispatch(SelectionKey sk) {
		Runnable r = (Runnable)sk.attachment();
		r.run();
	}
	
	
	public static void main(String[] args) {
		new Thread(new NIOClient("localhost", 8080)).start();
		new Thread(new NIOClient("localhost", 8080)).start();
	}
	
}
