package org.mtl.wiimote.device;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.mtl.wiimote.exception.WiimoteNotConnectException;

import wiiremotej.AnalogStickData;
import wiiremotej.WiiRemote;
import wiiremotej.WiiRemoteExtension;
import wiiremotej.WiiRemoteJ;
import wiiremotej.event.WRAccelerationEvent;
import wiiremotej.event.WRButtonEvent;
import wiiremotej.event.WRCombinedEvent;
import wiiremotej.event.WRExtensionEvent;
import wiiremotej.event.WRIREvent;
import wiiremotej.event.WRNunchukExtensionEvent;
import wiiremotej.event.WRStatusEvent;
import wiiremotej.event.WiiRemoteAdapter;

/**
 * Wii�����R���N���X
 * @author nemoto.hrs
 * @version 2008/03/27
 */
public class Wiimote{
	/** Wii�����[�g */
	private WiiRemote wiiremote = null;
	/** �R���g���[��No. */
	private int wiimoteNo = -1;
	/** �f�t�H���gLED�_���p�^�[�� */
	private boolean[] defaultLEDPattern = null;
	/** �o�b�e���[�c��(%) */
	private double batteryLevel = 0.0;
	/** �{�^��������ԕێ�MAP */
	private HashMap<Integer, Boolean> buttonMap = new HashMap<Integer, Boolean>();
	/** �ʒu���ێ�MAP */
	private HashMap<Integer, Double> positionMap = new HashMap<Integer, Double>();
	/** �A�i���O�X�e�B�b�N���ێ�MAP */
	private HashMap<Integer, Double> analogStickMap = new HashMap<Integer, Double>();

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

	/* �k���`���NKEY */
	public static final int KEY_C 		= WRNunchukExtensionEvent.C;
	public static final int KEY_Z 		= WRNunchukExtensionEvent.Z;
	
	/* �k���`���N�ʒu */
	public static final int NPOS_X 		= 200;
	public static final int NPOS_Y 		= 210;
	public static final int NPOS_Z 		= 220;
	public static final int NPOS_PITCH	= 230;
	public static final int NPOS_ROLL		= 240;
	
