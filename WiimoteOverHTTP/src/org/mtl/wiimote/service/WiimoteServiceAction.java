package org.mtl.wiimote.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
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
 * WiimoteOverHTTP�T�[�u���b�g�N���X
 * @author nemoto.hrs
 * @version 0.1
 */
public class WiimoteServiceAction extends HttpServlet{
	/** Default Serial Version */
	private static final long serialVersionUID = 1L;	
	
	/** Wii�����R���Ǘ��N���X*/
	private static WiimoteManager manager = null;
	/** XML�h�L�������g */
	private Document document = null;

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
		String resType 	= request.getParameter(Constant.RESPONSE_TYPE);

		Element responseNode = null;
		try {
			// response �m�[�h�쐬
			responseNode = this.createRootNode(Constant.NODE_RESPONSE);
			// method �m�[�h�쐬
			responseNode.appendChild(this.createNode(Constant.NODE_METHOD, method));

			// Wii�����R���ɑ΂��鏈���̎��s
			if(!GenericValidator.isBlankOrNull(method)){
				if(method.equals(Constant.FIND_WIIMOTE)){
					this.findWiimote(responseNode);
				}else if(method.equals(Constant.RELEASE_WIIMOTE)){
					this.releaseWiimote(responseNode, wiimote);
				}else if(method.equals(Constant.IS_CONNECTED)){
					this.isConnected(responseNode, wiimote);
				}else if(method.equals(Constant.GET_STATUS)){
					this.getStatus(responseNode, wiimote);
				}else if(method.equals(Constant.SET_VIBRATE)){
					this.setVibrate(responseNode, wiimote, time);
				}else if(method.equals(Constant.IS_PRESSED)){
					this.isPressed(responseNode, wiimote, button);
				}else if(method.equals(Constant.SET_LED)){
					this.setLED(responseNode, wiimote, light, time);
				}else{
					// status �m�[�h�ǉ�
					returnStatus = Constant.STATUS_METHOD_NOT_FOUND;
					responseNode.appendChild(this.createNode(Constant.NODE_STATUS, returnStatus.toString()));
				}
			}else{
				// status �m�[�h�ǉ�
				returnStatus = Constant.STATUS_METHOD_NOT_FOUND;
				responseNode.appendChild(this.createNode(Constant.NODE_STATUS, returnStatus.toString()));
			}
		}catch(Exception e){
			// status �m�[�h�ǉ�
			returnStatus = Constant.STATUS_NG;
			responseNode.appendChild(this.createNode(Constant.NODE_STATUS, returnStatus.toString()));
			e.printStackTrace();
		}finally{
			// ���X�|���X�̃��C�^�[���擾
			PrintWriter pw = response.getWriter();
			// ���ʂ��o��
			this.write(pw, resType);
			pw.flush();
			pw.close();
		}
	}
	
	/**
	 * Wii�����R���T���E�ڑ�
	 * @param rNode XML��_�m�[�h
	 */
	private void findWiimote(Element rNode){
		Integer st = 0;
		try{
			manager.findWiimote();
			st = Constant.STATUS_OK;
		}catch(Exception e){
			st = Constant.STATUS_NG;
		}
		// status �m�[�h�ǉ�
		rNode.appendChild(this.createNode(Constant.NODE_STATUS, st.toString()));
	}

	/**
	 * Wii�����R���ؒf
	 * @param rNode XML��_�m�[�h
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
		// status �m�[�h�ǉ�
		rNode.appendChild(this.createNode(Constant.NODE_STATUS, st.toString()));
	}

	/**
	 * Wii�����R���ڑ��󋵎擾
	 * @param rNode XML��_�m�[�h
	 * @param wiimote Wii�����R��No.
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
		// status �m�[�h�ǉ�
		rNode.appendChild(this.createNode(Constant.NODE_STATUS, st.toString()));
		// data �m�[�h�ǉ�
		Element data = this.createNode(Constant.NODE_DATA, null);
		data.appendChild(this.createNode(Constant.NODE_BOOL, (bol?"1":"0")));
		rNode.appendChild(data);
	}
	
	/**
	 * Wii�����R���ʒu���擾
	 * @param rNode XML��_�m�[�h
	 * @param wiimote Wii�����R��No.
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
		// status �m�[�h�ǉ�
		rNode.appendChild(this.createNode(Constant.NODE_STATUS, st.toString()));
		// data �m�[�h�ǉ�
		Element data = this.createNode(Constant.NODE_DATA, null);
		data.appendChild(this.createNode(Constant.NODE_X_POS, posInfo.get(Wiimote.POS_X).toString()));
		data.appendChild(this.createNode(Constant.NODE_Y_POS, posInfo.get(Wiimote.POS_Y).toString()));
		data.appendChild(this.createNode(Constant.NODE_Z_POS, posInfo.get(Wiimote.POS_Z).toString()));
		data.appendChild(this.createNode(Constant.NODE_PITCH, posInfo.get(Wiimote.POS_PITCH).toString()));
		data.appendChild(this.createNode(Constant.NODE_ROLL,  posInfo.get(Wiimote.POS_ROLL).toString()));
		rNode.appendChild(data);
	}

	/**
	 * Wii�����R�����擾
	 * @param rNode XML��_�m�[�h
	 * @param wiimote Wii�����R��No.
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
		// status �m�[�h�ǉ�
		rNode.appendChild(this.createNode(Constant.NODE_STATUS, st.toString()));
		// data �m�[�h�ǉ�
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

	/**
	 * Wii�����R���o�C�u���[�V��������
	 * @param rNode XML��_�m�[�h
	 * @param wiimote Wii�����R��No.
	 * @param time �o�C�u���[�V��������
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
		// status �m�[�h�ǉ�
		rNode.appendChild(this.createNode(Constant.NODE_STATUS, st.toString()));
	}
	
	/**
	 * Wii�����R���{�^�������󋵎擾
	 * @param wiimote Wii�����R��No.
	 * @param button �{�^����
	 * @return �X�e�[�^�X
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
		// status �m�[�h�ǉ�
		rNode.appendChild(this.createNode(Constant.NODE_STATUS, st.toString()));
		// data �m�[�h�ǉ�
		Element data = this.createNode(Constant.NODE_DATA, null);
		data.appendChild(this.createNode(Constant.NODE_BOOL, (bol?"1":"0")));
		rNode.appendChild(data);
	}
	
	/**
	 * Wii�����R��LED�_���E��������
	 * @param wiimote Wii�����R��No.
	 * @param light LED�_���p�^�[��
	 * @return �X�e�[�^�X
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
		// status �m�[�h�ǉ�
		rNode.appendChild(this.createNode(Constant.NODE_STATUS, st.toString()));
	}
	
	/**
	 * ��_�m�[�h���쐬
	 * @param docName ��_�m�[�h��
	 * @return �m�[�h�I�u�W�F�N�g
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
	 * �m�[�h���쐬
	 * @param nodeName �m�[�h��
	 * @param nodeValue �m�[�h�̒l
	 * @return �m�[�h�I�u�W�F�N�g
	 */
	private Element createNode(String nodeName, String nodeValue){
		return this.createNode(nodeName, null, null, nodeValue);
	}

	/**
	 * �m�[�h���쐬
	 * @param nodeName �m�[�h��
	 * @param nodeValue �m�[�h�̒l
	 * @return �m�[�h�I�u�W�F�N�g
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
	 * �o�͂��s��
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
