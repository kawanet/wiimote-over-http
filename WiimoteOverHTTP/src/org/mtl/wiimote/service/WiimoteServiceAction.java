package org.mtl.wiimote.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.arnx.jsonic.JSON;

import org.apache.commons.validator.GenericValidator;
import org.mtl.wiimote.device.Wiimote;
import org.mtl.wiimote.device.WiimoteManager;
import org.mtl.wiimote.exception.WiimoteNotConnectException;
import org.mtl.wiimote.exception.WiimoteNotFoundException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * WiimoteOverHTTPサーブレットクラス
 * @author nemoto.hrs
 * @version 0.1
 */
public class WiimoteServiceAction extends HttpServlet{
	/** Default Serial Version */
	private static final long serialVersionUID = 1L;	
	
	/** Wiiリモコン管理クラス*/
	private static WiimoteManager manager = null;
	/** XMLドキュメント */
	private Document document = null;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		// XMLステータス初期化
		Integer returnStatus = Constant.STATUS_OK;
		
		// Wiiリモコン管理クラスを初期化
		if(manager == null){
			manager = new WiimoteManager();
		}
		
		// リクエストパラメタ取得・実行
		String method 	= request.getParameter(Constant.METHOD);
		String wiimote 	= request.getParameter(Constant.WIIMOTE);
		String time 	= request.getParameter(Constant.TIME);
		String button	= request.getParameter(Constant.BUTTON);
		String light	= request.getParameter(Constant.LIGHT);
		String resType 	= request.getParameter(Constant.RESPONSE_TYPE);

