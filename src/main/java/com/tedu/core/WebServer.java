package com.tedu.core;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.tedu.http.HttpRequest;
import com.tedu.http.HttpResponse;
import com.tedu.servlets.HttpServlet;

/**
 * Web服务端主类
 * @author adminitartor
 *
 */
public class WebServer {
	
	private ServerSocket server;
	
	private ExecutorService threadPool;
	
	public WebServer(){
		try {
			System.out.println("正在初始化服务端...");
			server = new ServerSocket(8080);
			threadPool = Executors.newFixedThreadPool(30);
			System.out.println("服务端初始化完毕!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void start(){
		try {
			while(true){
				System.out.println("等待客户端连接...");
				Socket socket = server.accept();
				ClientHandler handler = new ClientHandler(socket);
				threadPool.execute(handler);
				System.out.println("一个客户端连接了!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		WebServer server = new WebServer();
		server.start();
	}
	/**
	 * 处理客户端请求并完成响应
	 * @author adminitartor
	 *
	 */
	private class ClientHandler implements Runnable{
		private Socket socket;
		public ClientHandler(Socket socket){
			this.socket = socket;
		}
		
		public void run(){
			try {
				System.out.println("处理客户端请求!");
				//根据输入流解析请求
				HttpRequest request = new HttpRequest(socket.getInputStream());
				//根据输出流创建响应对象
				HttpResponse response = new HttpResponse(socket.getOutputStream());
				//打桩 http://localhost:8080/myweb/index.html
				System.out.println("requestLine:"+request.getRequestLine());
				/*
				 * 先判断用户请求的是否为业务功能
				 */
				if(ServerContext.servletMapping.containsKey(request.getRequestLine())){
					System.out.println("请求的是一个Servlet!");
					//通过请求路径找到对应的Servlet名字
					String className = ServerContext.servletMapping.get(request.getRequestLine());
					System.out.println("该Servlet名字:"+className);
					//通过反射机制加载这个类
					Class cls = Class.forName(className);
					System.out.println("反射完毕");
					//实例化这个Servlet
					HttpServlet servlet = (HttpServlet)cls.newInstance();
					System.out.println("实例化该Servlet:"+servlet);
					servlet.service(request, response);
					
				}else{
					/*
					 * 查看请求的该页面是否存在
					 */
					File file = new File("webapps"+request.getRequestLine());
					if(file.exists()){
						System.out.println("该文件存在!");
						/*
						 * 响应客户端
						 */
						//设置响应头信息
						forward(request.getRequestLine(), request, response);
					}else{
						//设置状态代码
						response.setStatusCode(HttpContext.STATUS_CODE_NOT_FOUND);
						forward("/global/404.html",request,response);
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				//处理客户端断开连接后的操作
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		/**
		 * 跳转页面
		 */
		private void forward(String path,HttpRequest request,HttpResponse response){
			try {
				File file = new File("webapps"+path);
				String name = file.getName().substring(file.getName().lastIndexOf(".")+1);
				String contentType = HttpContext.getContentTypeByMime(name);
				//设置响应头Content-Type
				response.setContentType(contentType);
				response.setContentLength((int)file.length());
				//设置响应正文
				response.setEntity(file);
				//响应客户端
				response.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}









