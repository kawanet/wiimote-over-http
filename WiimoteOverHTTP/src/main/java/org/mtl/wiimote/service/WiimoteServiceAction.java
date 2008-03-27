package org.mtl.wiimote.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

import org.apache.commons.validator.GenericValidator;
import org.mtl.wiimote.device.Wiimote;
import org.mtl.wiimote.device.WiimoteManager;
import org.mtl.wiimote.exception.WiimoteNotConnectException;
import org.mtl.wiimote.exception.WiimoteNotFoundException;

/**
 * WiimoteOverHTTP�T�[�u���b�g�N���X
 * @author nemoto.hrs
 * @version 2008/03/27
 */
public class WiimoteServiceAction extends HttpServlet{
	/** Default Serial Version */
	private static final long serialVersionUID = 1L;	
	
	/** Wii�����R���Ǘ��N���X*/
	private static WiimoteManager manager = null;
	/** XML Serializer */
	private static XMLSerializer serializer = new XMLSerializer();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		// XML�X�e�[�^�X������
		Integer returnStatus = Constant.STATUS_OK;
		// XML Serializer������
		serializer.setRootName(Constant.NODE_RESPONSE);
		serializer.setTypeHintsEnabled(false);

		
		// Wii�����R���Ǘ��N���X��������
		if(manager == null){
			manager = new WiimoteManager();
		}
		
		// ���N�G�X�g�p�����^�擾�E���s
		String method 	= request.getParameter(Constant.METHOD);
		String wiimote 	= request.getParameter(Constant.WIIMOTE);
		String time 	= request.getParameter(Constant.TIME);
		String button	= request.getParameter(Constant.BUTTON);
		String light	= request.getParameter(Constant.LIGHT);
		String resType 	= request.getParameter(Constant.RESPONSE_TYPE);
		String callback = request.getParameter(Constant.CALLBACK);

