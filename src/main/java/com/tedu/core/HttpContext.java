package com.tedu.core;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * Http协议相关内容定义
 * @author adminitartor
 *
 */
public class HttpContext {
	public static final int CR = 13;
	public static final int LF = 10;
	/**
	 * 响应状态代码定义
	 */
	/**
	 * 代码:正常
	 */
	public static final int STATUS_CODE_OK = 200;
	/**
	 * 代码:未找到
	 */
	public static final int STATUS_CODE_NOT_FOUND = 404;
	/**
	 * 代码:错误
	 */
	public static final int STATUS_CODE_ERROR = 500;
	/**
	 * 状态代码与描述的映射
	 */
	private static Map<Integer,String> code_reason_mapping;
	
	/**
	 * 介质类型映射
	 * key:介质类型
	 * value:Content-Type对应该类型的值
	 */
	private static Map<String,String> mimeTypeMapping;
	
	static{
		//HttpContext加载的时候开始初始化
		init();
	}
	
	/**
	 * 初始化HttpContext相关内容
	 */
	public static void init(){
		//1 初始化介质类型映射
		initMimeTypeMapping();
		//2 初始化状态代码与描述的映射
		initCodeReasonMapping();
	}
	/**
	 * 初始化状态代码与描述的映射
	 */
	private static void initCodeReasonMapping(){
		code_reason_mapping = new HashMap<Integer,String>();
		code_reason_mapping.put(STATUS_CODE_OK, "OK");
		code_reason_mapping.put(STATUS_CODE_ERROR, "ERROR");
		code_reason_mapping.put(STATUS_CODE_NOT_FOUND, "NOT FOUND");
	}
	
	/**
	 * 初始化介质类型映射
	 */
	private static void initMimeTypeMapping(){
		mimeTypeMapping = new HashMap<String,String>();
		/*
		 * 解析conf/web.xml文档
		 * 将根标签中所有的<mime-mapping>标签读取出来
		 * 将其中的子标签<extension>中的内容作为key
		 * 将其中的子标签<mime-type>中的内容作为value
		 * 存入到mimeTypeMapping中即可
		 */
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read(
				new FileInputStream("conf"+File.separator+"web.xml")
			);
			Element root = doc.getRootElement();
			List<Element> list = root.elements("mime-mapping");
			for(Element ele : list){
				String key = ele.elementText("extension");
				String value = ele.elementText("mime-type");
				mimeTypeMapping.put(key, value);
			}
			System.out.println(mimeTypeMapping.size());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
//		mimeTypeMapping.put("html", "text/html");
//		mimeTypeMapping.put("jpg", "image/jpg");
//		mimeTypeMapping.put("gif", "image/gif");
//		mimeTypeMapping.put("png", "image/png");
	}
	/**
	 * 根据给定的介质类型获取对应的Content-Type的值
	 * 
	 * @param mime
	 * @return
	 */
	public static String getContentTypeByMime(String mime){
		return mimeTypeMapping.get(mime);
	}
	/**
	 * 获取给定的状态代码所对应的状态描述
	 * @param code
	 * @return
	 */
	public static String getReasonByCode(int code){
		return code_reason_mapping.get(code);
	}
	public static void main(String[] args) {
		String contentType =
			HttpContext.getContentTypeByMime("css");
		System.out.println(contentType);
	}
}






