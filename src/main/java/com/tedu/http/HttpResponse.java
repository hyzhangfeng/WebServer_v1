package com.tedu.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.tedu.core.HttpContext;

/**
 * 表示一个Http响应
 * @author adminitartor
 *
 */
public class HttpResponse {
	/*
	 * 用于向客户端做响应的输出流.
	 * 它应该是ClientHandler通过Socket
	 * 获取的输出流.
	 */
	private OutputStream out;
	/*
	 * 一个要响应给客户端的文件资源
	 * 这个文件的数据最终会在响应正文中
	 * 以字节的形式发送给客户端
	 */
	private File entity;
	/*
	 * 当前响应中包含的所有响应头的信息
	 * key:响应头的名字
	 * value:响应头对应的值
	 * 例如:
	 * key:Content-Type
	 * value:text/html
	 */
	private Map<String,String> headers = new HashMap<String,String>();
	/*
	 * 当前响应的状态代码
	 */
	private int statusCode = HttpContext.STATUS_CODE_OK;
	
	public HttpResponse(OutputStream out){
		this.out = out;
	}
	/**
	 * 将该响应对象中的内容响应给客户端
	 */
	public void flush(){
		/*
		 * 发送给客户端的HTTP响应分为三步:
		 * 1:发送状态行信息
		 * 2:发送响应头信息
		 * 3:发送响应正文信息
		 */
		sendStatusLine();
		sendHeaders();
		sendContent();		
	}
	/**
	 * 发送状态行信息
	 */
	private void sendStatusLine(){
		String line = "HTTP/1.1"+" "+statusCode+" "+HttpContext.getReasonByCode(statusCode);
		println(line);
	}
	/**
	 * 发送响应头信息
	 */
	private void sendHeaders(){
		Set<Entry<String,String>> headerSet = headers.entrySet();
		for(Entry<String,String> header : headerSet){
			String line = header.getKey()+":"+header.getValue();
			println(line);//发送每一个头信息
		}
		println("");//单独发送CRLF表示头发送完毕
	}
	/**
	 * 发送响应正文信息
	 */
	private void sendContent(){
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(entity);
			int len = -1;
			byte[] data = new byte[1024*10];
			while((len = fis.read(data))!=-1){
				out.write(data,0,len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(fis != null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 向客户端发送一行字符串
	 * 该字符串会通过ISO8859-1转换为一组
	 * 字节并写出.写出后会自动连续写出CRLF
	 * @param line
	 */
	private void println(String line){
		try {
			out.write(line.getBytes("ISO8859-1"));
			out.write(HttpContext.CR);
			out.write(HttpContext.LF);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 根据实体文件的名字获取对应的介质类型
	 * Content-Type使用的值
	 * 
	 * 临时先将该方法写在response中.实际上
	 * Content-Type的值应当是在设置response
	 * 内容的时候一同设置的.
	 * @return
	 */
	private String getMimeTypeByEntity(){
//	private static String getMimeTypeByEntity(String filename){
		/*
		 * html    text/html
		 * jpg     image/jpg
		 * png     image/png
		 * gif     image/gif
		 * 
		 * filename:logo.png
		 */
		String name = entity.getName().substring(entity.getName().lastIndexOf(".")+1);
		if("html".equals(name)){
			return "text/html";
		}else if("jpg".equals(name)){
			return "image/jpg";
		}else if("png".equals(name)){
			return "image/png";
		}else if("gif".equals(name)){
			return "image/gif";
		}
		return "";
	}
	/**
	 * 设置头信息Content-Type对应的值
	 * @param contentType
	 */
	public void setContentType(String contentType){
		this.headers.put("Content-Type", contentType);
	}
	/**
	 * 设置头信息Content-Length
	 * @param length
	 */
	public void setContentLength(int length){
		this.headers.put("Content-Length", length+"");
	}
	/**
	 * 设置状态代码
	 * @param code
	 */
	public void setStatusCode(int code){
		this.statusCode = code;
	}
	
	public File getEntity() {
		return entity;
	}

	public void setEntity(File entity) {
		this.entity = entity;
	}
	
//	public static void main(String[] args) {
//		String str = getMimeTypeByEntity(
//			"logo.html");
//		System.out.println(str);//image/png
//	}
}











