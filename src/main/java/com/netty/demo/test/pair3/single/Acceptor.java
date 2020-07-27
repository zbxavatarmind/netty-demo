package com.netty.demo.test.pair3.single;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.netty.demo.test.pair3.NioEventHandler;

public class Acceptor extends NioEventHandler{

	public Acceptor(Selector selector, SelectionKey selectionKey) {
		super(selector, selectionKey);
	}

	@Override
	protected void handler() throws IOException {
		
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel)selectionKey.channel();
		SocketChannel socketChannel = serverSocketChannel.accept();
		
		socketChannel.configureBlocking(false);
		
		socketChannel.register(selector, SelectionKey.OP_READ);
		
		System.out.println("客户端:"+socketChannel.getRemoteAddress()+"连接成功！");
		
//		selectionKey.attach(new ReadEventHandler(selector,selectionKey));
		
	}

}
