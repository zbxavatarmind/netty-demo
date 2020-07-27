package com.netty.demo.test.pair3;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public abstract class NioEventHandler implements Runnable{

	protected final Selector selector;
	protected final SelectionKey selectionKey;
	
	public NioEventHandler(Selector selector,SelectionKey selectionKey) {
		this.selector=selector;
		this.selectionKey=selectionKey;
	}
	
	protected abstract void handler()throws IOException;
	
	@Override
	public void run() {
		try {
			handler();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
