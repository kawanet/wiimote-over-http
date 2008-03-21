package org.mtl.wiimote.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
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

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

import org.apache.commons.validator.GenericValidator;
import org.mtl.wiimote.device.Wiimote;
import org.mtl.wiimote.device.WiimoteManager;
import org.mtl.wiimote.exception.WiimoteNotConnectException;
import org.mtl.wiimote.exception.WiimoteNotFoundException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
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
	
	/** JSONオブジェクト */
	private JSONObject jobj = new JSONObject();

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
		String callback = request.getParameter(Constant.CALLBACK);

		Element responseNode = null;
		try {
			// response ノード作成
			responseNode = this.createRootNode(Constant.NODE_RESPONSE);
			// method ノード作成
			responseNode.appendChild(this.createNode(Constant.NODE_METHOD, method));
			
//			jobj.put(Constant.NODE_METHOD, method);
//			jobj.put(Constant.NODE_STATUS, returnStatus.toString());
			
			// Wiiリモコンに対する処理の実行
			if(!GenericValidator.isBlankOrNull(method)){
				if(method.equals(Constant.FIND_WIIMOTE)){
					this.findWiimote(responseNode);
				}else if(method.equals(Constant.RELEASE_WIIMOTE)){
					this.releaseWiimote(responseNode, wiimote);
				}else if(method.equals(Constant.IS_CONNECTED)){
					this.isConnected(responseNode, wiimote);
				}else if(method.equals(Constant.POSITION_INFO)){
					this.getPositionInfo(responseNode, wiimote);
				}else if(method.equals(Constant.GET_STATUS)){
					this.getStatus(responseNode, wiimote);
//					this.getStatus(jobj, wiimote);
				}else if(method.equals(Constant.SET_VIBRATE)){
					this.setVibrate(responseNode, wiimote, time);
				}else if(method.equals(Constant.IS_PRESSED)){
					this.isPressed(responseNode, wiimote, button);
				}else if(method.equals(Constant.SET_LED)){
					this.setLED(responseNode, wiimote, light, time);
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
			// 結果を出力
			this.writeResponse(response, resType, callback);
		}
	}
	
	/**
	 * Wiiリモコン探索・接続
	 * @param rNode XML基点ノード
	 */
	private void findWiimote(Element rNode){
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
	 * Wiiリモコン切断
	 * @param rNode XML基点ノード
	 */
	private void releaseWiimote(Element rNode, String wiimote){
		Integer st = 0;
		try{
			if(wiimote != null && GenericValidator.isInt(wiimote)){
				manager.releaseWiimote(Integer.parseInt(wiimote));
			}else{
				manager.releaseAllWiimote();
			}
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
	 * Wiiリモコン情報取得
	 * @param rNode XML基点ノード
	 * @param wiimote WiiリモコンNo.
	 */
	private void getStatus(Element rNode, String wiimote){
		Integer st = 0;
		Map allInfo = null;
		try {
			if(wiimote != null && GenericValidator.isInt(wiimote)){
				allInfo = manager.getStatus(Integer.parseInt(wiimote));
			}else{
				allInfo = manager.getAllStatus();
			}
			st = Constant.STATUS_OK;

		}catch(WiimoteNotFoundException e){
			st = Constant.STATUS_WIIMOTE_NOT_FOUND;
		}catch(WiimoteNotConnectException e){
			st = Constant.STATUS_WIIMOTE_NOT_FOUND;
		}catch(Exception e){
			st = Constant.STATUS_NG;
		}
		// status ノード追加
		rNode.appendChild(this.createNode(Constant.NODE_STATUS, st.toString()));
		if(allInfo != null){
			// data ノード追加
			Element data = this.createNode(Constant.NODE_DATA, null);
			Iterator itr = allInfo.keySet().iterator();
			while(itr.hasNext()){
				Integer idx = (Integer)itr.next();
				Map info = (Map)allInfo.get(idx);
				Element wiiElm = this.createNode(Constant.NODE_WIIMOTE, Constant.ATTR_INDEX, idx.toString(), null);
				wiiElm.appendChild(this.createNode(Constant.NODE_X_POS, 		info.get(Wiimote.POS_X).toString()));
				wiiElm.appendChild(this.createNode(Constant.NODE_Y_POS, 		info.get(Wiimote.POS_Y).toString()));
				wiiElm.appendChild(this.createNode(Constant.NODE_Z_POS, 		info.get(Wiimote.POS_Z).toString()));
				wiiElm.appendChild(this.createNode(Constant.NODE_PITCH, 		info.get(Wiimote.POS_PITCH).toString()));
				wiiElm.appendChild(this.createNode(Constant.NODE_ROLL,  		info.get(Wiimote.POS_ROLL).toString()));
				wiiElm.appendChild(this.createNode(Constant.NODE_BTN_A, 		(Boolean)info.get(Wiimote.KEY_A)?"1":"0"));
				wiiElm.appendChild(this.createNode(Constant.NODE_BTN_B, 		(Boolean)info.get(Wiimote.KEY_B)?"1":"0"));
				wiiElm.appendChild(this.createNode(Constant.NODE_BTN_ONE, 		(Boolean)info.get(Wiimote.KEY_ONE)?"1":"0"));
				wiiElm.appendChild(this.createNode(Constant.NODE_BTN_TWO, 		(Boolean)info.get(Wiimote.KEY_TWO)?"1":"0"));
				wiiElm.appendChild(this.createNode(Constant.NODE_BTN_MINUS, 	(Boolean)info.get(Wiimote.KEY_MINUS)?"1":"0"));
				wiiElm.appendChild(this.createNode(Constant.NODE_BTN_PLUS, 		(Boolean)info.get(Wiimote.KEY_PLUS)?"1":"0"));
				wiiElm.appendChild(this.createNode(Constant.NODE_BTN_HOME, 		(Boolean)info.get(Wiimote.KEY_HOME)?"1":"0"));
				wiiElm.appendChild(this.createNode(Constant.NODE_BTN_UP, 		(Boolean)info.get(Wiimote.KEY_UP)?"1":"0"));
				wiiElm.appendChild(this.createNode(Constant.NODE_BTN_DOWN, 		(Boolean)info.get(Wiimote.KEY_DOWN)?"1":"0"));
				wiiElm.appendChild(this.createNode(Constant.NODE_BTN_LEFT, 		(Boolean)info.get(Wiimote.KEY_LEFT)?"1":"0"));
				wiiElm.appendChild(this.createNode(Constant.NODE_BTN_RIGHT, 	(Boolean)info.get(Wiimote.KEY_RIGHT)?"1":"0"));
				data.appendChild(wiiElm);
			}
			rNode.appendChild(data);
		}
	}

	/**
	 * Wiiリモコン情報取得
	 * @param rNode 基点ノード
	 * @param wiimote WiiリモコンNo.
	 */
	private void getStatus(JSONObject rNode, String wiimote){
		Integer st = 0;
		Map allInfo = null;
		try {
			if(wiimote != null && GenericValidator.isInt(wiimote)){
				allInfo = manager.getStatus(Integer.parseInt(wiimote));
			}else{
				allInfo = manager.getAllStatus();
			}
			st = Constant.STATUS_OK;

		}catch(WiimoteNotFoundException e){
			st = Constant.STATUS_WIIMOTE_NOT_FOUND;
		}catch(WiimoteNotConnectException e){
			st = Constant.STATUS_WIIMOTE_NOT_FOUND;
		}catch(Exception e){
			st = Constant.STATUS_NG;
		}
		// status ノード追加
		rNode.put(Constant.NODE_STATUS, st.toString());
		if(allInfo != null){
			// data ノード追加
			JSONArray wiimArr = new JSONArray();
			Iterator itr = allInfo.keySet().iterator();
			while(itr.hasNext()){
				Integer idx = (Integer)itr.next();
				Map info = (Map)allInfo.get(idx);
				JSONObject wiim = new JSONObject();
				JSONObject wiimVal = new JSONObject();
				wiimVal.put("@"+Constant.ATTR_INDEX, idx.toString());
				wiimVal.put(Constant.NODE_X_POS, 		info.get(Wiimote.POS_X).toString());
				wiimVal.put(Constant.NODE_Y_POS, 		info.get(Wiimote.POS_Y).toString());
				wiimVal.put(Constant.NODE_Z_POS, 		info.get(Wiimote.POS_Z).toString());
				wiimVal.put(Constant.NODE_PITCH, 		info.get(Wiimote.POS_PITCH).toString());
				wiimVal.put(Constant.NODE_ROLL,  		info.get(Wiimote.POS_ROLL).toString());
				wiimVal.put(Constant.NODE_BTN_A, 		(Boolean)info.get(Wiimote.KEY_A)?"1":"0");
				wiimVal.put(Constant.NODE_BTN_B, 		(Boolean)info.get(Wiimote.KEY_B)?"1":"0");
				wiimVal.put(Constant.NODE_BTN_ONE, 	(Boolean)info.get(Wiimote.KEY_ONE)?"1":"0");
				wiimVal.put(Constant.NODE_BTN_TWO, 	(Boolean)info.get(Wiimote.KEY_TWO)?"1":"0");
				wiimVal.put(Constant.NODE_BTN_MINUS, 	(Boolean)info.get(Wiimote.KEY_MINUS)?"1":"0");
				wiimVal.put(Constant.NODE_BTN_PLUS, 	(Boolean)info.get(Wiimote.KEY_PLUS)?"1":"0");
				wiimVal.put(Constant.NODE_BTN_HOME, 	(Boolean)info.get(Wiimote.KEY_HOME)?"1":"0");
				wiimVal.put(Constant.NODE_BTN_UP, 		(Boolean)info.get(Wiimote.KEY_UP)?"1":"0");
				wiimVal.put(Constant.NODE_BTN_DOWN, 	(Boolean)info.get(Wiimote.KEY_DOWN)?"1":"0");
				wiimVal.put(Constant.NODE_BTN_LEFT, 	(Boolean)info.get(Wiimote.KEY_LEFT)?"1":"0");
				wiimVal.put(Constant.NODE_BTN_RIGHT, 	(Boolean)info.get(Wiimote.KEY_RIGHT)?"1":"0");
				wiim.put(Constant.NODE_WIIMOTE, wiimVal);
				wiimArr.add(wiim);
			}
			rNode.element(Constant.NODE_DATA, wiimArr);
		}
	}
	
	/**
	 * Wiiリモコンバイブレーション操作
	 * @param rNode XML基点ノード
	 * @param wiimote WiiリモコンNo.
	 * @param time バイブレーション時間
	 */
	private void setVibrate(Element rNode, String wiimote, String time){
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
	private void setLED(Element rNode, String wiimote, String light, String time){
		Integer st = 0;
		try{
			if(wiimote != null && GenericValidator.isInt(wiimote) && 
					light != null && light.matches("^[01],[01],[01],[01]$") && 
					time != null && GenericValidator.isLong(time)){
				String[] sptn = light.split(",");
				boolean[] pattern = {
						(sptn[0].equals("1")?true:false), (sptn[1].equals("1")?true:false),
						(sptn[2].equals("1")?true:false), (sptn[3].equals("1")?true:false),};
				manager.setLEDLightsFor(Integer.parseInt(wiimote), pattern, Long.parseLong(time));
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
		document = domImpl.createDocument("", docName, null);
		return document.getDocumentElement();
	}
	
	/**
	 * ノードを作成
	 * @param nodeName ノード名
	 * @param nodeValue ノードの値
	 * @return ノードオブジェクト
	 */
	private Element createNode(String nodeName, String nodeValue){
		return this.createNode(nodeName, null, null, nodeValue);
	}

	/**
	 * ノードを作成
	 * @param nodeName ノード名
	 * @param nodeValue ノードの値
	 * @return ノードオブジェクト
	 */
	private Element createNode(String nodeName, String attrName, String attrValue, String nodeValue){
		Element node = null;
		if(nodeName != null){
			node = document.createElement(nodeName);
			if(attrName != null && attrValue != null){
				node.setAttribute(attrName, attrValue);
			}
			if(nodeValue != null){
				node.appendChild(document.createTextNode(nodeValue));
			}
		}
		return node;
	}
	

	/**
	 * 出力を行う
	 * @param writer 
	 * @param type
	 */
	private void writeResponse(HttpServletResponse response, String type, String callback){
		PrintWriter writer = null;
		try{
			// レスポンスのライターを取得
			response.setCharacterEncoding(Constant.UTF8);
			writer = response.getWriter();
			
			// XML DOM作成
			StringWriter xmlDom = new StringWriter();
			StreamResult result = new StreamResult(xmlDom);
			DOMSource source = new DOMSource(document);
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(source, result);

			// XML
			if(type == null || type.equals(Constant.RESPONSE_XML)){
				response.setContentType(Constant.HD_RESPONSE_XML);
				writer.write(xmlDom.toString());
//				XMLSerializer serializer = new XMLSerializer();
//				serializer.setRootName(Constant.NODE_RESPONSE);
//				serializer.setElementName(null);
//				serializer.setTypeHintsEnabled(false);
//				writer.write(serializer.write(jobj));
			// JSON・JSONP
			}else if(type.equals(Constant.RESPONSE_JSON)){
				XMLSerializer serializer = new XMLSerializer();
//				serializer.setForceTopLevelObject(true);
//				serializer.setSkipNamespaces(false);
//				serializer.setRootName(Constant.NODE_RESPONSE);
//				serializer.setElementName(Constant.NODE_DATA);
				JSON json = serializer.read(xmlDom.toString());
				// callbackパラメタが指定された場合はJSONPで返す
				if(callback != null && !callback.equals("")){
					response.setContentType(Constant.HD_RESPONSE_JSONP);
					writer.write(callback+"("+json+");");
				}else{
					response.setContentType(Constant.HD_RESPONSE_JSON);
					json.write(writer);
//					jobj.write(writer);
				}
			}else{
				writer.write("illigal responseType");
			}
		}catch(TransformerConfigurationException e){
			e.printStackTrace();
		}catch(TransformerException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			writer.flush();
			writer.close();
		}
	}
}
