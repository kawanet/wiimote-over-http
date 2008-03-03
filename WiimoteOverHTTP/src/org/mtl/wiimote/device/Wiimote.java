package org.mtl.wiimote.device;

import java.io.IOException;
import java.util.HashMap;

import org.mtl.wiimote.exception.WiimoteNotConnectException;

import wiiremotej.WiiRemote;
import wiiremotej.WiiRemoteExtension;
import wiiremotej.WiiRemoteJ;
import wiiremotej.event.WRAccelerationEvent;
import wiiremotej.event.WRButtonEvent;
import wiiremotej.event.WRCombinedEvent;
import wiiremotej.event.WRExtensionEvent;
import wiiremotej.event.WRIREvent;
import wiiremotej.event.WRStatusEvent;
import wiiremotej.event.WiiRemoteListener;

/**
 * Wiiリモコンクラス
 * @author nemoto.hrs
 * @version 0.1
 */
public class Wiimote{
	/** Wiiリモート */
	private WiiRemote wiiremote = null;
	/** コントローラNo. */
	private int wiimoteNo = -1;
	/** デフォルトLED点灯パターン */
	private boolean[] defaultLEDPattern = null;
	/** ボタン押下状態保持MAP */
	private HashMap<Integer, Boolean> buttonMap = new HashMap<Integer, Boolean>();
	/** デフォルトLED点灯パターンリスト */
	private static final boolean[][] LED_PATTERN = {
		{true,	false,	false,	false},
		{false,	true,	false,	false},
		{false,	false,	true,	false},
		{false,	false,	false,	true},
		{true,	true,	true,	true}
	};
	/* WiiリモコンNo */
	public static final int NUM_ONE 		= 0;
	public static final int NUM_TWO 		= 1;
	public static final int NUM_THREE 	= 2;
	public static final int NUM_FOUR 		= 3;
	private static final int NUM_OTHER 	= 4;
	
	/* WiiリモコンKEY */
	public static final int KEY_A 		= WRButtonEvent.A;
	public static final int KEY_B 		= WRButtonEvent.B;
	public static final int KEY_ONE 		= WRButtonEvent.ONE;
	public static final int KEY_TWO 		= WRButtonEvent.TWO;
	public static final int KEY_PLUS 		= WRButtonEvent.PLUS;
	public static final int KEY_MINUS 	= WRButtonEvent.MINUS;
	public static final int KEY_HOME 		= WRButtonEvent.HOME;
	public static final int KEY_UP 		= WRButtonEvent.UP;
	public static final int KEY_DOWN 		= WRButtonEvent.DOWN;
	public static final int KEY_LEFT 		= WRButtonEvent.LEFT;
	public static final int KEY_RIGHT 	= WRButtonEvent.RIGHT;
	
	/**
	 * コンストラクタ
	 */
	public Wiimote(){
		new Wiimote(NUM_OTHER);
	}
	
	/**
	 * コンストラクタ
	 * @param wiimoteNo WiiリモコンNo.
	 */
	public Wiimote(int wiimoteNo){
		this.wiimoteNo = wiimoteNo;
		if(this.wiimoteNo <= NUM_ONE && this.wiimoteNo <= NUM_FOUR){
			this.defaultLEDPattern = LED_PATTERN[this.wiimoteNo];
		}else{
			this.defaultLEDPattern = LED_PATTERN[NUM_OTHER];
		}
		this.initButtonMap();
	}
	
	/**
	 * ボタン押下状態初期化
	 */
	private void initButtonMap(){
		buttonMap.put(KEY_A, 	 false);
		buttonMap.put(KEY_B, 	 false);
		buttonMap.put(KEY_ONE, 	 false);
		buttonMap.put(KEY_TWO, 	 false);
		buttonMap.put(KEY_PLUS,  false);
		buttonMap.put(KEY_MINUS, false);
		buttonMap.put(KEY_HOME,  false);
		buttonMap.put(KEY_UP, 	 false);
		buttonMap.put(KEY_DOWN,  false);
		buttonMap.put(KEY_LEFT,  false);
		buttonMap.put(KEY_RIGHT, false);
	}
	
