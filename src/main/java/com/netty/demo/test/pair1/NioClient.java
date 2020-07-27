package com.netty.demo.test.pair1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

public class NioClient {
	public static void main(String[] args) {
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		SocketChannel socketChannel =null;
		
		try {
			socketChannel = SocketChannel.open();
			//channel设置成非阻塞
			socketChannel.configureBlocking(false);
			socketChannel.connect(new InetSocketAddress(8080));
			if(socketChannel.finishConnect()) {
				int i=0;
				while(true) {
					TimeUnit.SECONDS.sleep(1);
					String info ="I'm the "+i+++"-th infomation from client";
					buffer.clear();
					buffer.put(info.getBytes());
					buffer.flip();
					while(buffer.hasRemaining()) {
						System.out.println(buffer);
						socketChannel.write(buffer);
					}
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(socketChannel!=null) {
				try {
					socketChannel.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
	}
}
