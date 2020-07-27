package com.netty.demo.test.pair2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SingleThreadClient {
	public static void main(String[] args) {
		for (int i = 0; i < 20; i++) {
	        new Thread(new Runnable() {
	            @Override
	            public void run() {
	                try {
	                    clientHandler();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }).start();
	    }
	    return;
	}
	
	private static void clientHandler() throws IOException {
	    long start = System.currentTimeMillis();
	    Socket socket = new Socket();
	    socket.setSoTimeout(100000);
	    socket.connect(new InetSocketAddress(9999));
	    OutputStream outputStream = socket.getOutputStream();
	    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
	    bw.write("你好，我是client " + socket.getLocalSocketAddress() + "\r\n");
	    bw.flush();
	    
	    InputStream inputStream = socket.getInputStream();
	    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
	    System.out.printf("接到服务端响应：%s，处理了%d\r\n", br.readLine(), (System.currentTimeMillis() - start));
	    br.close();
	    inputStream.close();
	    bw.close();
	    outputStream.close();
	}
}
