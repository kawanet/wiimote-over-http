package org.mtl.wiimote.device;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import wiiremotej.event.WRButtonEvent;

/**
 * ボタンイベント保持クラス
 * @author nemoto.hrs
 * @version 0.1
 */
public class ButtonEventReserver{

	/** ボタンイベント保持リスト */
	private ArrayList<Object> buttonEventList = new ArrayList<Object>();
	/** ボタンイベント削除用タイマー */
	private Timer timer = new Timer();

	/**
	 * 押下されたボタンイベントを追加する
	 * @param o ボタンイベント
	 * @return
	 */
	public boolean add(Object o) {
		timer.purge();
		timer.schedule(new EventClearTask(), 100000);
		return buttonEventList.add(o);
	}

	/**
	 * 押下されたボタン情報を指定された位置に追加する
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
	 * ボタンイベント内容削除インナークラス
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
