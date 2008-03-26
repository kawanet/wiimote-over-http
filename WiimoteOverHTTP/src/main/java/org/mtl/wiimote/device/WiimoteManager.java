package org.mtl.wiimote.device;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.mtl.wiimote.exception.WiimoteNotConnectException;
import org.mtl.wiimote.exception.WiimoteNotFoundException;

import wiiremotej.WiiRemoteJ;

/**
 * Wii�����R���Ǘ��N���X
 * @author nemoto.hrs
 * @version 0.1
 */
public class WiimoteManager {

	/** Wii�����R���ő�ڑ��� */
	private static final int maxStock = 7;
	/** �f�t�H���gLED�_���p�^�[�����X�g */
	private final static boolean[][] LED_PATTERN = {
		{true,	true,	true,	true},
		{true,	false,	false,	false},
		{false,	true,	false,	false},
		{false,	false,	true,	false},
		{false,	false,	false,	true},
		{true,	false,	false,	true},
		{false,	true,	false,	true},
		{false,	false,	true,	true}
	};

	/** Wii�����R���ێ�Map */
	private static HashMap<Integer, Wiimote> wiimoteMap = new HashMap<Integer, Wiimote>();
	/** Wii�����R��KEY Map */
	private static HashMap<String, Integer> keyMap = new HashMap<String, Integer>();
	