	/**
	 * 周辺のWiiリモコンを探し、接続を行う
	 * @return 接続成功の場合はtrue
	 */
	public boolean connect(){
		try{
			// 既に接続されている場合は一旦切断する
			if(wiiremote != null && wiiremote.isConnected()){
				this.disconnect();
			}
			// Wiiリモコンを探す
			wiiremote = WiiRemoteJ.findRemote();
			if(wiiremote != null && wiiremote.isConnected()){
				System.out.println("=== CONNECT === wiimoteNo:"+this.wiimoteNo);
				// イベントのリスナを設定
				wiiremote.addWiiRemoteListener(new WiimoteListener());
				// LEDを点灯させる
				wiiremote.setLEDLights(this.defaultLEDPattern);
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return false;
	}

	/**
	 * Wiiリモコンを切断する
	 */
	public void disconnect(){
		if(wiiremote != null && wiiremote.isConnected()){
			this.initButtonMap();
			wiiremote.disconnect();
		}
	}
	
	/**
	 * Wiiリモコンの接続状況を返す
	 * @return 接続されている場合はtrue 未接続の場合はfalse
	 */
	public boolean isConnected(){
		if(wiiremote != null){
			return wiiremote.isConnected();
		}
		return false;
	}

	/**
	 * WiiリモコンのバイブレーションをONにする
	 * @throws WiimoteNotConnectException Wiiリモコンが未接続
	 * @throws IOException 
	 */
	public void vibrateFor(long milliSec) 
	throws IOException, WiimoteNotConnectException{
		if(wiiremote != null && wiiremote.isConnected()){
			wiiremote.vibrateFor(milliSec);
		}else{
			throw new WiimoteNotConnectException();
		}
	}

	/**
	 * WiiリモコンのLEDを指定されたパターンで点灯させる
	 * @param pattern 点灯パターン
	 * @throws WiimoteNotConnectException Wiiリモコンが未接続
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public void setLEDLights(boolean[] pattern) 
	throws IllegalArgumentException, IOException, WiimoteNotConnectException{
		if(wiiremote != null && wiiremote.isConnected()){
			wiiremote.setLEDLights(pattern);
		}else{
			throw new WiimoteNotConnectException();
		}		
	}

	/**
	 * Wiiリモコンのボタンが押下されたか判断する
	 * @param key ボタン
	 * @return 押されていた場合はtrue
	 * @throws WiimoteNotConnectException Wiiリモコンが未接続
	 */
	public boolean isPressed(int key) throws WiimoteNotConnectException{ 
		if(wiiremote != null && wiiremote.isConnected()){
			return buttonMap.get(key);
		}else{
			throw new WiimoteNotConnectException();
		}		
	}

	/**
	 * WiiリモコンNo.を返す
	 * @return WiiリモコンNo.
	 */
	public int getWiimoteNo(){
		return wiimoteNo;
	}

	/**
	 * WiiリモコンNo.を設定する
	 * @param wiimoteNo WiiリモコンNo.
	 */
	public void setWiimoteNo(int wiimoteNo){
		this.wiimoteNo = wiimoteNo;
	}

	/**
	 * LEDのデフォルト点灯パターンを返す
	 * @return LEDのデフォルト点灯パターン
	 */
	public boolean[] getDefaultLEDPattern() {
		return defaultLEDPattern;
	}

	/**
	 * LEDのデフォルト点灯パターンを設定する
	 * @param defaultLEDPattern LEDのデフォルト点灯パターン(true:ON false:OFF)
	 */
	public void setDefaultLEDPattern(boolean[] defaultLEDPattern) {
		this.defaultLEDPattern = defaultLEDPattern;
	}
	
	// Inner Class ==============================
	/**
	 * Wiiリモコンイベントリスナ
	 * @author nemoto.hrs
	 * @version 0.1
	 */
	private class WiimoteListener implements WiiRemoteListener{
		
		public void accelerationInputReceived(WRAccelerationEvent arg0) {
			System.out.println("X    :"+arg0.getXAcceleration());
			System.out.println("Y    :"+arg0.getYAcceleration());
			System.out.println("Z    :"+arg0.getZAcceleration());
			System.out.println("Pitch:"+arg0.getPitch());
			System.out.println("Roll :"+arg0.getRoll()+"\n");
		}

		public void buttonInputReceived(WRButtonEvent arg0) {
			if(arg0.wasPressed(WRButtonEvent.A))		buttonMap.put(KEY_A, 	 true);
			if(arg0.wasPressed(WRButtonEvent.B))		buttonMap.put(KEY_B, 	 true);
			if(arg0.wasPressed(WRButtonEvent.ONE))		buttonMap.put(KEY_ONE, 	 true);
			if(arg0.wasPressed(WRButtonEvent.TWO))		buttonMap.put(KEY_TWO, 	 true);
			if(arg0.wasPressed(WRButtonEvent.MINUS))	buttonMap.put(KEY_MINUS, true);
			if(arg0.wasPressed(WRButtonEvent.PLUS))		buttonMap.put(KEY_PLUS,  true);
			if(arg0.wasPressed(WRButtonEvent.HOME))		buttonMap.put(KEY_HOME,  true);
			if(arg0.wasPressed(WRButtonEvent.UP))		buttonMap.put(KEY_UP, 	 true);
			if(arg0.wasPressed(WRButtonEvent.DOWN))		buttonMap.put(KEY_DOWN,  true);
			if(arg0.wasPressed(WRButtonEvent.LEFT))		buttonMap.put(KEY_LEFT,  true);
			if(arg0.wasPressed(WRButtonEvent.RIGHT))	buttonMap.put(KEY_RIGHT, true);
						
			if(arg0.wasReleased(WRButtonEvent.A))		buttonMap.put(KEY_A, 	 false);
			if(arg0.wasReleased(WRButtonEvent.B))		buttonMap.put(KEY_B, 	 false);
			if(arg0.wasReleased(WRButtonEvent.ONE))		buttonMap.put(KEY_ONE, 	 false);
			if(arg0.wasReleased(WRButtonEvent.TWO))		buttonMap.put(KEY_TWO, 	 false);
			if(arg0.wasReleased(WRButtonEvent.MINUS))	buttonMap.put(KEY_MINUS, false);
			if(arg0.wasReleased(WRButtonEvent.PLUS))	buttonMap.put(KEY_PLUS,  false);
			if(arg0.wasReleased(WRButtonEvent.HOME))	buttonMap.put(KEY_HOME,  false);
			if(arg0.wasReleased(WRButtonEvent.UP))		buttonMap.put(KEY_UP, 	 false);
			if(arg0.wasReleased(WRButtonEvent.DOWN))	buttonMap.put(KEY_DOWN,  false);
			if(arg0.wasReleased(WRButtonEvent.LEFT))	buttonMap.put(KEY_LEFT,  false);
			if(arg0.wasReleased(WRButtonEvent.RIGHT))	buttonMap.put(KEY_RIGHT, false);
		}

		public void IRInputReceived(WRIREvent arg0) {
			// TODO 自動生成されたメソッド・スタブ		
		}

		public void combinedInputReceived(WRCombinedEvent arg1) {
			// TODO 自動生成されたメソッド・スタブ
		}

		public void disconnected() {
			System.out.println("=== DISCONNECT === wiimoteNo:"+wiimoteNo);
		}

		public void extensionConnected(WiiRemoteExtension arg0) {
			// TODO 自動生成されたメソッド・スタブ
		}

		public void extensionDisconnected(WiiRemoteExtension arg0) {
			// TODO 自動生成されたメソッド・スタブ
		}

		public void extensionInputReceived(WRExtensionEvent arg0) {
			// TODO 自動生成されたメソッド・スタブ
		}

		public void extensionPartiallyInserted() {
			// TODO 自動生成されたメソッド・スタブ
		}

		public void extensionUnknown() {
			// TODO 自動生成されたメソッド・スタブ	
		}

		public void statusReported(WRStatusEvent arg0) {
			// TODO 自動生成されたメソッド・スタブ	
		}

	}
}
