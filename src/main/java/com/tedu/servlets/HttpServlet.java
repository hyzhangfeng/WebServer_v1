package com.tedu.servlets;

import java.io.File;

import com.tedu.core.HttpContext;
import com.tedu.http.HttpRequest;
import com.tedu.http.HttpResponse;

/**
 * 所有Servlet的超类.定义Servlet都应当
 * 具备的功能
 * @author adminitartor
 *
 */
public abstract class HttpServlet {
	public abstract void service(HttpRequest request,HttpResponse response);
	
	/**
	 * 跳转页面
	 */
	public void forward(String path,HttpRequest request,HttpResponse response){
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






