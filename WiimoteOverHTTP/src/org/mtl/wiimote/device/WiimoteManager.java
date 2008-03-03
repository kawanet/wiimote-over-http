package org.mtl.wiimote.device;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.mtl.wiimote.exception.WiimoteNotConnectException;
import org.mtl.wiimote.exception.WiimoteNotFoundException;

import wiiremotej.WiiRemoteJ;

/**
 * Wiiリモコン管理クラス
 * @author nemoto.hrs
 * @version 0.1
 */
public class WiimoteManager {

	/** Wiiリモコン最大接続数 */
	private static final int maxStock = 8;
	/** デフォルトLED点灯パターンリスト */
	private final static boolean[][] LED_PATTERN = {
		{true,	false,	false,	false},
		{false,	true,	false,	false},
		{false,	false,	true,	false},
		{false,	false,	false,	true},
		{true,	true,	false,	false},
		{true,	false,	true,	false},
		{true,	false,	false,	true},
		{false,	true,	true,	false},
	};

	/** Wiiリモコン保持Map */
	private static HashMap<Integer, Wiimote> wiimoteMap = new HashMap<Integer, Wiimote>();
	/** WiiリモコンKEY Map */
	private static HashMap<String, Integer> keyMap = new HashMap<String, Integer>();
	
	/**
	 * コンストラクタ
	 */
	public WiimoteManager(){
		// WiiRemoteJクラスのログのコンソール表示ON
		WiiRemoteJ.setConsoleLoggingAll();
		// Wiiリモコンのインスタンスをマッピング
		for(int i = 0; i < maxStock; i++){
			Wiimote wiim = new Wiimote(i);
			wiim.setDefaultLEDPattern(LED_PATTERN[i]);
			wiimoteMap.put(i, wiim);
		}
		// KEYの値をマッピング
		keyMap.put("A", 	Wiimote.KEY_A);
		keyMap.put("B", 	Wiimote.KEY_B);
		keyMap.put("ONE", 	Wiimote.KEY_ONE);
		keyMap.put("TWO", 	Wiimote.KEY_TWO);
		keyMap.put("PLUS", 	Wiimote.KEY_PLUS);
		keyMap.put("MINUS", Wiimote.KEY_MINUS);
		keyMap.put("HOME", 	Wiimote.KEY_HOME);
		keyMap.put("UP", 	Wiimote.KEY_UP);
		keyMap.put("DOWN", 	Wiimote.KEY_DOWN);
		keyMap.put("LEFT", 	Wiimote.KEY_LEFT);
		keyMap.put("RIGHT", Wiimote.KEY_RIGHT);
	}
	
	/**
	 * 周辺のWiiリモコンを探し、接続を行う
	 * @return 接続成功の場合はtrue　接続失敗、未検出の場合はfalse
	 */
	public boolean findWiimote(){
		Wiimote wiim = null;
		if((wiim = this.getWiimote()) != null){
			// Wiiリモコンを探す＆接続
			return wiim.connect();
		}
		return false;
	}

	/**
	 * 指定されたWiiリモコンの接続状況を返す
	 * @param wiimoteNo WiiリモコンNo.
	 * @return 接続されている場合はtrue 未接続の場合はfalse
	 * @throws WiimoteNotFoundException Wiiリモコンが存在しない(不正なWiiリモコンNo.)
	 */
	public boolean isConnected(int wiimoteNo) throws WiimoteNotFoundException{
		if(!wiimoteMap.containsKey(wiimoteNo)){
			throw new WiimoteNotFoundException();
		}
		Wiimote wiim = wiimoteMap.get(wiimoteNo);
		return wiim.isConnected();
	}

	/**
	 * 指定されたWiiリモコンのバイブレーションをONにする
	 * @param wiimoteNo WiiリモコンNo.
	 * @throws IOException 
	 * @throws WiimoteNotFoundException Wiiリモコンが存在しない(不正なWiiリモコンNo.)
	 * @throws WiimoteNotConnectException Wiiリモコンが未接続
	 */
	public void vibrateFor(int wiimoteNo, long milliSec) 
	throws IOException, WiimoteNotFoundException, WiimoteNotConnectException{
		if(!wiimoteMap.containsKey(wiimoteNo)){
			throw new WiimoteNotFoundException();
		}
		Wiimote wiim = wiimoteMap.get(wiimoteNo);
		wiim.vibrateFor(milliSec);
	}

	/**
	 * 指定されたWiiリモコンのボタンが押下されたか判断する
	 * @param wiimoteNo WiiリモコンNo.
	 * @param keyName ボタン名
	 * @return 押されていた場合はtrue
	 * @throws WiimoteNotFoundException Wiiリモコンが存在しない(不正なWiiリモコンNo.)
	 * @throws WiimoteNotConnectException Wiiリモコンが未接続
	 */
	public boolean isPressed(int wiimoteNo, String keyName) 
	throws WiimoteNotFoundException, WiimoteNotConnectException{
		if(!wiimoteMap.containsKey(wiimoteNo)){
			throw new WiimoteNotFoundException();
		}
		Wiimote wiim = wiimoteMap.get(wiimoteNo);
		Integer pressKey = keyMap.get(keyName);
		if(pressKey != null){
			return wiim.isPressed(pressKey.intValue());
		}
		return false;
	}

	/**
	 * 指定されたWiiリモコンのLEDを指定されたパターンで点灯させる
	 * @param wiimoteNo WiiリモコンNo.
	 * @param pattern 点灯パターン
	 * @throws WiimoteNotFoundException Wiiリモコンが存在しない(不正なWiiリモコンNo.)
	 * @throws WiimoteNotConnectException Wiiリモコンが未接続
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public void setLEDLights(int wiimoteNo, boolean[] pattern) 
	throws WiimoteNotFoundException, IllegalArgumentException, IOException, WiimoteNotConnectException{
		if(!wiimoteMap.containsKey(wiimoteNo)){
			throw new WiimoteNotFoundException();
		}
		Wiimote wiim = wiimoteMap.get(wiimoteNo);
		wiim.setLEDLights(pattern);
	}

	/**
	 * 指定されたWiiリモコンの位置情報を返す
	 * @param wiimoteNo WiiリモコンNo.
	 * @return 位置情報MAP
	 * @throws WiimoteNotFoundException Wiiリモコンが存在しない(不正なWiiリモコンNo.)
	 * @throws WiimoteNotConnectException Wiiリモコンが未接続
	 */
	public Map<Integer, Double> getPositionInfo(int wiimoteNo) 
	throws WiimoteNotFoundException, WiimoteNotConnectException{
		if(!wiimoteMap.containsKey(wiimoteNo)){
			throw new WiimoteNotFoundException();
		}
		Wiimote wiim = wiimoteMap.get(wiimoteNo);
		return wiim.getPositionInfo();
	}
	
	/**
	 * 空きのWiiリモコンを返す
	 * @return Wiiリモコン
	 */
	private synchronized Wiimote getWiimote(){
		for(int i = 0; i < maxStock; i++){
			Wiimote wiim = wiimoteMap.get(i);
			if(wiim == null || !wiim.isConnected()){
				return wiim;
			}
		}
		return null;
	}
}
