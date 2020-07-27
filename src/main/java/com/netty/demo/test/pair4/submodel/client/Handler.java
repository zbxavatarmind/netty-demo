package com.netty.demo.test.pair4.submodel.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

public class Handler implements Runnable{

	private final SelectionKey selectionKey;
	
	private final SocketChannel socketChannel;
	
	private ByteBuffer readBuffer = ByteBuffer.allocate(2048);
	
	private ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
	
	private final static int READ=0;
	private final static int SEND=1;
	
	private int status = SEND;
	
	private AtomicInteger counter = new AtomicInteger();
	
	Handler(Selector selector,SocketChannel socketChannel)throws IOException{
		socketChannel.configureBlocking(false);
		this.socketChannel=socketChannel;
		selectionKey = this.socketChannel.register(selector, 0);
		System.out.println("客户端具体handler sk:"+selectionKey);
		selectionKey.attach(this);
		selectionKey.interestOps(SelectionKey.OP_WRITE);
//		selector.wakeup();
	}
	
	
	@Override
	public void run() {
		try {
			switch(status) {
				case SEND:
					send();
					break;
				case READ:
					read();
					break;
				default:
			 }
		} catch (IOException e) {
			 System.err.println("send或read时发生异常！异常信息：" + e.getMessage());
	            selectionKey.cancel();
	            try {
	                socketChannel.close();
	            } catch (IOException e2) {
	                System.err.println("关闭通道时发生异常！异常信息：" + e2.getMessage());
	                e2.printStackTrace();
	            }
		}
		
	}

	
	void send()throws IOException{
		if(selectionKey.isValid()) {
			sendBuffer.clear();
			int count = counter.incrementAndGet();
			if(count<=10) {
				byte[] bytes = String.format("客户端发送的第%s条消息", count).getBytes();
				sendBuffer.put(bytes);
				sendBuffer.flip();
				int writeSize = socketChannel.write(sendBuffer);
				if(writeSize>0) {
					status = READ;
					selectionKey.interestOps(SelectionKey.OP_READ);
				}else {
					selectionKey.cancel();
					socketChannel.close();
				}
				
				
			}
		}
	}
	
	void read()throws IOException{
		if(selectionKey.isValid()) {
			readBuffer.clear();
			int readSize = socketChannel.read(readBuffer);
			System.out.println(String.format("收到来自服务端的消息: %s", new String(readBuffer.array())));
			
			if(readSize>0) {
				status = SEND;
				selectionKey.interestOps(SelectionKey.OP_WRITE);
			}else {
				selectionKey.cancel();
				socketChannel.close();
			}
			
		}
	}
}
