package com.netty.demo.test;

import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
//no.1
public class PromiseTest {
	//no.2
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		EventExecutor executor = new DefaultEventExecutor();
		//no.3
		Promise promise = new DefaultPromise(executor);
		
		promise.addListener(new GenericFutureListener<Future<String>>() {
//no.4
			@Override
			public void operationComplete(Future<String> future) throws Exception {
				if(future.isSuccess()) {
					System.out.println("任务结束，结果："+future.get());
				}else {
					System.out.println("任务失败，异常："+future.cause());
				}
				
			}
			
		}).addListener(new GenericFutureListener<Future<String>>() {

			@Override
			public void operationComplete(Future<String> future) throws Exception {
				System.out.println("任务结束，balabala...");
				
			}
		});
		
		executor.submit(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				//设置成功
//				promise.setSuccess("章倍祥测试成功！！！！");
				//设置失败
				promise.setFailure(new RuntimeException());
			}
		});
		//no.6
		try {
//			promise.sync();
			Promise await = promise.await();
//			System.out.println(await.get());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
