package com.netty.demo.test;

public class SimpleTest {
	public static String[] aa= new String[10];
	public static void testMethod() {
		
		for(int i=0;i<10;i++) {
			boolean success =false;
			try {
				
				if(i==5) {
					int a=1/0;
				}
				aa[i] = "第"+i+"个字符串";
				success =true;
			}catch(Exception e) {
				  throw new IllegalStateException("failed to create a child event loop", e);
			}finally {
				System.out.println("finally执行了");
				if(!success) {
					for(int j=0;j<i;j++) {
						aa[j]=null;
					}
				}
			}
		}
	
	}
	
	public static void main(String[] args) {
		try {
			testMethod();
		} catch (Exception e) {
			
		}
		System.out.println("***********************");
		for(int i=0;i<10;i++) {
			
			System.out.println(aa[i]);
		}
	}
}
