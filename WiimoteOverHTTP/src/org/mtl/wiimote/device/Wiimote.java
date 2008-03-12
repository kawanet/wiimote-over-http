package org.mtl.wiimote.device;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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
 * Wii�����R���N���X
 * @author nemoto.hrs
 * @version 0.1
 */
public class Wiimote{
	/** Wii�����[�g */
	private WiiRemote wiiremote = null;
	/** �R���g���[��No. */
	private int wiimoteNo = -1;
	/** �f�t�H���gLED�_���p�^�[�� */
	private boolean[] defaultLEDPattern = null;
	/** �{�^��������ԕێ�MAP */
	private HashMap<Integer, Boolean> buttonMap = new HashMap<Integer, Boolean>();
	/** �ʒu���ێ�MAP */
	private HashMap<Integer, Double> positionMap = new HashMap<Integer, Double>();
	/** �f�t�H���gLED�_���p�^�[�����X�g */
	private static final boolean[][] LED_PATTERN = {
		{true,	true,	true,	true},
		{true,	false,	false,	false},
		{false,	true,	false,	false},
		{false,	false,	true,	false},
		{false,	false,	false,	true}
	};
	/* Wii�����R��No */
	private static final int NUM_OTHER 	= 0;
	public static final int NUM_ONE 		= 1;
	public static final int NUM_TWO 		= 2;
	public static final int NUM_THREE 	= 3;
	public static final int NUM_FOUR 		= 4;
	
	/* Wii�����R��KEY */
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
	
	/* Wii�����R���ʒu */
	public static final int POS_X 		= 100;
	public static final int POS_Y 		= 110;
	public static final int POS_Z 		= 120;
	public static final int POS_PITCH		= 130;
	public static final int POS_ROLL		= 140;
		
	/**
	 * �R���X�g���N�^
	 */
	public Wiimote(){
		new Wiimote(NUM_OTHER);
	}
	
	/**
	 * �R���X�g���N�^
	 * @param wiimoteNo Wii�����R��No.
	 */
	public Wiimote(int wiimoteNo){
		this.wiimoteNo = wiimoteNo;
		if(this.wiimoteNo <= NUM_ONE && this.wiimoteNo <= NUM_FOUR){
			this.defaultLEDPattern = LED_PATTERN[this.wiimoteNo];
		}else{
			this.defaultLEDPattern = LED_PATTERN[NUM_OTHER];
		}
		this.initInfoMap();
	}
	
	/**
	 * ��ԕێ�MAP������
	 */
	private void initInfoMap(){
		// �{�^�����
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
		// �ʒu���
		positionMap.put(POS_X, 		0.0);
		positionMap.put(POS_Y, 		0.0);
		positionMap.put(POS_Z, 		0.0);
		positionMap.put(POS_PITCH, 	0.0);
		positionMap.put(POS_ROLL, 	0.0);
	}
	