		JSONObject jResponse = null;
		try {
			// response �f�[�^�쐬
			jResponse = new JSONObject();
			// method �m�[�h�쐬
			jResponse.put(Constant.NODE_METHOD, method);
			// status �m�[�h�쐬
			jResponse.put(Constant.NODE_STATUS, returnStatus.toString());
			
			// Wii�����R���ɑ΂��鏈���̎��s
			if(!GenericValidator.isBlankOrNull(method)){
				if(method.equals(Constant.FIND_WIIMOTE)){
					this.findWiimote(jResponse);
				}else if(method.equals(Constant.RELEASE_WIIMOTE)){
					this.releaseWiimote(jResponse, wiimote);
				}else if(method.equals(Constant.IS_CONNECTED)){
					this.isConnected(jResponse, wiimote);
				}else if(method.equals(Constant.POSITION_INFO)){
					this.getPositionInfo(jResponse, wiimote);
				}else if(method.equals(Constant.GET_STATUS)){
					this.getStatus(jResponse, wiimote);
				}else if(method.equals(Constant.SET_VIBRATE)){
					this.setVibrate(jResponse, wiimote, time);
				}else if(method.equals(Constant.IS_PRESSED)){
					this.isPressed(jResponse, wiimote, button);
				}else if(method.equals(Constant.SET_LED)){
					this.setLED(jResponse, wiimote, light, time);
				}else if(method.equals(Constant.GET_INFO)){
					this.getInfo(jResponse, wiimote);
				}else{
					// status �m�[�h�ǉ�
					returnStatus = Constant.STATUS_METHOD_NOT_FOUND;
					jResponse.put(Constant.NODE_STATUS, returnStatus.toString());
				}
			}else{
				// status �m�[�h�ǉ�
				returnStatus = Constant.STATUS_METHOD_NOT_FOUND;
				jResponse.put(Constant.NODE_STATUS, returnStatus.toString());
			}
		}catch(Exception e){
			// status �m�[�h�ǉ�
			returnStatus = Constant.STATUS_NG;
			jResponse.put(Constant.NODE_STATUS, returnStatus.toString());
			e.printStackTrace();
		}finally{
			// ���ʂ��o��
			this.writeResponse(response, resType, jResponse, callback);
		}
	}
	
	/**
	 * Wii�����R���T���E�ڑ�
	 * @param rNode XML��_�m�[�h
	 */
	private void findWiimote(JSONObject rNode){
		Integer st = 0;
		try{
			manager.findWiimote();
			st = Constant.STATUS_OK;
		}catch(Exception e){
			st = Constant.STATUS_NG;
		}
		// status �m�[�h�ǉ�
		rNode.put(Constant.NODE_STATUS, st.toString());
	}

	/**
	 * Wii�����R���ؒf
	 * @param rNode XML��_�m�[�h
	 */
	private void releaseWiimote(JSONObject rNode, String wiimote){
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
		// status �m�[�h�ǉ�
		rNode.put(Constant.NODE_STATUS, st.toString());
	}

	/**
	 * Wii�����R���ڑ��󋵎擾
	 * @param rNode XML��_�m�[�h
	 * @param wiimote Wii�����R��No.
	 */
	private void isConnected(JSONObject rNode, String wiimote){
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
		// status �m�[�h�ǉ�
		rNode.put(Constant.NODE_STATUS, st.toString());
		// data �m�[�h�ǉ�
		rNode.put(Constant.NODE_DATA, (bol?"1":"0"));
	}
	
	/**
	 * Wii�����R���ʒu���擾
	 * @param rNode XML��_�m�[�h
	 * @param wiimote Wii�����R��No.
	 */
	private void getPositionInfo(JSONObject rNode, String wiimote){
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
		// status �m�[�h�ǉ�
		rNode.put(Constant.NODE_STATUS, st.toString());
		// data �m�[�h�ǉ�
		JSONObject data = new JSONObject();
		data.put(Constant.NODE_X_POS, posInfo.get(Wiimote.POS_X).toString());
		data.put(Constant.NODE_Y_POS, posInfo.get(Wiimote.POS_Y).toString());
		data.put(Constant.NODE_Z_POS, posInfo.get(Wiimote.POS_Z).toString());
		data.put(Constant.NODE_PITCH, posInfo.get(Wiimote.POS_PITCH).toString());
		data.put(Constant.NODE_ROLL,  posInfo.get(Wiimote.POS_ROLL).toString());
		rNode.put(Constant.NODE_DATA, data);
	}

	/**
	 * Wii�����R��������擾
	 * @param rNode ��_�m�[�h
	 * @param wiimote Wii�����R��No.
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
		// status �m�[�h�ǉ�
		rNode.put(Constant.NODE_STATUS, st.toString());
		if(allInfo != null){
			// data �m�[�h�ǉ�
			JSONArray wiimArr = new JSONArray();
			// XML Serializer �Ƀ��X�g�^�O���ǉ�
			serializer.setElementName(Constant.NODE_WIIMOTE);
			Iterator itr = allInfo.keySet().iterator();
			while(itr.hasNext()){
				Integer idx = (Integer)itr.next();
				Map info = (Map)allInfo.get(idx);
				JSONObject wiimVal = new JSONObject();
				JSONObject nchkVal = new JSONObject();
				wiimVal.put("@"+Constant.ATTR_INDEX, idx.toString());
				wiimVal.put(Constant.NODE_X_POS, 	info.get(Wiimote.POS_X).toString());
				wiimVal.put(Constant.NODE_Y_POS, 	info.get(Wiimote.POS_Y).toString());
				wiimVal.put(Constant.NODE_Z_POS, 	info.get(Wiimote.POS_Z).toString());
				wiimVal.put(Constant.NODE_PITCH, 	info.get(Wiimote.POS_PITCH).toString());
				wiimVal.put(Constant.NODE_ROLL,  	info.get(Wiimote.POS_ROLL).toString());
				wiimVal.put(Constant.NODE_BTN_A, 	(Boolean)info.get(Wiimote.KEY_A)?"1":"0");
				wiimVal.put(Constant.NODE_BTN_B, 	(Boolean)info.get(Wiimote.KEY_B)?"1":"0");
				wiimVal.put(Constant.NODE_BTN_ONE, 	(Boolean)info.get(Wiimote.KEY_ONE)?"1":"0");
				wiimVal.put(Constant.NODE_BTN_TWO, 	(Boolean)info.get(Wiimote.KEY_TWO)?"1":"0");
				wiimVal.put(Constant.NODE_BTN_MINUS,(Boolean)info.get(Wiimote.KEY_MINUS)?"1":"0");
				wiimVal.put(Constant.NODE_BTN_PLUS, (Boolean)info.get(Wiimote.KEY_PLUS)?"1":"0");
				wiimVal.put(Constant.NODE_BTN_HOME, (Boolean)info.get(Wiimote.KEY_HOME)?"1":"0");
				wiimVal.put(Constant.NODE_BTN_UP, 	(Boolean)info.get(Wiimote.KEY_UP)?"1":"0");
				wiimVal.put(Constant.NODE_BTN_DOWN, (Boolean)info.get(Wiimote.KEY_DOWN)?"1":"0");
				wiimVal.put(Constant.NODE_BTN_LEFT, (Boolean)info.get(Wiimote.KEY_LEFT)?"1":"0");
				wiimVal.put(Constant.NODE_BTN_RIGHT,(Boolean)info.get(Wiimote.KEY_RIGHT)?"1":"0");
				// �k���`���N���
				nchkVal.put(Constant.NODE_X_POS, info.get(Wiimote.NPOS_X).toString());
				nchkVal.put(Constant.NODE_Y_POS, info.get(Wiimote.NPOS_Y).toString());
				nchkVal.put(Constant.NODE_Z_POS, info.get(Wiimote.NPOS_Z).toString());
				nchkVal.put(Constant.NODE_PITCH, info.get(Wiimote.NPOS_PITCH).toString());
				nchkVal.put(Constant.NODE_ROLL,  info.get(Wiimote.NPOS_ROLL).toString());
				nchkVal.put(Constant.NODE_BTN_C, (Boolean)info.get(Wiimote.KEY_C)?"1":"0");
				nchkVal.put(Constant.NODE_BTN_Z, (Boolean)info.get(Wiimote.KEY_Z)?"1":"0");
				nchkVal.put(Constant.NODE_X_VEC, info.get(Wiimote.ALG_X).toString());
				nchkVal.put(Constant.NODE_Y_VEC, info.get(Wiimote.ALG_Y).toString());
				wiimVal.put(Constant.NODE_NUNCHUK, nchkVal);
				wiimArr.add(wiimVal);
			}
			rNode.put(Constant.NODE_DATA, wiimArr);
		}
	}
	
	/**
	 * Wii�����R���o�C�u���[�V��������
	 * @param rNode XML��_�m�[�h
	 * @param wiimote Wii�����R��No.
	 * @param time �o�C�u���[�V��������
	 */
	private void setVibrate(JSONObject rNode, String wiimote, String time){
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
		// status �m�[�h�ǉ�
		rNode.put(Constant.NODE_STATUS, st.toString());
	}
	
	/**
	 * Wii�����R���{�^�������󋵎擾
	 * @param wiimote Wii�����R��No.
	 * @param button �{�^����
	 * @return �X�e�[�^�X
	 */
	private void isPressed(JSONObject rNode, String wiimote, String button){
		Integer st = 0;
		boolean bol = false;
		try{
			String paramVal = Constant.HOME+"|"+Constant.MINUS+"|"+Constant.PLUS+"|"+Constant.A+"|"+Constant.B+"|"+Constant.ONE+"|"+
					Constant.TWO+"|"+Constant.UP+"|"+Constant.DOWN+"|"+Constant.LEFT+"|"+Constant.RIGHT+"|"+Constant.C+"|"+Constant.Z;
		if(wiimote != null && GenericValidator.isInt(wiimote) && 
					button != null && button.matches(paramVal)){
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
		// status �m�[�h�ǉ�
		rNode.put(Constant.NODE_STATUS, st.toString());
		// data �m�[�h�ǉ�
		JSONObject data = new JSONObject();
		data.put(Constant.NODE_BOOL, (bol?"1":"0"));
		rNode.put(Constant.NODE_DATA, data);
	}
	
	/**
	 * Wii�����R��LED�_���E��������
	 * @param wiimote Wii�����R��No.
	 * @param light LED�_���p�^�[��
	 * @return �X�e�[�^�X
	 */
	private void setLED(JSONObject rNode, String wiimote, String light, String time){
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
		// status �m�[�h�ǉ�
		rNode.put(Constant.NODE_STATUS, st.toString());
	}

	/**
	 * Wii�����R�����擾
	 * @param rNode ��_�m�[�h
	 * @param wiimote Wii�����R��No.
	 */
	private void getInfo(JSONObject rNode, String wiimote){
		Integer st = 0;
		Map allInfo = null;
		try {
			if(wiimote != null && GenericValidator.isInt(wiimote)){
				allInfo = manager.getInfo(Integer.parseInt(wiimote));
			}else{
				allInfo = manager.getAllInfo();
			}
			st = Constant.STATUS_OK;

		}catch(WiimoteNotFoundException e){
			st = Constant.STATUS_WIIMOTE_NOT_FOUND;
		}catch(WiimoteNotConnectException e){
			st = Constant.STATUS_WIIMOTE_NOT_FOUND;
		}catch(Exception e){
			st = Constant.STATUS_NG;
		}
		// status �m�[�h�ǉ�
		rNode.put(Constant.NODE_STATUS, st.toString());
		if(allInfo != null){
			// data �m�[�h�ǉ�
			JSONArray wiimArr = new JSONArray();
			// XML Serializer �Ƀ��X�g�^�O���ǉ�
			serializer.setElementName(Constant.NODE_INFO);
			Iterator itr = allInfo.keySet().iterator();
			while(itr.hasNext()){
				Integer idx = (Integer)itr.next();
				Map info = (Map)allInfo.get(idx);
				JSONObject infoVal = new JSONObject();
				infoVal.put("@"+Constant.ATTR_WIIMOTE, idx.toString());
				infoVal.put(Constant.NODE_BATTERY, 	info.get(Constant.NODE_BATTERY));
				infoVal.put(Constant.NODE_CLASSIC, 	info.get(Constant.NODE_CLASSIC));
				infoVal.put(Constant.NODE_NUNCHUK, 	info.get(Constant.NODE_NUNCHUK));
				wiimArr.add(infoVal);
			}
			rNode.put(Constant.NODE_DATA, wiimArr);
		}
	}
	
	/**
	 * �o�͂��s��
	 * @param writer 
	 * @param type
	 */
	private void writeResponse(HttpServletResponse response, String type, JSONObject jobj, String callback){
		PrintWriter writer = null;
		try{
			// ���X�|���X�̃��C�^�[���擾
			response.setCharacterEncoding(Constant.UTF8);
			writer = response.getWriter();

			// XML
			if(type == null || type.equals(Constant.RESPONSE_XML)){
				response.setContentType(Constant.HD_RESPONSE_XML);
				writer.write(serializer.write(jobj));
			// JSON�EJSONP
			}else if(type.equals(Constant.RESPONSE_JSON)){
				JSONObject robj = new JSONObject();
				robj.put(Constant.NODE_RESPONSE, jobj);
				String json = robj.toString().replace("[", "{\""+serializer.getElementName()+"\":[").replace("]", "]}");
				// callback�p�����^���w�肳�ꂽ�ꍇ��JSONP�ŕԂ�
				if(callback != null && !callback.equals("")){
					response.setContentType(Constant.HD_RESPONSE_JSONP);
					writer.write(callback+"("+json+");");
				}else{
					response.setContentType(Constant.HD_RESPONSE_JSON);
					writer.write(json);
				}
			}else{
				writer.write("illigal responseType");
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			writer.flush();
			writer.close();
		}
	}
}
