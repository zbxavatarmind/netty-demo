package com.netty.demo.test.pair4.submodel.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncHandler implements Runnable{

	private final SelectionKey selectionKey;
	
	private final SocketChannel socketChannel;
	
	private ByteBuffer readBuffer = ByteBuffer.allocate(1024);
	
	private ByteBuffer sendBuffer = ByteBuffer.allocate(2048);
	
	private final static int READ = 0;
	
	private final static int SEND = 1;
	
	private final static int PROCESSING =2;//新增状态，处理中
	
	private  int status = READ;
	
	//开启线程数为5的异步处理线程池
	private static final ExecutorService workers = Executors.newFixedThreadPool(5);
	
	private int num;//从反应堆序号
	
	public AsyncHandler(Selector selector,SocketChannel socketChannel,int num)throws IOException {
		this.num=num;
		socketChannel.configureBlocking(false);
		this.socketChannel = socketChannel;
		selectionKey=this.socketChannel.register(selector, 0);
		selectionKey.attach(this);
		selectionKey.interestOps(SelectionKey.OP_READ);
//		selector.wakeup();
	}
	
	
	@Override
	public void run() {
		try {
			switch(status) {
				case READ:
					read();
					break;
				case SEND:
					send();
					break;
				default:
			}
		} catch (IOException e) {
			System.err.println("read或send时发生异常！异常信息：" + e.getMessage());
            selectionKey.cancel();
            try {
                socketChannel.close();
            } catch (IOException e2) {
                System.err.println("关闭通道时发生异常！异常信息：" + e2.getMessage());
                e2.printStackTrace();
            }
		}
		
		
	}
	
	
	private void read() throws IOException {
		if(selectionKey.isValid()) {
			
			readBuffer.clear();
			int readSize = socketChannel.read(readBuffer);
			if(readSize>0) {
				status = PROCESSING;
				workers.execute(() -> {
					try {
						readWorker();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				selectionKey.interestOps(SelectionKey.OP_WRITE);
			}
			
			else {
				selectionKey.cancel();
				socketChannel.close();
				System.out.println("read连接通道关闭");
			}
		}
	}
	

	private void send() throws IOException {
		
		if(selectionKey.isValid()) {
			sendBuffer.clear();
			sendBuffer.put(String.format("%d号SubReactor触发：我收到来自%s的信息辣：%s,  200ok;",num,
                    socketChannel.getRemoteAddress(),
                    new String(readBuffer.array())).getBytes());
			sendBuffer.flip();
			int writeSize = socketChannel.write(sendBuffer);
			if(writeSize>0) {
				status = PROCESSING;
				workers.execute(() -> {
					try {
						sendWorker();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				selectionKey.interestOps(SelectionKey.OP_READ);
			}else {
				selectionKey.cancel();
				socketChannel.close();
				System.out.println("write连接通道关闭");
			}
		}
	}
	
	
	private void readWorker() throws IOException {
		try {
			Thread.sleep(5000L);
		} catch (InterruptedException e) {
			
		}
		System.out.println(String.format("收到来自 %s 的消息: %s",
                socketChannel.getRemoteAddress(),
                new String(readBuffer.array())));
		status = SEND;
		/**
		 * 写事件是由代码触发的，和读事件不同，读事件需要输入内容才会触发，所以在执行下述代码之后，如果写完不调整interestOps
		 * 就会无限触发写事件
		 */
//		selectionKey.interestOps(SelectionKey.OP_WRITE);
//		this.selectionKey.selector().wakeup();
	}
	
	private void sendWorker()throws IOException{
//		try {
//			Thread.sleep(2000L);
//		} catch (InterruptedException e) {
//			
//		}
		
		status =READ;
//		selectionKey.interestOps(SelectionKey.OP_READ);
//		this.selectionKey.selector().wakeup();
	}

}
