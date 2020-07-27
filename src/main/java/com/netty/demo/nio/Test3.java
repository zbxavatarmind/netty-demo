package com.netty.demo.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class Test3 {
	public static void main(String[] args) throws IOException {
		int ports[] = new int[5];
		ports[0] =5000;
		ports[1] =5001;
		ports[2] =5002;
		ports[3] =5003;
		ports[4] =5004;
		
		Selector selector = Selector.open();
		for(int i=0;i<5;i++) {
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.bind(new InetSocketAddress(ports[i]));
			
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("监听端口:"+ports[i]);
		}
		
		while(true) {
			int num = selector.select();
			System.out.println("当前就绪number:"+num);
			
			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			System.out.println("selectedKeys:"+selectedKeys);
			
			Iterator<SelectionKey> iterator = selectedKeys.iterator();
			while(iterator.hasNext()) {
				SelectionKey selectionKey = iterator.next();
				if(selectionKey.isAcceptable()) {
					ServerSocketChannel serverSocketChannel=(ServerSocketChannel)selectionKey.channel();
					SocketChannel socketChannel = serverSocketChannel.accept();
					System.out.println("获得客户端连接："+socketChannel);
					socketChannel.configureBlocking(false);
					socketChannel.register(selector, SelectionKey.OP_READ);
					
					
					iterator.remove();
				}else if(selectionKey.isReadable()) {
					SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
					StringBuilder sb = new StringBuilder();
					while(true) {
						ByteBuffer buffer = ByteBuffer.allocate(1024);
						buffer.clear();
						int readSize = socketChannel.read(buffer);
						if(readSize<=0) {
							break;
						}
						System.out.println(buffer.position()+","+buffer.limit());
						buffer.flip();
						System.out.println(buffer.position()+","+buffer.limit());
						//filp翻转之后该方式会移动limit和position，导致write方法无法正常写，写的是空值
						sb.append(Charset.forName("UTF-8").decode(buffer).toString());
						//decode/encode方式会移动limit，position，但是buffer.array方式不会
//						sb.append(new String(buffer.array()));
//						byte[] content = new byte[buffer.limit()];
						System.out.println(buffer.position()+","+buffer.limit());
//						buffer.get(content);
//						sb.append(new String(content));
//						System.out.println(buffer.position()+","+buffer.limit());
						buffer.flip();
						System.out.println(buffer.position()+","+buffer.limit());
						socketChannel.write(buffer);
					}
					
					System.out.println("收到来自于："+socketChannel+"的消息："+sb.toString());
					
					iterator.remove();
				}
			}
		}
	}
}
