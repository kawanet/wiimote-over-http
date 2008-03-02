package org.mtl.wiimote.service;

public class Constant {

	/** パラメータKEY */
	public static final String METHOD 		= "method";
	public static final String WIIMOTE 		= "wiimote";
	public static final String LIGHT 			= "light";
	public static final String TIME 			= "time";
	public static final String BUTTON 		= "button";
	public static final String RESPONSE_TYPE 	= "responseType";
	
	/** パラメータ(METHOD) VALUE */
	public static final String FIND_REMOTE 		= "findRemote";
	public static final String IS_CONNECTED 		= "isConnected";
	public static final String VIBRATE_FOR 		= "vibrateFor";
	public static final String IS_PRESSED 		= "isPressed";
	public static final String SET_LED_LIGHTS 	= "setLEDLights";
	public static final String POSITION_INFO 		= "PositionInfo";

	/** XMLステータス */
	public static final Integer STATUS_OK 				= 200;
	public static final Integer STATUS_NG 				= 500;
	public static final Integer STATUS_PARAM_NG 			= 400;
	public static final Integer STATUS_WIIMOTE_NOT_FOUND 	= 404;
	public static final Integer STATUS_METHOD_NOT_FOUND 	= 501;

	/** XMLノード */
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
	
	/** レスポンスタイプ */
	public static final String RESPONSE_XML 	= "XML";
	public static final String RESPONSE_JSON 	= "JSON";
	
}