	/**
	 * �R���X�g���N�^
	 */
	public WiimoteManager(){
		// WiiRemoteJ�N���X�̃��O�̃R���\�[���\��ON
		WiiRemoteJ.setConsoleLoggingAll();
		// Wii�����R���̃C���X�^���X���}�b�s���O
		for(int i = 1; i <= maxStock; i++){
			Wiimote wiim = new Wiimote(i);
			wiim.setDefaultLEDPattern(LED_PATTERN[i]);
			wiimoteMap.put(i, wiim);
		}
		// KEY�̒l���}�b�s���O
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
	
//	/**
//	 * ���ӂ�Wii�����R����T���A�ڑ����s���i1��̂݁j
//	 * @return �ڑ������̏ꍇ��true�@�ڑ����s�A�����o�̏ꍇ��false
//	 */
//	public boolean findWiimote(){
//		Wiimote wiim = null;
//		if((wiim = this.getWiimote()) != null){
//			// Wii�����R����T�����ڑ�
//			return wiim.connect();
//		}
//		return false;
//	}

	/**
	 * ���ӂ�Wii�����R����T���A�ڑ����s��
	 * @return �ڑ�������
	 */
	public int findWiimote(){
		int wCnt = 0;
		for(int i = 1; i <= maxStock; i++){
			Wiimote wiim = wiimoteMap.get(i);
			if(wiim != null && !wiim.isConnected()){
				if(wiim.connect()){
					wCnt++;
				}else{
					break;
				}
			}
		}
		return wCnt;
	}

	/**
	 * �ڑ�����Ă���Wii�����R����ؒf����
	 * @param wiimoteNo Wii�����R��No.
	 * @throws WiimoteNotFoundException Wii�����R�������݂��Ȃ�(�s����Wii�����R��No.)
	 */
	public void releaseWiimote(int wiimoteNo) throws WiimoteNotFoundException{
		if(!wiimoteMap.containsKey(wiimoteNo)){
			throw new WiimoteNotFoundException();
		}
		Wiimote wiim = wiimoteMap.get(wiimoteNo);
		if(wiim != null && wiim.isConnected()){
			wiim.disconnect();
		}
	}

	/**
	 * �ڑ�����Ă���S�Ă�Wii�����R����ؒf����
	 */
	public void releaseAllWiimote(){
		for(int i = 1; i <= maxStock; i++){
			Wiimote wiim = wiimoteMap.get(i);
			if(wiim != null && wiim.isConnected()){
				wiim.disconnect();
			}
		}
	}

	/**
	 * �w�肳�ꂽWii�����R���̐ڑ��󋵂�Ԃ�
	 * @param wiimoteNo Wii�����R��No.
	 * @return �ڑ�����Ă���ꍇ��true ���ڑ��̏ꍇ��false
	 * @throws WiimoteNotFoundException Wii�����R�������݂��Ȃ�(�s����Wii�����R��No.)
	 */
	public boolean isConnected(int wiimoteNo) throws WiimoteNotFoundException{
		if(!wiimoteMap.containsKey(wiimoteNo)){
			throw new WiimoteNotFoundException();
		}
		Wiimote wiim = wiimoteMap.get(wiimoteNo);
		return wiim.isConnected();
	}

	/**
	 * �w�肳�ꂽWii�����R���̃o�C�u���[�V�������w��b����ON�ɂ���
	 * @param wiimoteNo Wii�����R��No.
	 * @throws IOException 
	 * @throws WiimoteNotFoundException Wii�����R�������݂��Ȃ�(�s����Wii�����R��No.)
	 * @throws WiimoteNotConnectException Wii�����R�������ڑ�
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
	 * �w�肳�ꂽWii�����R���̎w�肳�ꂽ�{�^�����������ꂽ�����f����
	 * @param wiimoteNo Wii�����R��No.
	 * @param keyName �{�^����
	 * @return ������Ă����ꍇ��true
	 * @throws WiimoteNotFoundException Wii�����R�������݂��Ȃ�(�s����Wii�����R��No.)
	 * @throws WiimoteNotConnectException Wii�����R�������ڑ�
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
	 * �w�肳�ꂽWii�����R����LED���w�肳�ꂽ�p�^�[���œ_��������
	 * @param wiimoteNo Wii�����R��No.
	 * @param pattern �_���p�^�[��
	 * @throws WiimoteNotFoundException Wii�����R�������݂��Ȃ�(�s����Wii�����R��No.)
	 * @throws WiimoteNotConnectException Wii�����R�������ڑ�
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
	 * �w�肳�ꂽWii�����R����LED���w�肳�ꂽ�p�^�[���Ŏw��b���ԓ_��������
	 * @param wiimoteNo Wii�����R��No.
	 * @param pattern �_���p�^�[��
	 * @throws WiimoteNotFoundException Wii�����R�������݂��Ȃ�(�s����Wii�����R��No.)
	 * @throws WiimoteNotConnectException Wii�����R�������ڑ�
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public void setLEDLightsFor(int wiimoteNo, boolean[] pattern, long milliSec) 
	throws WiimoteNotFoundException, IllegalArgumentException, IOException, WiimoteNotConnectException{
		if(!wiimoteMap.containsKey(wiimoteNo)){
			throw new WiimoteNotFoundException();
		}
		Wiimote wiim = wiimoteMap.get(wiimoteNo);
		wiim.setLEDLightsFor(pattern, milliSec);
	}

	/**
	 * �w�肳�ꂽWii�����R���̈ʒu����Ԃ�
	 * @param wiimoteNo Wii�����R��No.
	 * @return �ʒu���MAP
	 * @throws WiimoteNotFoundException Wii�����R�������݂��Ȃ�(�s����Wii�����R��No.)
	 * @throws WiimoteNotConnectException Wii�����R�������ڑ�
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
	 * �w�肳�ꂽWii�����R���̏���Ԃ�
	 * @param wiimoteNo Wii�����R��No.
	 * @return ���MAP
	 * @throws WiimoteNotFoundException Wii�����R�������݂��Ȃ�(�s����Wii�����R��No.)
	 * @throws WiimoteNotConnectException Wii�����R�������ڑ�
	 */
	public Map<Integer, Map> getStatus(int wiimoteNo) 
	throws WiimoteNotFoundException, WiimoteNotConnectException{
		Map<Integer, Map> map = new HashMap<Integer, Map>();
		if(!wiimoteMap.containsKey(wiimoteNo)){
			throw new WiimoteNotFoundException();
		}
		Wiimote wiim = wiimoteMap.get(wiimoteNo);
		map.put(wiimoteNo, wiim.getStatus());
		return map;
	}

	/**
	 * �S�Ă�Wii�����R���̏���Ԃ�
	 * @return ���MAP
	 * @throws WiimoteNotFoundException Wii�����R�������݂��Ȃ�(�s����Wii�����R��No.)
	 * @throws WiimoteNotConnectException Wii�����R�������ڑ�
	 */
	public Map<Integer, Map> getAllStatus() 
	throws WiimoteNotFoundException, WiimoteNotConnectException{
		Map<Integer, Map> map = new HashMap<Integer, Map>();
		for(int i = 1; i <= maxStock; i++){
			Wiimote wiim = wiimoteMap.get(i);
			if(wiim != null && wiim.isConnected()){
				map.put(i, wiim.getStatus());
			}
		}
		if(map.size() == 0){
			throw new WiimoteNotFoundException();
		}
		return map;
	}

	/**
	 * �󂫂�Wii�����R����Ԃ�
	 * @return Wii�����R��
	 */
	@SuppressWarnings("unused")
	private synchronized Wiimote getWiimote(){
		for(int i = 1; i <= maxStock; i++){
			Wiimote wiim = wiimoteMap.get(i);
			if(wiim == null || !wiim.isConnected()){
				return wiim;
			}
		}
		return null;
	}
}
