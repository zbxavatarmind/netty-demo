package com.netty.demo.test.pair2;

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

public class SingleThreadServer {
	public static void main(String[] args) throws Exception{
		Selector selector = initServer();
	    while (selector.select() > 0) {
	        Set<SelectionKey> set = selector.selectedKeys();
	        System.out.println("并发量:"+set.size());
	        Iterator<SelectionKey> iterator = set.iterator();
	        while (iterator.hasNext()) {
	            SelectionKey selectionKey = iterator.next();
	            try {
	                if (selectionKey.isAcceptable()) {
	                    ServerSocketChannel serverSocketChannel = 
	                        (ServerSocketChannel) selectionKey.channel();
	                    SocketChannel channel = serverSocketChannel.accept();
	                    System.out.println("建立链接：" + channel.getRemoteAddress());
	                    channel.configureBlocking(false);
	                    ByteBuffer buffer = ByteBuffer.allocate(1024);
	                    channel.register(selector, SelectionKey.OP_READ, buffer);
	                } else if (selectionKey.isReadable()) {
	                    SocketChannel channel = (SocketChannel) selectionKey.channel();
	                    ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
	                    buffer.clear();
	                    StringBuilder sb = new StringBuilder();
	                    int read = 0;
	                    while ((read = channel.read(buffer)) > 0) {
	                        buffer.flip();
	                        sb.append(Charset.forName("UTF-8").
	                                  newDecoder().decode(buffer));
	                        buffer.clear();
	                    }
	                    System.out.printf("收到 %s 发来的：%s\n", 
	                                      channel.getRemoteAddress(), sb);
	                    buffer.clear();
	                    // 模拟server端处理耗时
//	                    Thread.sleep((int) (Math.random() * 1000));
	                    
	                    buffer.put(("收到，你发来的是：" + sb + "\r\n").getBytes("utf-8"));
	                    buffer.flip();
	                    channel.write(buffer);
	                    System.out.printf("回复 %s 内容是： %s\n", 
	                                      channel.getRemoteAddress(), sb);
	                    channel.register(selector, SelectionKey.OP_READ, buffer.clear());
	                }
	            } catch (Exception e) {
	                selectionKey.cancel();
	                selectionKey.channel().close();
	                System.err.println(e.getMessage());
	            }
	            iterator.remove();
	        }
	    }
	}
	
	private static Selector initServer() throws IOException {
	    ServerSocketChannel serverChannel = ServerSocketChannel.open();
	    serverChannel.configureBlocking(false);
	    serverChannel.bind(new InetSocketAddress(9999));
	    Selector selector = Selector.open();
	    serverChannel.register(selector, SelectionKey.OP_ACCEPT);
	    System.out.println("server 启动");
	    return selector;
	}
}
