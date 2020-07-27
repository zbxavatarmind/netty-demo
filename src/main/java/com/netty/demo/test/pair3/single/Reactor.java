package com.netty.demo.test.pair3.single;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
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
		serverSocketChannel=ServerSocketChannel.open();
		serverSocketChannel.bind(new InetSocketAddress(port));
		serverSocketChannel.configureBlocking(false);
		
	}
	
	@Override
	public void run() {
		try {
			SelectionKey sk = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			while(true) {
				selector.select();
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = selectedKeys.iterator();
				while(iterator.hasNext()) {
					SelectionKey nextKey = iterator.next();
					
					dispatch(nextKey);
					iterator.remove();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private void dispatch(SelectionKey k) throws IOException{
		if(!k.isValid()) {
			return;
		}
		
		if(k.isAcceptable()) {
			Acceptor acceptor=new Acceptor(selector, k);
			acceptor.handler();
		}
		
		if(k.isReadable()) {
			ReadEventHandler readEventHandler=new ReadEventHandler(selector, k);
			readEventHandler.handler();
		}
	}
	
	public static void main(String[] args) throws IOException {
		Reactor reactor = new Reactor(8080);
		reactor.run();
	}
}