		Element responseNode = null;
		try {
			// response ノード作成
			responseNode = this.createRootNode(Constant.NODE_RESPONSE);
			// method ノード作成
			responseNode.appendChild(this.createNode(Constant.NODE_METHOD, method));

			// Wiiリモコンに対する処理の実行
			if(!GenericValidator.isBlankOrNull(method)){
				if(method.equals(Constant.FIND_REMOTE)){
					this.findRemote(responseNode);
				}else if(method.equals(Constant.IS_CONNECTED)){
					this.isConnected(responseNode, wiimote);
				}else if(method.equals(Constant.POSITION_INFO)){
					this.getPositionInfo(responseNode, wiimote);
				}else if(method.equals(Constant.VIBRATE_FOR)){
					this.vibrateFor(responseNode, wiimote, time);
				}else if(method.equals(Constant.IS_PRESSED)){
					this.isPressed(responseNode, wiimote, button);
				}else if(method.equals(Constant.SET_LED_LIGHTS)){
					this.setLEDLights(responseNode, wiimote, light);
				}else{
					// status ノード追加
					returnStatus = Constant.STATUS_METHOD_NOT_FOUND;
					responseNode.appendChild(this.createNode(Constant.NODE_STATUS, returnStatus.toString()));
				}
			}else{
				// status ノード追加
				returnStatus = Constant.STATUS_METHOD_NOT_FOUND;
				responseNode.appendChild(this.createNode(Constant.NODE_STATUS, returnStatus.toString()));
			}
		}catch(Exception e){
			// status ノード追加
			returnStatus = Constant.STATUS_NG;
			responseNode.appendChild(this.createNode(Constant.NODE_STATUS, returnStatus.toString()));
			e.printStackTrace();
		}finally{
			response.setStatus(Constant.STATUS_OK);
			// レスポンスのライターを取得
			PrintWriter pw = response.getWriter();
			// 結果を出力
			this.write(pw, resType);
			pw.flush();
			pw.close();
		}
	}
	
	/**
	 * Wiiリモコン探索
	 * @param rNode XML基点ノード
	 */
	private void findRemote(Element rNode){
		Integer st = 0;
		try{
			manager.findWiimote();
			st = Constant.STATUS_OK;
		}catch(Exception e){
			st = Constant.STATUS_NG;
		}
		// status ノード追加
		rNode.appendChild(this.createNode(Constant.NODE_STATUS, st.toString()));
	}

	/**
	 * Wiiリモコン接続状況取得
	 * @param rNode XML基点ノード
	 * @param wiimote WiiリモコンNo.
	 */
	private void isConnected(Element rNode, String wiimote){
		Integer st = 0;
		boolean bol = false;
		try{
			if(wiimote != null && GenericValidator.isInt(wiimote)){
				bol = manager.isConnected(Integer.parseInt(wiimote));
				st = Constant.STATUS_OK;
			}else{
				st = Constant.STATUS_PARAM_NG;
			}
		}catch(WiimoteNotFoundException e){
			st = Constant.STATUS_WIIMOTE_NOT_FOUND;
		}catch(Exception e){
			st = Constant.STATUS_NG;
		}
		// status ノード追加
		rNode.appendChild(this.createNode(Constant.NODE_STATUS, st.toString()));
		// data ノード追加
		Element data = this.createNode(Constant.NODE_DATA, null);
		data.appendChild(this.createNode(Constant.NODE_BOOL, (bol?"1":"0")));
		rNode.appendChild(data);
	}
	
	/**
	 * TODO 
	 * Wiiリモコン位置情報取得
	 * @param rNode XML基点ノード
	 * @param wiimote WiiリモコンNo.
	 */
	private void getPositionInfo(Element rNode, String wiimote){
		Integer st = 0;
		Map<Integer, Double> posInfo = null;
		try {
			if(wiimote != null && GenericValidator.isInt(wiimote)){
				posInfo = manager.getPositionInfo(Integer.parseInt(wiimote));
				st = Constant.STATUS_OK;
			}else{
				st = Constant.STATUS_PARAM_NG;
			}
		}catch(WiimoteNotFoundException e){
			st = Constant.STATUS_WIIMOTE_NOT_FOUND;
		}catch(WiimoteNotConnectException e){
			st = Constant.STATUS_WIIMOTE_NOT_FOUND;
		}catch(Exception e){
			st = Constant.STATUS_NG;
		}
		// status ノード追加
		rNode.appendChild(this.createNode(Constant.NODE_STATUS, st.toString()));
		// data ノード追加
		Element data = this.createNode(Constant.NODE_DATA, null);
		data.appendChild(this.createNode(Constant.NODE_X_POS, posInfo.get(Wiimote.POS_X).toString()));
		data.appendChild(this.createNode(Constant.NODE_Y_POS, posInfo.get(Wiimote.POS_Y).toString()));
		data.appendChild(this.createNode(Constant.NODE_Z_POS, posInfo.get(Wiimote.POS_Z).toString()));
		data.appendChild(this.createNode(Constant.NODE_PITCH, posInfo.get(Wiimote.POS_PITCH).toString()));
		data.appendChild(this.createNode(Constant.NODE_ROLL,  posInfo.get(Wiimote.POS_ROLL).toString()));
		rNode.appendChild(data);
	}
	
	/**
	 * Wiiリモコンバイブレーション操作
	 * @param rNode XML基点ノード
	 * @param wiimote WiiリモコンNo.
	 * @param time バイブレーション時間
	 */
	private void vibrateFor(Element rNode, String wiimote, String time){
		Integer st = 0;
		try {
			if(wiimote != null && GenericValidator.isInt(wiimote) && 
					time != null && GenericValidator.isLong(time)){
				manager.vibrateFor(Integer.parseInt(wiimote), Long.parseLong(time));
				st = Constant.STATUS_OK;
			}else{
				st = Constant.STATUS_PARAM_NG;				
			}
		}catch(WiimoteNotFoundException e){
			st = Constant.STATUS_WIIMOTE_NOT_FOUND;
		}catch(WiimoteNotConnectException e){
			st = Constant.STATUS_WIIMOTE_NOT_FOUND;
		}catch(Exception e){
			st = Constant.STATUS_NG;
		}
		// status ノード追加
		rNode.appendChild(this.createNode(Constant.NODE_STATUS, st.toString()));
	}
	
	/**
	 * Wiiリモコンボタン押下状況取得
	 * @param wiimote WiiリモコンNo.
	 * @param button ボタン名
	 * @return ステータス
	 */
	private void isPressed(Element rNode, String wiimote, String button){
		Integer st = 0;
		boolean bol = false;
		try{
			if(wiimote != null && GenericValidator.isInt(wiimote) && 
					button != null && button.matches("HOME|MINUS|PLUS|A|B|ONE|TWO|UP|DOWN|LEFT|RIGHT")){
				bol = manager.isPressed(Integer.parseInt(wiimote), button);
				st = Constant.STATUS_OK;
			}else{
				st = Constant.STATUS_PARAM_NG;
			}
		}catch(WiimoteNotFoundException e){
			st = Constant.STATUS_WIIMOTE_NOT_FOUND;
		}catch(WiimoteNotConnectException e){
			st = Constant.STATUS_WIIMOTE_NOT_FOUND;
		}catch(Exception e){
			st = Constant.STATUS_NG;
		}
		// status ノード追加
		rNode.appendChild(this.createNode(Constant.NODE_STATUS, st.toString()));
		// data ノード追加
		Element data = this.createNode(Constant.NODE_DATA, null);
		data.appendChild(this.createNode(Constant.NODE_BOOL, (bol?"1":"0")));
		rNode.appendChild(data);
	}
	
	/**
	 * WiiリモコンLED点灯・消灯操作
	 * @param wiimote WiiリモコンNo.
	 * @param light LED点灯パターン
	 * @return ステータス
	 */
	private void setLEDLights(Element rNode, String wiimote, String light){
		Integer st = 0;
		try{
			if(wiimote != null && GenericValidator.isInt(wiimote) && 
					light != null && light.matches("^[01],[01],[01],[01]$")){
				String[] sptn = light.split(",");
				boolean[] pattern = {
						(sptn[0].equals("1")?true:false), (sptn[1].equals("1")?true:false),
						(sptn[2].equals("1")?true:false), (sptn[3].equals("1")?true:false),};
				manager.setLEDLights(Integer.parseInt(wiimote), pattern);
				st = Constant.STATUS_OK;
			}else{
				st = Constant.STATUS_PARAM_NG;
			}
		}catch(WiimoteNotFoundException e){
			st = Constant.STATUS_WIIMOTE_NOT_FOUND;
		}catch(WiimoteNotConnectException e){
			st = Constant.STATUS_WIIMOTE_NOT_FOUND;
		}catch(Exception e){
			st = Constant.STATUS_NG;
		}
		// status ノード追加
		rNode.appendChild(this.createNode(Constant.NODE_STATUS, st.toString()));
	}
	
	/**
	 * 基点ノードを作成
	 * @param docName 基点ノード名
	 * @return ノードオブジェクト
	 * @throws ParserConfigurationException 
	 * @throws ParserConfigurationException
	 */
	private Element createRootNode(String docName) throws ParserConfigurationException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		DOMImplementation domImpl = builder.getDOMImplementation();
		document = domImpl.createDocument("",docName, null);
		return document.getDocumentElement();
	}
	
	/**
	 * ノードを作成
	 * @param nodeName ノード名
	 * @param nodeValue ノードの値
	 * @return ノードオブジェクト
	 */
	private Element createNode(String nodeName, String nodeValue){
		Element node = document.createElement(nodeName);
		if(nodeValue != null){
			node.appendChild(document.createTextNode(nodeValue));
		}
		return node;
	}
	
	/**
	 * 出力を行う
	 * @param writer 
	 * @param type
	 */
	private void write(Writer writer, String type){
		try{
			if(type == null || type.equals(Constant.RESPONSE_XML)){
				TransformerFactory transFactory = TransformerFactory.newInstance();
				Transformer transformer = transFactory.newTransformer();
	
				DOMSource source = new DOMSource(document);
				StreamResult result = new StreamResult(writer);
				transformer.transform(source, result);				
			}else if(type.equals(Constant.RESPONSE_JSON)){
				writer.write(JSON.encode(document));
			}else{
				writer.write("illigal responseType");
			}
		}catch(TransformerConfigurationException e){
			e.printStackTrace();
		}catch(TransformerException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
