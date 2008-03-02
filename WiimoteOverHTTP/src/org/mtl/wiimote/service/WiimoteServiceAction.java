package org.mtl.wiimote.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.validator.GenericValidator;
import org.mtl.wiimote.device.WiimoteManager;
import org.mtl.wiimote.exception.WiimoteNotConnectException;
import org.mtl.wiimote.exception.WiimoteNotFoundException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * WiimoteOverHTTP�T�[�u���b�g�N���X
 * @author nemoto.hrs
 * @version 0.1
 */
public class WiimoteServiceAction extends HttpServlet{
	/** Default Serial Version */
	private static final long serialVersionUID = 1L;	
	
	/** Wii�����R���Ǘ��N���X*/
	private static WiimoteManager manager = null;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		// XML�X�e�[�^�X������
		Integer returnStatus = Constant.STATUS_OK;
		
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

		try {
			if(!GenericValidator.isBlankOrNull(method)){
				if(method.equals(Constant.FIND_REMOTE)){
					returnStatus = this.findRemote();
				}else if(method.equals(Constant.IS_CONNECTED)){
					returnStatus = this.isConnected(wiimote);
				}else if(method.equals(Constant.POSITION_INFO)){
					returnStatus = this.getPositionInfo(wiimote);
				}else if(method.equals(Constant.VIBRATE_FOR)){
					returnStatus = this.vibrateFor(wiimote, time);
				}else if(method.equals(Constant.IS_PRESSED)){
					returnStatus = this.isPressed(wiimote, button);
				}else if(method.equals(Constant.SET_LED_LIGHTS)){
					returnStatus = this.setLEDLights(wiimote, light);
				}else{
					returnStatus = Constant.STATUS_METHOD_NOT_FOUND;
				}
			}else{
				returnStatus = Constant.STATUS_METHOD_NOT_FOUND;
			}
			response.sendRedirect("./");
		}finally{
			// ���X�|���X�̃��C�^�[���擾
			PrintWriter pw = response.getWriter();
			pw.flush();
			pw.close();
		}
	}
	
	/**
	 * Wii�����R���T��
	 * @return �X�e�[�^�X
	 */
	private int findRemote(){
		try{
			manager.findWiimote();
		}catch(Exception e){
			return Constant.STATUS_NG;
		}
		return Constant.STATUS_OK;
	}

	/**
	 * Wii�����R���ڑ��󋵎擾
	 * @param wiimote Wii�����R��No.
	 * @return �X�e�[�^�X
	 */
	private int isConnected(String wiimote){
		try{
			if(wiimote != null && GenericValidator.isInt(wiimote)){
				manager.isConnected(Integer.parseInt(wiimote));
				return Constant.STATUS_OK;
			}
		}catch(WiimoteNotFoundException e){
			return Constant.STATUS_WIIMOTE_NOT_FOUND;
		}catch(Exception e){
			return Constant.STATUS_NG;
		}
		return Constant.STATUS_PARAM_NG;
	}
	
	/**
	 * Wii�����R���ʒu���擾
	 * @param wiimote Wii�����R��No.
	 * @return �X�e�[�^�X
	 */
	private int getPositionInfo(String wiimote){
		try {
			if(wiimote != null && GenericValidator.isInt(wiimote)){
				manager.getPositionInfo(Integer.parseInt(wiimote));
				return Constant.STATUS_OK;
			}
		}catch(WiimoteNotFoundException e){
			return Constant.STATUS_WIIMOTE_NOT_FOUND;
		}catch(WiimoteNotConnectException e){
			return Constant.STATUS_WIIMOTE_NOT_FOUND;
		}catch(Exception e){
			return Constant.STATUS_NG;
		}
		return Constant.STATUS_PARAM_NG;
	}
	
	/**
	 * Wii�����R���o�C�u���[�V��������
	 * @param wiimote Wii�����R��No.
	 * @param time �o�C�u���[�V��������
	 * @return �X�e�[�^�X
	 */
	private int vibrateFor(String wiimote, String time){
		try {
			if(wiimote != null && GenericValidator.isInt(wiimote) && 
					time != null && GenericValidator.isLong(time)){
				manager.vibrateFor(Integer.parseInt(wiimote), Long.parseLong(time));
				return Constant.STATUS_OK;
			}
		}catch(WiimoteNotFoundException e){
			return Constant.STATUS_WIIMOTE_NOT_FOUND;
		}catch(WiimoteNotConnectException e){
			return Constant.STATUS_WIIMOTE_NOT_FOUND;
		}catch(Exception e){
			return Constant.STATUS_NG;
		}
		return Constant.STATUS_PARAM_NG;
	}
	
	/**
	 * Wii�����R���{�^�������󋵎擾
	 * @param wiimote Wii�����R��No.
	 * @param button �{�^����
	 * @return �X�e�[�^�X
	 */
	private int isPressed(String wiimote, String button){
		try{
			if(wiimote != null && GenericValidator.isInt(wiimote) && 
					button != null && button.matches("HOME|MINUS|PLUS|A|B|ONE|TWO|UP|DOWN|LEFT|RIGHT")){
				manager.isPressed(Integer.parseInt(wiimote), button);
				return Constant.STATUS_OK;
			}
		}catch(WiimoteNotFoundException e){
			return Constant.STATUS_WIIMOTE_NOT_FOUND;
		}catch(WiimoteNotConnectException e){
			return Constant.STATUS_WIIMOTE_NOT_FOUND;
		}catch(Exception e){
			return Constant.STATUS_NG;
		}
		return Constant.STATUS_PARAM_NG;
	}
	
	/**
	 * Wii�����R��LED�_���E��������
	 * @param wiimote Wii�����R��No.
	 * @param light LED�_���p�^�[��
	 * @return �X�e�[�^�X
	 */
	private int setLEDLights(String wiimote, String light){
		try{
			if(wiimote != null && GenericValidator.isInt(wiimote) && 
					light != null && light.matches("^[01],[01],[01],[01]$")){
				String[] sptn = light.split(",");
				boolean[] pattern = {
						(sptn[0].equals("1")?true:false), (sptn[1].equals("1")?true:false),
						(sptn[2].equals("1")?true:false), (sptn[3].equals("1")?true:false),};
				manager.setLEDLights(Integer.parseInt(wiimote), pattern);
				return Constant.STATUS_OK;
			}
		}catch(WiimoteNotFoundException e){
			return Constant.STATUS_WIIMOTE_NOT_FOUND;
		}catch(WiimoteNotConnectException e){
			return Constant.STATUS_WIIMOTE_NOT_FOUND;
		}catch(Exception e){
			return Constant.STATUS_NG;
		}
		return Constant.STATUS_PARAM_NG;
	}
	
	private void createXML(){
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
	
			DOMImplementation domImpl = builder.getDOMImplementation();
			Document document = domImpl.createDocument("","response",null);
			Element response = document.getDocumentElement();
			
			// status �m�[�h�쐬
			Element status = document.createElement("status");
			status.appendChild(document.createTextNode("---"));
			response.appendChild(status);
			
			// method �m�[�h�쐬
			Element method = document.createElement("method");
			status.appendChild(document.createTextNode("---"));
			response.appendChild(method);
			
			

	
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transformer = transFactory.newTransformer();
	
			DOMSource source = new DOMSource(document);
			File newXML = new File("newXML.xml");
			FileOutputStream os = new FileOutputStream(newXML);
			StreamResult result = new StreamResult(os);
			transformer.transform(source, result);
		}catch(Exception e){
			
		}
	
	}
}
