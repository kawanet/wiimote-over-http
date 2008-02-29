package org.mtl.wiimote.device;

import java.io.IOException;
import java.util.HashMap;

import org.mtl.wiimote.exception.WiimoteNotConnectException;
import org.mtl.wiimote.exception.WiimoteNotFoundException;

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
 * Wiiリモコン管理クラス
 * @author nemoto
 * @version 0.1
 */
public class WiimoteManager {

	/** Wiiリモコン最大接続数 */
	private static final int maxStock = 8;
	/** 接続済みWiiリモコン保持Map */
	private static HashMap<Integer, WiiRemote> wiimoteMap = new HashMap<Integer, WiiRemote>();
	/** ボタン押下状態保持Map */
	private static HashMap<Integer, ButtonEventReserver> buttonEventMap = new HashMap<Integer, ButtonEventReserver>();
	/** LED点灯パターン */
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

	/**
	 * コンストラクタ
	 */
	public WiimoteManager(){
		// WiiRemoteJクラスのログのコンソール表示ON
		WiiRemoteJ.setConsoleLoggingAll();
		for(int i = 0; i < maxStock; i++){
			wiimoteMap.put(i, null);
			buttonEventMap.put(i, null);
		}
	}
	
	/**
	 * 周辺のWiiリモコンを探し、接続を行う
	 * @return 接続成功の場合はtrue
	 * @throws IllegalStateException
	 * @throws InterruptedException
	 * @throws IOException 
	 * @throws IllegalArgumentException 
	 */
	public boolean findWiimote() throws IllegalStateException, InterruptedException, IllegalArgumentException, IOException{
		int wNo = 0;
		if((wNo = this.getWiimoteNo()) != -1){
			// Wiiリモコンを探す
			WiiRemote wiiremote = WiiRemoteJ.findRemote();
			if(wiiremote != null && wiiremote.isConnected()){
				System.out.println("=== CONNECT === wiimoteNo:"+wNo);
				wiiremote.setLEDLights(LED_PATTERN[wNo]);
				// イベントのリスナを設定
				wiiremote.addWiiRemoteListener(new WiimoteListener(wNo));
				
				// 接続済Wiiリモコンを追加
				wiimoteMap.put(wNo, wiiremote);
				// Wiiリモコンボタンイベント追加
				buttonEventMap.put(wNo, new ButtonEventReserver());
				return true;
			}
			return false;
		}
		return false;
	}
	
	/**
	 * 指定されたWiiリモコンの接続状況を返す
	 * @param wiimoteNo WiiリモコンNo.
	 * @return 接続されている場合はtrue 未接続の場合はfalse
	 * @throws WiimoteNotFoundException Wiiリモコンが存在しない
	 */
	public boolean isConnected(int wiimoteNo) throws WiimoteNotFoundException{
		if(!wiimoteMap.containsKey(wiimoteNo)){
			throw new WiimoteNotFoundException();
		}
		WiiRemote wr = wiimoteMap.get(wiimoteNo);
		if(wr != null && wr.isConnected()){
			return true;
		}
		return false;
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
		WiiRemote wr = wiimoteMap.get(wiimoteNo);
		if(wr != null && wr.isConnected()){
			wr.vibrateFor(milliSec);
		}else{
			throw new WiimoteNotConnectException();
		}
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
		WiiRemote wr = wiimoteMap.get(wiimoteNo);
		if(wr != null && wr.isConnected()){
			return buttonEventMap.get(wiimoteNo).contains(keyName);
		}else{
			throw new WiimoteNotConnectException();
		}		
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
		WiiRemote wr = wiimoteMap.get(wiimoteNo);
		if(wr != null && wr.isConnected()){
			wr.setLEDLights(pattern);
		}else{
			throw new WiimoteNotConnectException();
		}		
	}
	
	/**
	 * Wiiリモコンの空きNoを返す
	 * @return Wiiリモコン空きNo.
	 */
	private synchronized int getWiimoteNo(){
		for(int i = 0; i < maxStock; i++){
			WiiRemote wr = wiimoteMap.get(i);
			if(wr == null || !wr.isConnected()){
				return i;
			}
		}
		return -1;
	}

	/**
	 * Wiiリモコンイベントリスナ
	 * @author nemoto
	 * @version 0.1
	 */
	private class WiimoteListener implements WiiRemoteListener{
		
		private int wiimoteNo = 0;
		public WiimoteListener(int wiimoteNo){
			this.wiimoteNo = wiimoteNo;
		}
		public void IRInputReceived(WRIREvent arg0) {
			// TODO 自動生成されたメソッド・スタブ
			
		}

		public void accelerationInputReceived(WRAccelerationEvent arg0) {
			System.out.println("X    :"+arg0.getXAcceleration());
			System.out.println("Y    :"+arg0.getYAcceleration());
			System.out.println("Z    :"+arg0.getZAcceleration());
			System.out.println("Pitch:"+arg0.getPitch());
			System.out.println("Roll :"+arg0.getRoll()+"\n");
		}

		public void buttonInputReceived(WRButtonEvent arg0) {
			ButtonEventReserver bel = buttonEventMap.get(this.wiimoteNo);
			if(arg0.isPressed(WRButtonEvent.A)){
				bel.add("A");
				System.out.println("A");
			}
			if(arg0.isPressed(WRButtonEvent.B)){
				bel.add("B");
				System.out.println("B");
			}
			if(arg0.isPressed(WRButtonEvent.HOME)){
				bel.add("HOME");
				System.out.println("HOME");
			}
			if(arg0.isPressed(WRButtonEvent.DOWN)){
				bel.add("DOWN");
				System.out.println("↓");
			}
			if(arg0.isPressed(WRButtonEvent.UP)){
				bel.add("UP");
				System.out.println("↑");
			}
			if(arg0.isPressed(WRButtonEvent.LEFT)){
				bel.add("LEFT");
				System.out.println("←");
			}
			if(arg0.isPressed(WRButtonEvent.RIGHT)){
				bel.add("RIGHT");
				System.out.println("→");
			}
			if(arg0.isPressed(WRButtonEvent.ONE)){
				bel.add("ONE");
				System.out.println("1");
			}
			if(arg0.isPressed(WRButtonEvent.TWO)){
				bel.add("TWO");
				System.out.println("2");
			}
			if(arg0.isPressed(WRButtonEvent.MINUS)){
				bel.add("MINUS");
				System.out.println("-");
			}
			if(arg0.isPressed(WRButtonEvent.PLUS)){
				bel.add("PLUS");
				System.out.println("+");
			}
			
		}

		public void combinedInputReceived(WRCombinedEvent arg1) {
			// TODO 自動生成されたメソッド・スタブ
			
		}

		public void disconnected() {
			buttonEventMap.get(this.wiimoteNo).cancel();
			wiimoteMap.put(this.wiimoteNo, null);
			System.out.println("=== DISCONNECT === wiimoteNo:"+this.wiimoteNo);
		}

		public void extensionConnected(WiiRemoteExtension arg0) {
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
