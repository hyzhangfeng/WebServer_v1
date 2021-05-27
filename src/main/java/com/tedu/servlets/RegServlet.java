package com.tedu.servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import com.tedu.core.HttpContext;
import com.tedu.http.HttpRequest;
import com.tedu.http.HttpResponse;

/**
 * 处理注册业务
 * @author adminitartor
 *
 */
public class RegServlet extends HttpServlet{
	
	public void service(HttpRequest request,HttpResponse response){
		/*
		 * 首先获取用户输入的注册信息
		 * 将用户注册信息用下面的格式按行写入
		 * 到user.txt文件中保存
		 * 格式:username,password,nickname
		 * 例如:fancq,123456,fanfan
		 */
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String nickname = request.getParameter("nickname");
		System.out.println(username+","+password+","+nickname);
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("user.txt",true)));
			pw.println(username+","+password+","+nickname);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(pw!=null){
				pw.close();
			}
		}
		System.out.println("注册完毕!");
		forward("/myweb/reg_success.html", request, response);
	}
}





