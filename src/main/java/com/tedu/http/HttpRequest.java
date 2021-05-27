package com.tedu.http;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.tedu.core.HttpContext;

/**
 * 该类的每一个实例用于表示客户端发送过来的
 * 一次HTTP请求内容
 * 这里包含:请求行信息,消息头,消息正文
 * @author adminitartor
 *
 */
public class HttpRequest {
	/*
	 * 请求行中的内容
	 */
	//请求的方法
	private String method;
	//请求的资源路径
	private String uri;
	//请求使用的HTTP协议版本
	private String protocol;
	
	//请求信息
	private String requestLine;
	//请求所附带的所有参数
	private Map<String,String> params = new HashMap<String,String>();
	
	/*
	 * 消息头
	 */
	private Map<String,String> headers = new HashMap<String,String>();
	
	/**
	 * 构造方法,通过给定的输入流读取客户端发送
	 * 过来的HTTP请求内容
	 * @param in
	 */
	public HttpRequest(InputStream in){
		/*
		 * 解析分为三步
		 * 1:解析请求行
		 * 2:解析消息头
		 * 3:解析消息正文
		 */
		parseRquestLine(in);
		parseHeaders(in);
		parseContent(in);
	}
	/**
	 * 解析消息正文
	 * @param in
	 */
	private void parseContent(InputStream in){
		//获取消息头中的Content-Type
		String contentType = this.headers.get("Content-Type");
		if(contentType!=null && "application/x-www-form-urlencoded".equals(contentType)){
			System.out.println("!!!!!!!!!!解析表单数据!!!!!!!!!!");
			int contentLength = Integer.parseInt(this.headers.get("Content-Length"));
			try {
				byte[] buf = new byte[contentLength];
				in.read(buf);
				String line = new String(buf);
				System.out.println("form表单内容:"+line);
				line = URLDecoder.decode(line, "UTF-8");
				System.out.println("解码后的form表单内容:"+line);
				parseParams(line);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * 解析消息头
	 */
	private void parseHeaders(InputStream in){
		/*
		 * 由于之前的方法已经从流中将请求行内容
		 * 读取了,所以从这个流中继续读取的就应当
		 * 是消息头内容了.
		 * 
		 * 读取若干行内容(CRLF结尾算一行)
		 * 每一行按照":"拆分成两部分,第一部分
		 * 应当是消息头的名字,而第二部分为对应的值
		 * 将名字作为key,值作为value存入到header
		 * 这个Map中保存.
		 */
		while(true){
			String line = readLine(in);
			//单独读取到了CRLF
			if("".equals(line)){
				break;
			}
			//找到每条头信息中":"的位置
			int index = line.indexOf(":");
			String name = line.substring(0, index).trim();
			String value = line.substring(index+1).trim();
			this.headers.put(name, value);
		}
		System.out.println("消息头解析完毕!");
		this.headers.forEach((k,v)->System.out.println(k+":"+v));
	}
	
	/**
	 * 解析请求行
	 */
	private void parseRquestLine(InputStream in){
		/*
		 * 解析请求行的大致步骤:
		 * 1:通过输入流读取一行字符串
		 *   以CRLF结尾
		 *   CR:回车,ASC编码对应为13
		 *   LF:换行,ASC编码对应为10
		 * 2:将读取的请求行内容按照空格拆分
		 * 3:将拆出来的三个部分分别对应的设置
		 *   到属性method,uri,protocol上
		 *   
		 *   localhost:8080/index.html  
		 */
		try {
			String line = readLine(in);		
			String[] data = line.split("\\s");
			System.out.println(data.length);
			System.out.println(Arrays.toString(data));
			
			this.method = data[0];
			this.uri = data[1];
			parseUri();
			this.protocol = data[2];
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 解析URI
	 * URI可能会包含客户端传递过来的数据
	 * 所以要对它进行解析
	 * 例如:
	 * uri:/myweb/reg?user=fancq&pwd=12345
	 * 或
	 * uri:/myweb/reg.html
	 * 对于第一种情况,需要将?左面的内容赋值
	 * 给requestLine这个属性,而右面的内容
	 * 则每个参数都存入params这个map中
	 * 而第二种情况由于没有参数,那么直接将
	 * uri赋值给requestLine即可.
	 */
	private void parseUri(){
		//判断uri是否含有?
		int index = this.uri.indexOf("?");
		if(index==-1){
			this.requestLine = this.uri;
		}else{
			this.requestLine = this.uri.substring(0, index);
			String queryStr = this.uri.substring(index+1);
			parseParams(queryStr);
		}
	}
	/**
	 * 解析浏览器发送过来的参数
	 * @param line
	 */
	private void parseParams(String line){
		String[] paramArr = line.split("&");
		for(String paramStr : paramArr){
			String[] para = paramStr.split("=");
			if(para.length==2){
				this.params.put(para[0], para[1]);
			}else if(para.length==1){
				this.params.put(para[0], "");
			}
		}
	}
	
	private String readLine(InputStream in){
		StringBuilder builder = new StringBuilder();
		try {
			int d = -1;
			char c1=0,c2=0;//c1表示上次读到的字节,c2表示本读到的字节
			while((d = in.read())!=-1){
				c2 = (char)d;
				if(c1==HttpContext.CR&&
				   c2==HttpContext.LF){
					break;
				}
				builder.append(c2);
				c1 = c2;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return builder.toString().trim();
	}
	/**
	 * 根据给定的参数名获取参数的值
	 * @param name
	 * @return
	 */
	public String getParameter(String name){
		return params.get(name);
	}
	
	public String getRequestLine() {
		return requestLine;
	}
	public String getMethod() {
		return method;
	}
	public String getUri() {
		return uri;
	}
	public String getProtocol() {
		return protocol;
	}
	public Map<String, String> getHeaders() {
		return headers;
	}
	
	
	
}



