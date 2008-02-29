package org.mtl.wiimote.service;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.GenericValidator;
import org.mtl.wiimote.device.WiimoteManager;
import org.mtl.wiimote.exception.WiimoteNotConnectException;
import org.mtl.wiimote.exception.WiimoteNotFoundException;

/**
 * WiimoteOverHTTPサーブレットクラス
 * @author nemoto
 * @version 0.1
 */
public class WiimoteServiceAction extends HttpServlet{
	/** Default Serial Version */
	private static final long serialVersionUID = 1L;	
	
	/** Wiiリモコン管理クラス*/
	private static WiimoteManager manager = null;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// XMLステータス初期化
		Integer returnStatus = Constant.STATUS_OK;
		
		// Wiiリモコン管理クラスを初期化
		if(manager == null){
			manager = new WiimoteManager();
		}
		
		// レスポンスのライターを取得
		PrintWriter pw = response.getWriter();

		// リクエストパラメタ取得・実行
		String method 	= request.getParameter(Constant.METHOD);
		String wiimote 	= request.getParameter(Constant.WIIMOTE);
		String time 	= request.getParameter(Constant.TIME);
		String button	= request.getParameter(Constant.BUTTON);
		String light	= request.getParameter(Constant.LIGHT);

		try {
			if(!GenericValidator.isBlankOrNull(method)){
				if(method.equals(Constant.FIND_REMOTE)){
					manager.findWiimote();
				}else if(method.equals(Constant.POSITION_INFO)){
					if(wiimote != null && GenericValidator.isInt(wiimote)){
						// TODO
					}
				}else if(method.equals(Constant.VIBRATE_FOR)){
					if(wiimote != null && GenericValidator.isInt(wiimote) && 
							time != null && GenericValidator.isLong(time)){
						manager.vibrateFor(Integer.parseInt(wiimote), Integer.parseInt(time));
					}
				}else if(method.equals(Constant.IS_PRESSED)){
					if(wiimote != null && GenericValidator.isInt(wiimote) && 
							button != null && button.matches("HOME|MINUS|PLUS|A|B|ONE|TWO|UP|DOWN|LEFT|RIGHT")){
						boolean bol = manager.isPressed(Integer.parseInt(wiimote), button);
						System.out.println(Boolean.toString(bol));
					}
				}else if(method.equals(Constant.SET_LED_LIGHTS)){
					if(wiimote != null && GenericValidator.isInt(wiimote) && 
							light != null && light.matches("^[01],[01],[01],[01]$")){
						String[] sptn = light.split(",");
						boolean[] pattern = {
								(sptn[0].equals("1")?true:false), (sptn[1].equals("1")?true:false),
								(sptn[2].equals("1")?true:false), (sptn[3].equals("1")?true:false),};
						manager.setLEDLights(Integer.parseInt(wiimote), pattern);
					}
				}
			}
			response.sendRedirect("./");
		} catch (IllegalStateException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (WiimoteNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (WiimoteNotConnectException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}finally{
			pw.flush();
			pw.close();
		}
	}
}
