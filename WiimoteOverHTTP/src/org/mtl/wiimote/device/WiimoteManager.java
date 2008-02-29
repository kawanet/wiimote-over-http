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
 * Wii�����R���Ǘ��N���X
 * @author nemoto
 * @version 0.1
 */
public class WiimoteManager {

	/** Wii�����R���ő�ڑ��� */
	private static final int maxStock = 8;
	/** �ڑ��ς�Wii�����R���ێ�Map */
	private static HashMap<Integer, WiiRemote> wiimoteMap = new HashMap<Integer, WiiRemote>();
	/** �{�^��������ԕێ�Map */
	private static HashMap<Integer, ButtonEventReserver> buttonEventMap = new HashMap<Integer, ButtonEventReserver>();
	/** LED�_���p�^�[�� */
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
	 * �R���X�g���N�^
	 */
	public WiimoteManager(){
		// WiiRemoteJ�N���X�̃��O�̃R���\�[���\��ON
		WiiRemoteJ.setConsoleLoggingAll();
		for(int i = 0; i < maxStock; i++){
			wiimoteMap.put(i, null);
			buttonEventMap.put(i, null);
		}
	}
	
	/**
	 * ���ӂ�Wii�����R����T���A�ڑ����s��
	 * @return �ڑ������̏ꍇ��true
	 * @throws IllegalStateException
	 * @throws InterruptedException
	 * @throws IOException 
	 * @throws IllegalArgumentException 
	 */
	public boolean findWiimote() throws IllegalStateException, InterruptedException, IllegalArgumentException, IOException{
		int wNo = 0;
		if((wNo = this.getWiimoteNo()) != -1){
			// Wii�����R����T��
			WiiRemote wiiremote = WiiRemoteJ.findRemote();
			if(wiiremote != null && wiiremote.isConnected()){
				System.out.println("=== CONNECT === wiimoteNo:"+wNo);
				wiiremote.setLEDLights(LED_PATTERN[wNo]);
				// �C�x���g�̃��X�i��ݒ�
				wiiremote.addWiiRemoteListener(new WiimoteListener(wNo));
				
				// �ڑ���Wii�����R����ǉ�
				wiimoteMap.put(wNo, wiiremote);
				// Wii�����R���{�^���C�x���g�ǉ�
				buttonEventMap.put(wNo, new ButtonEventReserver());
				return true;
			}
			return false;
		}
		return false;
	}
	
	/**
	 * �w�肳�ꂽWii�����R���̐ڑ��󋵂�Ԃ�
	 * @param wiimoteNo Wii�����R��No.
	 * @return �ڑ�����Ă���ꍇ��true ���ڑ��̏ꍇ��false
	 * @throws WiimoteNotFoundException Wii�����R�������݂��Ȃ�
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
	 * �w�肳�ꂽWii�����R���̃o�C�u���[�V������ON�ɂ���
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
		WiiRemote wr = wiimoteMap.get(wiimoteNo);
		if(wr != null && wr.isConnected()){
			wr.vibrateFor(milliSec);
		}else{
			throw new WiimoteNotConnectException();
		}
	}
	
	/**
	 * �w�肳�ꂽWii�����R���̃{�^�����������ꂽ�����f����
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
		WiiRemote wr = wiimoteMap.get(wiimoteNo);
		if(wr != null && wr.isConnected()){
			return buttonEventMap.get(wiimoteNo).contains(keyName);
		}else{
			throw new WiimoteNotConnectException();
		}		
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
		WiiRemote wr = wiimoteMap.get(wiimoteNo);
		if(wr != null && wr.isConnected()){
			wr.setLEDLights(pattern);
		}else{
			throw new WiimoteNotConnectException();
		}		
	}
	
	/**
	 * Wii�����R���̋�No��Ԃ�
	 * @return Wii�����R����No.
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
	 * Wii�����R���C�x���g���X�i
	 * @author nemoto
	 * @version 0.1
	 */
	private class WiimoteListener implements WiiRemoteListener{
		
		private int wiimoteNo = 0;
		public WiimoteListener(int wiimoteNo){
			this.wiimoteNo = wiimoteNo;
		}
		public void IRInputReceived(WRIREvent arg0) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
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
				System.out.println("��");
			}
			if(arg0.isPressed(WRButtonEvent.UP)){
				bel.add("UP");
				System.out.println("��");
			}
			if(arg0.isPressed(WRButtonEvent.LEFT)){
				bel.add("LEFT");
				System.out.println("��");
			}
			if(arg0.isPressed(WRButtonEvent.RIGHT)){
				bel.add("RIGHT");
				System.out.println("��");
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
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}

		public void disconnected() {
			buttonEventMap.get(this.wiimoteNo).cancel();
			wiimoteMap.put(this.wiimoteNo, null);
			System.out.println("=== DISCONNECT === wiimoteNo:"+this.wiimoteNo);
		}

		public void extensionConnected(WiiRemoteExtension arg0) {
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
