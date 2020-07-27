package com.netty.demo.test.pair1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioServer {
	private static final int BUF_SIZE=1024;
	private static final int PORT=8080;
	private static final int TIMEOUT=3000;
	
	public static void main(String[] args) {
		selector();
	}
	
	public static void selector() {
		Selector selector =null;
		ServerSocketChannel serverSocketChannel =null;
		
		try {
			selector = Selector.open();
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
			//设置成非阻塞
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			while(true) {
				if(selector.select(TIMEOUT)==0) {
					System.out.println("==");
					continue;
				}
			Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
			while(iter.hasNext()) {
				SelectionKey key = iter.next();
				if(key.isAcceptable()) {
					handleAccept(key);
				}
				if(key.isReadable()) {
					handleRead(key);
				}
				if(key.isWritable()) {
					handleWrite(key);
				}
				if(key.isConnectable()) {
					System.out.println("isConnectable = true");
				}
				iter.remove();
			}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(selector!=null) {
					selector.close();
				}
				if(serverSocketChannel!=null) {
					serverSocketChannel.close();
				}
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void handleAccept(SelectionKey key) throws IOException {
		ServerSocketChannel channel =(ServerSocketChannel) key.channel();
		SocketChannel socketChannel = channel.accept();
		socketChannel.configureBlocking(false);
		socketChannel.register(key.selector(), SelectionKey.OP_READ,ByteBuffer.allocateDirect(BUF_SIZE));
	}
	
	public static void handleRead(SelectionKey key) throws IOException {
		SocketChannel socketChannel =(SocketChannel) key.channel();
		ByteBuffer buffer=(ByteBuffer)key.attachment();
		int readSize = socketChannel.read(buffer);
		
		
		while(readSize>0) {
			buffer.flip();
			//用于一次性从buffer中接收多个数据，避免单个字符打印，不便阅读
			byte[] content = new byte[buffer.limit()];
			while(buffer.hasRemaining()) {
				buffer.get(content);
				System.out.println(new String(content));
			}
			buffer.clear();
			readSize = socketChannel.read(buffer);
		}
		if(readSize==-1) {
			socketChannel.close();
		}
	}	
	
	public static void handleWrite(SelectionKey key) throws IOException {
		SocketChannel socketChannel=(SocketChannel)key.channel();
		ByteBuffer buffer=(ByteBuffer)key.attachment();
		buffer.flip();
		while(buffer.hasRemaining()) {
			socketChannel.write(buffer);
		}
		buffer.compact();
	}
}
