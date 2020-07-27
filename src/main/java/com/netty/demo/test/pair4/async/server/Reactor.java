package com.netty.demo.test.pair4.async.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Reactor implements Runnable{

	private final Selector selector;
	
	private final ServerSocketChannel serverSocketChannel;
	
	public Reactor(int port) throws IOException {
		selector = Selector.open();
		System.out.println("服务端selector:"+selector);
		serverSocketChannel=ServerSocketChannel.open();
		serverSocketChannel.bind(new InetSocketAddress(port));
		serverSocketChannel.configureBlocking(false);
		//Reactor是入口，最初给一个channel注册上去的时间都只能是accept
		SelectionKey sk = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		System.out.println("服务端Sk:"+sk);
		sk.attach(new Acceptor(selector,serverSocketChannel));
	}
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) {
				selector.select();
				System.out.println("select进来了*******************");
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = selectedKeys.iterator();
				while(iterator.hasNext()) {
					
					dispatch(iterator.next());
					iterator.remove();
					
				}
				
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	
	void dispatch(SelectionKey sk) {
		Runnable r= (Runnable)sk.attachment();
		r.run();
	}
	
	public static void main(String[] args) throws IOException {
		new Reactor(8080).run();
	}

}
