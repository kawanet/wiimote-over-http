package org.mtl.wiimote.service;

public class Constant {

	/** �p�����[�^KEY */
	public static final String METHOD 		= "method";
	public static final String WIIMOTE 		= "wiimote";
	public static final String LIGHT 			= "light";
	public static final String TIME 			= "time";
	public static final String BUTTON 		= "button";
	public static final String RESPONSE_TYPE 	= "responseType";
	
	/** �p�����[�^(METHOD) VALUE */
	public static final String FIND_REMOTE 		= "findRemote";
	public static final String IS_CONNECTED 		= "isConnected";
	public static final String VIBRATE_FOR 		= "vibrateFor";
	public static final String IS_PRESSED 		= "isPressed";
	public static final String SET_LED_LIGHTS 	= "setLEDLights";
	public static final String POSITION_INFO 		= "PositionInfo";

	public static final String FIND_WIIMOTE 		= "findWiimote";
	public static final String RELEASE_WIIMOTE 	= "releaseWiimote";
	public static final String SET_VIBRATE 		= "setVibrate";
	public static final String SET_LED		 	= "setLED";
	public static final String GET_STATUS		 	= "getStatus";

	/** XML�X�e�[�^�X */
	public static final Integer STATUS_OK 				= 200;
	public static final Integer STATUS_NG 				= 500;
	public static final Integer STATUS_PARAM_NG 			= 400;
	public static final Integer STATUS_WIIMOTE_NOT_FOUND 	= 404;
	public static final Integer STATUS_METHOD_NOT_FOUND 	= 501;

	/** XML�m�[�h */
	public static final String NODE_RESPONSE 			= "response";
	public static final String NODE_STATUS 			= "status";
	public static final String NODE_METHOD 			= "method";
	public static final String NODE_DATA 				= "data";
	public static final String NODE_BOOL 				= "bool";
	public static final String NODE_IS_YAW_ALLOWED 	= "isYawAllowed";
	public static final String NODE_PITCH 			= "pitch";
	public static final String NODE_ROLL 				= "roll";
	public static final String NODE_X_POS 			= "xPos";
	public static final String NODE_Y_POS 			= "yPos";
	public static final String NODE_Z_POS 			= "zPos";
	public static final String NODE_BTN_ONE 			= "one";
	public static final String NODE_BTN_TWO			= "two";
	public static final String NODE_BTN_A 			= "a";
	public static final String NODE_BTN_B 			= "b";
	public static final String NODE_BTN_MINUS			= "minus";
	public static final String NODE_BTN_PLUS 			= "plus";
	public static final String NODE_BTN_HOME 			= "home";
	public static final String NODE_BTN_UP 			= "up";
	public static final String NODE_BTN_DOWN 			= "down";
	public static final String NODE_BTN_LEFT 			= "left";
	public static final String NODE_BTN_RIGHT			= "right";
	public static final String NODE_WIIMOTE 			= "wiimote";
	public static final String ATTR_INDEX 			= "index";

	/** ���X�|���X�^�C�v */
	public static final String RESPONSE_XML 	= "XML";
	public static final String RESPONSE_JSON 	= "JSON";
	
}
