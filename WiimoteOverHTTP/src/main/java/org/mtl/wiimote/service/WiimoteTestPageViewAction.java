package org.mtl.wiimote.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * WiimoteOverHTTPサーブレットクラス
 * @author nemoto.hrs
 * @version 0.2
 */
public class WiimoteTestPageViewAction extends HttpServlet{
	/** Default Serial Version */
	private static final long serialVersionUID = 1L;	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		FileInputStream welcomePage = null;
		OutputStream oStream = null;
		try{
			response.setContentType("text/html; charset=UTF-8");
			response.setCharacterEncoding(Constant.UTF8);
			welcomePage = new FileInputStream(new File("index.html"));
			oStream = response.getOutputStream();
			int i = -1;
			while((i = welcomePage.read()) != -1){
				oStream.write(i);
			}
		}finally{
			if(welcomePage != null)welcomePage.close();
			if(oStream != null){
				oStream.flush();
				oStream.close();
			}
		}
	}
}
