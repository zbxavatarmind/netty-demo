package com.netty.demo.test.pair4.submodel.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

public class SubReactor implements Runnable{

	private final Selector selector;
	
	private boolean register =false;
	
	private int num;
	
	SubReactor(Selector selector, int num) {
        this.selector = selector;
        this.num = num;
    }

	@Override
	public void run() {
		while(!Thread.interrupted()) {
			System.out.println(num+"号SubReactor等待注册中...");
			while(!Thread.interrupted()&&!register) {
				
				try {
					if(selector.select()==0) {
						continue;
					}
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = selectedKeys.iterator();
				while(iterator.hasNext()) {
					dispatch(iterator.next());
					iterator.remove();
				}
			}
		}
		
	}
	
	private void dispatch(SelectionKey key) {
		Runnable r = (Runnable)key.attachment();
		r.run();
	}
	
	void registering(boolean register) {
		this.register=register;
	}
	
	
}
