package org.mtl.wiimote.device;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import wiiremotej.event.WRButtonEvent;

/**
 * �{�^���C�x���g�ێ��N���X
 * @author nemoto.hrs
 * @version 0.1
 */
public class ButtonEventReserver{

	/** �{�^���C�x���g�ێ����X�g */
	private ArrayList<Object> buttonEventList = new ArrayList<Object>();
	/** �{�^���C�x���g�폜�p�^�C�}�[ */
	private Timer timer = new Timer();

	/**
	 * �������ꂽ�{�^���C�x���g��ǉ�����
	 * @param o �{�^���C�x���g
	 * @return
	 */
	public boolean add(Object o) {
		timer.purge();
		timer.schedule(new EventClearTask(), 100000);
		return buttonEventList.add(o);
	}

	/**
	 * �������ꂽ�{�^�������w�肳�ꂽ�ʒu�ɒǉ�����
	 * @param index
	 * @param element
	 */
	public void add(int index, Object element) {
		timer.purge();
		timer.schedule(new EventClearTask(), 100000);
		buttonEventList.add(index, element);		
	}

	public WRButtonEvent get(int index){
		if(this.buttonEventList.size() > index){
			return (WRButtonEvent)this.buttonEventList.get(index);
		}
		return null;
	}
	
	public boolean contains(Object o) {
		return buttonEventList.contains(o);
	}

	public Object[] toArray() {
		return buttonEventList.toArray();
	}

	public void cancel() {
		timer.cancel();
	}		
	
	/**
	 * �{�^���C�x���g���e�폜�C���i�[�N���X
	 * @author nemoto.hrs
	 * @version 0.1
	 */
	private class EventClearTask extends TimerTask{
		public void run() {
			if(buttonEventList.size() > 0){
				buttonEventList.remove(0);
			}
		}
	}
}
