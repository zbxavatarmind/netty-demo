package com.netty.demo.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ByteBufferTest {
	public static void main(String[] args) throws Exception{
//		ByteBuffer buffer = ByteBuffer.allocate(64);
//		buffer.putChar('好');
//		buffer.putInt(123);
//		buffer.putFloat(123.4565f);
		
		
//		FileInputStream inputStream = new FileInputStream("aaa.txt");
//		FileChannel channel = inputStream.getChannel();
//		ByteBuffer buffer = ByteBuffer.allocate(512);
//		channel.read(buffer);
//		System.out.println("read完后,position:"+buffer.position()+",limit:"+buffer.limit());
//		buffer.flip();
//		System.out.println("read完flip后,position:"+buffer.position()+",limit:"+buffer.limit());
//		while(buffer.hasRemaining()) {
//			System.out.println("每次read:"+buffer.position()+",limit:"+buffer.limit());
//			System.out.println("Character:"+(char)buffer.get());
//		}
//		inputStream.close();
//		channel.close();
		
//		FileOutputStream outputStream = new FileOutputStream("bbb.txt");
//		FileChannel channel = outputStream.getChannel();
//		
//		ByteBuffer buffer = ByteBuffer.allocate(512);
//		
//		byte[] msg = "welcome test nio zbx".getBytes();
//		
//		for(int i=0;i<msg.length;i++) {
//			buffer.put(msg[i]);
//		}
//		System.out.println("filp前,position:"+buffer.position()+",limit:"+buffer.limit());
//		buffer.flip();
//		System.out.println("filp后,position:"+buffer.position()+",limit:"+buffer.limit());
//		channel.write(buffer);
//		System.out.println("write后,position:"+buffer.position()+",limit:"+buffer.limit());
//		outputStream.close();
		
		
		ByteBuffer buffer = ByteBuffer.allocate(10);
		for(int i=0;i<buffer.capacity();i++) {
			buffer.put((byte)i);
		}
		buffer.position(2);
		buffer.limit(6);
		
		ByteBuffer sliceBuffer = buffer.slice();
		for(int i=0;i<sliceBuffer.capacity();i++) {
			byte b = sliceBuffer.get(i);
			b*=2;
			sliceBuffer.put(b);
		}
		
		buffer.clear();
		
		while(buffer.hasRemaining()) {
			System.out.println(buffer.get());
		}
		
		
	}
}