	/* �k���`���N�A�i���O�X�e�B�b�N */
	public static final int ALG_X 		= 250;
	public static final int ALG_Y 		= 260;

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
		buttonMap.put(KEY_C,  	 false);
		buttonMap.put(KEY_Z, 	 false);
		// �ʒu���
		positionMap.put(POS_X, 		0.0);
		positionMap.put(POS_Y, 		0.0);
		positionMap.put(POS_Z, 		0.0);
		positionMap.put(POS_PITCH, 	0.0);
		positionMap.put(POS_ROLL, 	0.0);
		positionMap.put(NPOS_X, 	0.0);
		positionMap.put(NPOS_Y, 	0.0);
		positionMap.put(NPOS_Z, 	0.0);
		positionMap.put(NPOS_PITCH,	0.0);
		positionMap.put(NPOS_ROLL, 	0.0);
		// �A�i���O�X�e�B�b�N���
		analogStickMap.put(ALG_X, 0.0);
		analogStickMap.put(ALG_Y, 0.0);
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
				wiiremote.addWiiRemoteListener(new WiimoteAdapter());
				wiiremote.setAccelerometerEnabled(true);
				wiiremote.setIRSensorEnabled(false, WRIREvent.FULL);
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
			Map algInfo = (Map)analogStickMap.clone();
			posInfo.putAll(btnInfo);
			posInfo.putAll(algInfo);
			return posInfo;
		}else{
			throw new WiimoteNotConnectException();
		}		
	}
	
	/**
	 * Wii�����R���̃o�b�e���[�c�ʂ�Ԃ�
	 * @return �o�b�e���[�c��(%)
	 * @throws WiimoteNotConnectException Wii�����R�������ڑ�
	 */
	public double getBatteryLevel() throws WiimoteNotConnectException{
		if(wiiremote != null && wiiremote.isConnected()){
			try{
				wiiremote.requestStatus();
				Thread.sleep(200);
			}catch(Exception e){
				e.printStackTrace();
			};
			return this.batteryLevel;
		}else{
			throw new WiimoteNotConnectException();
		}
	}
	
	/**
	 * �k���`���N���q�����Ă��邩�ۂ���Ԃ�
	 * @return �k���`���N���q�����Ă���ꍇ��true
	 * @throws WiimoteNotConnectException Wii�����R�������ڑ�
	 */
	public boolean isNumchuk() throws WiimoteNotConnectException{
		if(wiiremote != null && wiiremote.isConnected()){
			return wiiremote.isExtensionConnected();
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
	/**
	 * LED�f�t�H���g�_���p�^�[���Z�b�^�[
	 */
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
	 * Wii�����R���A�_�v�^
	 * @author nemoto.hrs
	 * @version 0.3
	 */
	private class WiimoteAdapter extends WiiRemoteAdapter{

		DecimalFormat df = new DecimalFormat("###.######");
		/* Wii�����R�����W */
		Double pX = null;
		Double pY = null;
		Double pZ = null;
		Double pPc = null;
		Double pRl = null;
		
		/* �k���`���N���W */
		Double npX = null;
		Double npY = null;
		Double npZ = null;
		Double npPc = null;
		Double npRl = null;
		
		public void accelerationInputReceived(WRAccelerationEvent arg0) {
			// �ʒu���
			pX = arg0.getXAcceleration();
			pY = arg0.getYAcceleration();
			pZ = arg0.getZAcceleration();
			pPc = arg0.getPitch();
			pRl = arg0.getRoll();
			positionMap.put(POS_X, 		pX.isNaN()?pX:new Double(df.format(pX)));
			positionMap.put(POS_Y, 		pY.isNaN()?pY:new Double(df.format(pY)));
			positionMap.put(POS_Z, 		pZ.isNaN()?pZ:new Double(df.format(pZ)));
			positionMap.put(POS_PITCH, 	pPc.isNaN()?pPc:new Double(df.format(pPc)));
			positionMap.put(POS_ROLL, 	pRl.isNaN()?pRl:new Double(df.format(pRl)));
		}

		public void buttonInputReceived(WRButtonEvent arg0) {
			// �{�^�����
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
			try{
				wiiremote.setExtensionEnabled(true);
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		public void extensionDisconnected(WiiRemoteExtension arg0) {
			try{
				wiiremote.setExtensionEnabled(false);
				// �S�Ă̏���������
				buttonMap.put(KEY_C, false);
				buttonMap.put(KEY_Z, false);
				positionMap.put(NPOS_X, 	0.0);
				positionMap.put(NPOS_Y, 	0.0);
				positionMap.put(NPOS_Z, 	0.0);
				positionMap.put(NPOS_PITCH,	0.0);
				positionMap.put(NPOS_ROLL, 	0.0);
				analogStickMap.put(ALG_X, 0.0);
				analogStickMap.put(ALG_Y, 0.0);
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		public void extensionInputReceived(WRExtensionEvent arg0) {
			// �k���`���N
			if(arg0 instanceof WRNunchukExtensionEvent){
	            WRNunchukExtensionEvent evt = (WRNunchukExtensionEvent)arg0;
                WRAccelerationEvent aEvt = evt.getAcceleration();
                AnalogStickData aData = evt.getAnalogStickData();
                
                // �ʒu���
    			npX = aEvt.getXAcceleration();
    			npY = aEvt.getYAcceleration();
    			npZ = aEvt.getZAcceleration();
    			npPc = aEvt.getPitch();
    			npRl = aEvt.getRoll();
    			positionMap.put(NPOS_X, 	npX.isNaN()?npX:new Double(df.format(npX)));
    			positionMap.put(NPOS_Y, 	npY.isNaN()?npY:new Double(df.format(npY)));
    			positionMap.put(NPOS_Z, 	npZ.isNaN()?npZ:new Double(df.format(npZ)));
    			positionMap.put(NPOS_PITCH, npPc.isNaN()?npPc:new Double(df.format(npPc)));
    			positionMap.put(NPOS_ROLL, 	npRl.isNaN()?npRl:new Double(df.format(npRl)));
                
    			// �{�^�����
				if(evt.wasPressed(WRNunchukExtensionEvent.C))	buttonMap.put(KEY_C, true);
				if(evt.wasPressed(WRNunchukExtensionEvent.Z))	buttonMap.put(KEY_Z, true);
	            
				if(evt.wasReleased(WRNunchukExtensionEvent.C))	buttonMap.put(KEY_C, false);
				if(evt.wasReleased(WRNunchukExtensionEvent.Z))	buttonMap.put(KEY_Z, false);
				
				// �A�i���O�X�e�B�b�N���
				analogStickMap.put(ALG_X, new Double(df.format(aData.getX())));
				analogStickMap.put(ALG_Y, new Double(df.format(aData.getY())));
			}
		}

		public void extensionPartiallyInserted() {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
		}

		public void extensionUnknown() {
			// TODO �����������ꂽ���\�b�h�E�X�^�u	
		}

		public void statusReported(WRStatusEvent arg0) {
			batteryLevel = arg0.getBatteryLevel()*100.0;
		}

	}
}