	/**
	 * ���ӂ�Wii�����R����T���A�ڑ����s��
	 * @return �ڑ������̏ꍇ��true
	 */
	public boolean connect(){
		try{
			// ���ɐڑ�����Ă���ꍇ�͈�U�ؒf����
			if(wiiremote != null && wiiremote.isConnected()){
				this.disconnect();
			}
			// Wii�����R����T��
			wiiremote = WiiRemoteJ.findRemote();
			if(wiiremote != null && wiiremote.isConnected()){
				System.out.println("=== CONNECT === wiimoteNo:"+this.wiimoteNo);
				// �C�x���g�̃��X�i��ݒ�
				wiiremote.addWiiRemoteListener(new WiimoteListener());
				wiiremote.setAccelerometerEnabled(true);
				// LED��_��������
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
	 * Wii�����R����ؒf����
	 */
	public void disconnect(){
		if(wiiremote != null && wiiremote.isConnected()){
			this.initInfoMap();
			wiiremote.disconnect();
			wiiremote = null;
		}
	}
	
	/**
	 * Wii�����R���̐ڑ��󋵂�Ԃ�
	 * @return �ڑ�����Ă���ꍇ��true ���ڑ��̏ꍇ��false
	 */
	public boolean isConnected(){
		if(wiiremote != null){
			return wiiremote.isConnected();
		}
		return false;
	}

	/**
	 * Wii�����R���̃o�C�u���[�V�������w��b����ON�ɂ���
	 * @throws WiimoteNotConnectException Wii�����R�������ڑ�
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
	 * Wii�����R����LED���w�肳�ꂽ�p�^�[���œ_��������
	 * @param pattern �_���p�^�[��
	 * @throws WiimoteNotConnectException Wii�����R�������ڑ�
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
	 * Wii�����R����LED���w�肳�ꂽ�p�^�[���Ŏw��b���ԓ_��������
	 * @param pattern �_���p�^�[��
	 * @throws WiimoteNotConnectException Wii�����R�������ڑ�
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public void setLEDLightsFor(boolean[] pattern, long milliSec) 
	throws IllegalArgumentException, IOException, WiimoteNotConnectException{
		if(wiiremote != null && wiiremote.isConnected()){
			wiiremote.setLEDLights(pattern);
			Timer timer = new Timer(true);
			timer.schedule(new DefaultLEDLightsSetter(), milliSec);
		}else{
			throw new WiimoteNotConnectException();
		}		
	}

	/**
	 * Wii�����R���̎w�肳�ꂽ�{�^�����������ꂽ�����f����
	 * @param key �{�^��
	 * @return ������Ă����ꍇ��true
	 * @throws WiimoteNotConnectException Wii�����R�������ڑ�
	 */
	public boolean isPressed(int key) throws WiimoteNotConnectException{ 
		if(wiiremote != null && wiiremote.isConnected()){
			return buttonMap.get(key);
		}else{
			throw new WiimoteNotConnectException();
		}		
	}

	/**
	 * Wii�����R���̑S�Ẵ{�^������Ԃ�
	 * @return �{�^�����MAP
	 * @throws WiimoteNotConnectException Wii�����R�������ڑ�
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, Boolean> getButtonInfo() throws WiimoteNotConnectException{
		if(wiiremote != null && wiiremote.isConnected()){
			return (Map)buttonMap.clone();
		}else{
			throw new WiimoteNotConnectException();
		}		
	}

	/**
	 * Wii�����R���̎w�肳�ꂽ���W�̏���Ԃ�
	 * @param pos ���W
	 * @return �w�肳�ꂽ���W�ʒu
	 * @throws WiimoteNotConnectException Wii�����R�������ڑ�
	 */
	public double getPosition(int pos) throws WiimoteNotConnectException{ 
		if(wiiremote != null && wiiremote.isConnected()){
			return positionMap.get(pos);
		}else{
			throw new WiimoteNotConnectException();
		}		
	}

	/**
	 * Wii�����R���̑S�Ă̈ʒu����Ԃ�
	 * @return �ʒu���MAP
	 * @throws WiimoteNotConnectException Wii�����R�������ڑ�
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, Double> getPositionInfo() throws WiimoteNotConnectException{
		if(wiiremote != null && wiiremote.isConnected()){
			return (Map)positionMap.clone();
		}else{
			throw new WiimoteNotConnectException();
		}		
	}

	/**
	 * Wii�����R���̃{�^���ƈʒu�S�Ă̏���Ԃ�
	 * @return ���MAP
	 * @throws WiimoteNotConnectException Wii�����R�������ڑ�
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, Object> getStatus() throws WiimoteNotConnectException{
		if(wiiremote != null && wiiremote.isConnected()){
			Map posInfo = (Map)positionMap.clone();
			Map btnInfo = (Map)buttonMap.clone();
			posInfo.putAll(btnInfo);
			return posInfo;
		}else{
			throw new WiimoteNotConnectException();
		}		
	}

	/**
	 * Wii�����R��No.��Ԃ�
	 * @return Wii�����R��No.
	 */
	public int getWiimoteNo(){
		return wiimoteNo;
	}

	/**
	 * Wii�����R��No.��ݒ肷��
	 * @param wiimoteNo Wii�����R��No.
	 */
	public void setWiimoteNo(int wiimoteNo){
		this.wiimoteNo = wiimoteNo;
	}

	/**
	 * LED�̃f�t�H���g�_���p�^�[����Ԃ�
	 * @return LED�̃f�t�H���g�_���p�^�[��
	 */
	public boolean[] getDefaultLEDPattern() {
		return defaultLEDPattern;
	}

	/**
	 * LED�̃f�t�H���g�_���p�^�[����ݒ肷��
	 * @param defaultLEDPattern LED�̃f�t�H���g�_���p�^�[��(true:ON false:OFF)
	 */
	public void setDefaultLEDPattern(boolean[] defaultLEDPattern) {
		this.defaultLEDPattern = defaultLEDPattern;
	}
	
	// Inner Class ==============================
	private class DefaultLEDLightsSetter extends TimerTask{
		@Override
		public void run() {
			try {
				wiiremote.setLEDLights(getDefaultLEDPattern());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Wii�����R���C�x���g���X�i
	 * @author nemoto.hrs
	 * @version 0.1
	 */
	private class WiimoteListener implements WiiRemoteListener{
		
		public void accelerationInputReceived(WRAccelerationEvent arg0) {
			positionMap.put(POS_X, 		arg0.getXAcceleration());
			positionMap.put(POS_Y, 		arg0.getYAcceleration());
			positionMap.put(POS_Z, 		arg0.getZAcceleration());
			positionMap.put(POS_PITCH, 	arg0.getPitch());
			positionMap.put(POS_ROLL, 	arg0.getRoll());
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
			// TODO �����������ꂽ���\�b�h�E�X�^�u		
		}

		public void combinedInputReceived(WRCombinedEvent arg1) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
		}

		public void disconnected() {
			System.out.println("=== DISCONNECT === wiimoteNo:"+wiimoteNo);
			wiiremote = null;
		}

		public void extensionConnected(WiiRemoteExtension arg0) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
		}

		public void extensionDisconnected(WiiRemoteExtension arg0) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
		}

		public void extensionInputReceived(WRExtensionEvent arg0) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
		}

		public void extensionPartiallyInserted() {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
		}

		public void extensionUnknown() {
			// TODO �����������ꂽ���\�b�h�E�X�^�u	
		}

		public void statusReported(WRStatusEvent arg0) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u	
		}

	}
}
