package com.netty.demo.test.pair1;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class BioServer {
	public static void main(String[] args) {
		ServerSocket serverSocket =null;
		
		
		try {
			serverSocket=new ServerSocket(8080);
			byte[] revBuf = new byte[1024];
			
			while(true) {
				Socket socket = serverSocket.accept();
				new Thread(()->{
					InputStream in =null;
					try {
						while(true) {
							SocketAddress clientAddr = socket.getRemoteSocketAddress();
							System.out.println("handing client from:"+clientAddr);
							in= socket.getInputStream();
							int readSize = in.read(revBuf);
							if(readSize!=-1) {
								System.out.println(new String(revBuf));
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally {
						try {
							if(in!=null) {
								in.close();
							}
						} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
						}
						
					}
				}).start();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if(serverSocket!=null) {
					serverSocket.close();
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
