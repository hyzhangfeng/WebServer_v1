package com.tedu.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.tedu.core.HttpContext;
import com.tedu.http.HttpRequest;
import com.tedu.http.HttpResponse;

/**
 * 用来处理登录业务
 * @author adminitartor
 *
 */
public class LoginServlet extends HttpServlet{
	
	public void service(HttpRequest request,HttpResponse response){
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		System.out.println(username+":"+password);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream("user.txt")));
			//按行读取每个用户的信息
			String line = null;
			boolean login = false;//默认登录失败!
			while((line = br.readLine())!=null){
				//按照","拆分用户信息
				String[] infos = line.split(",");
				String user = infos[0];
				String pwd = infos[1];
				System.out.println("正在比对的用户:"+user+":"+pwd);
				if(username.equals(user)&&password.equals(pwd)){
					login = true;
					break;
				}
			}
			if(login){
				System.out.println("登录成功");
				forward("/myweb/login_success.html", request, response);
			}else{
				System.out.println("登录失败");
				forward("/myweb/login_error.html", request, response);
			}
						
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(br != null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}




