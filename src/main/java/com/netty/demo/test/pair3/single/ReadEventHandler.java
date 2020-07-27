package com.netty.demo.test.pair3.single;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import com.netty.demo.test.pair3.NioEventHandler;

public class ReadEventHandler extends NioEventHandler{

	public ReadEventHandler(Selector selector, SelectionKey selectionKey) {
		super(selector, selectionKey);
		
	}

	@Override
	protected void handler() throws IOException {
		SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		StringBuilder sb = new StringBuilder();
		while(true) {
			buffer.clear();
			int read = socketChannel.read(buffer);
			if(read<=0) {
				break;
			}
			buffer.flip();
			sb.append(Charset.forName("UTF-8").newDecoder().decode(buffer).toString());
		}
		System.out.println("收到客户端:"+socketChannel.getRemoteAddress()+"发来的消息:"+sb.toString());
		
	}

}
