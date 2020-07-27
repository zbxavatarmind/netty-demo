package com.netty.demo.test.pair4.submodel.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Acceptor implements Runnable{

//	private final Selector selector;
	
	private final ServerSocketChannel serverSocketChannel;
	
	private final int coreNum =Runtime.getRuntime().availableProcessors();//获取cpu核心数
	
	private final Selector[] selectors = new Selector[coreNum];//创建selector给subReactor使用，个数为cpu核数，这一点和netty的EventLoopGroup的workerGroup建立时的定义很像
	
	private int next = 0; //轮询使用subReactor的下标索引
	
	private final SubReactor[] reactors = new SubReactor[coreNum];
	
	/**
	 * 这里可以联想到netty的线程模型，SubReactor[]数组即对应着EventLoopGroup，其中一个SubReactor代表一个eventLoop,一个eventLoop会绑定一个唯一的线程thread和选择器selector，
	 * 该线程可以处理多个连接channel以实现多路复用
	 */
	
	private Thread[] threads = new Thread[coreNum];//对应每个subReactor的处理线程
	
	
	
	
	public Acceptor(ServerSocketChannel serverSocketChannel) throws IOException {
		this.serverSocketChannel = serverSocketChannel;
		for(int i=0;i<coreNum;i++) {
			selectors[i]=Selector.open();
			reactors[i]=new SubReactor(selectors[i], i);
			threads[i]=new Thread(reactors[i]);
			threads[i].start();
		}
		
	}
	@Override
	public void run() {
		
		try {
			SocketChannel socketChannel = serverSocketChannel.accept();
			if(socketChannel!=null) {
				System.out.println("客户端:"+socketChannel.getRemoteAddress()+"连接成功！");
				socketChannel.configureBlocking(false);
				//注意，一个selector在select的时候是无法注册新的事件的，因此在这边要先暂停一下select方法触发的程序段
				reactors[next].registering(true);
				selectors[next].wakeup();
				SelectionKey sk = socketChannel.register(selectors[next], SelectionKey.OP_READ);
				selectors[next].wakeup();
				reactors[next].registering(false);
				sk.attach(new AsyncHandler(selectors[next], socketChannel,next));
				
				if(++next==selectors.length) {
					//判断越界，重新分配
					next=0;
				}
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}

}
