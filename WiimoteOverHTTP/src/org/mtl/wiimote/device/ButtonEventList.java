package org.mtl.wiimote.device;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * �{�^���C�x���g�ێ��N���X
 * @author nemoto
 * @version 0.1
 */
public class ButtonEventList{

	/** �{�^���C�x���g�ێ����X�g */
	ArrayList<Object> buttonEvent = new ArrayList<Object>();
	/** �{�^���C�x���g�폜�p�^�C�}�[ */
	Timer timer = new Timer();

	/**
	 * �������ꂽ�{�^������ǉ�����
	 * @param o �{�^�����
	 * @return
	 */
	public boolean add(Object o) {
		timer.purge();
		timer.schedule(new EventClearTask(), 1000);
		return buttonEvent.add(o);
	}

	/**
	 * �������ꂽ�{�^�������w�肳�ꂽ�ʒu�ɒǉ�����
	 * @param index
	 * @param element
	 */
	public void add(int index, Object element) {
		timer.purge();
		timer.schedule(new EventClearTask(), 300);
		buttonEvent.add(index, element);		
	}

	public boolean contains(Object o) {
		return buttonEvent.contains(o);
	}

	public Object[] toArray() {
		return buttonEvent.toArray();
	}

	public void cancel() {
		timer.cancel();
	}		
	
	/**
	 * �{�^���C�x���g���e�폜�C���i�[�N���X
	 * @author nemoto
	 * @version 0.1
	 */
	private class EventClearTask extends TimerTask{
		public void run() {
			buttonEvent.clear();
		}
	}
}
