package org.mtl.wiimote.device;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * ボタンイベント保持クラス
 * @author nemoto
 * @version 0.1
 */
public class ButtonEventList{

	/** ボタンイベント保持リスト */
	ArrayList<Object> buttonEvent = new ArrayList<Object>();
	/** ボタンイベント削除用タイマー */
	Timer timer = new Timer();

	/**
	 * 押下されたボタン情報を追加する
	 * @param o ボタン情報
	 * @return
	 */
	public boolean add(Object o) {
		timer.purge();
		timer.schedule(new EventClearTask(), 1000);
		return buttonEvent.add(o);
	}

	/**
	 * 押下されたボタン情報を指定された位置に追加する
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
	 * ボタンイベント内容削除インナークラス
	 * @author nemoto
	 * @version 0.1
	 */
	private class EventClearTask extends TimerTask{
		public void run() {
			buttonEvent.clear();
		}
	}
}
