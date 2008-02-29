package org.mtl.wiimote.device;

import wiiremotej.WiiRemoteExtension;
import wiiremotej.event.WRAccelerationEvent;
import wiiremotej.event.WRButtonEvent;
import wiiremotej.event.WRCombinedEvent;
import wiiremotej.event.WRExtensionEvent;
import wiiremotej.event.WRIREvent;
import wiiremotej.event.WRStatusEvent;
import wiiremotej.event.WiiRemoteListener;
// XXX 今のところ未使用
public class WiimoteListener implements WiiRemoteListener{
	
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
		if(arg0.isPressed(WRButtonEvent.A)){
			System.out.println("A");
		}
		if(arg0.isPressed(WRButtonEvent.B)){
			System.out.println("B");
		}
		if(arg0.isPressed(WRButtonEvent.HOME)){
			System.out.println("HOME");
		}
		if(arg0.isPressed(WRButtonEvent.DOWN)){
			System.out.println("↓");
		}
		if(arg0.isPressed(WRButtonEvent.UP)){
			System.out.println("↑");
		}
		if(arg0.isPressed(WRButtonEvent.LEFT)){
			System.out.println("←");
		}
		if(arg0.isPressed(WRButtonEvent.RIGHT)){
			System.out.println("→");
		}
		if(arg0.isPressed(WRButtonEvent.ONE)){
			try{
				if(!arg0.getSource().isVibrating()){
					System.out.println("*** Vibration Start ***");
					arg0.getSource().startVibrating();
					System.out.println("1");
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if(arg0.isPressed(WRButtonEvent.TWO)){
			try{
				if(arg0.getSource().isVibrating()){
					System.out.println("*** Vibration Stop ***");
					arg0.getSource().stopVibrating();
					System.out.println("2");
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if(arg0.isPressed(WRButtonEvent.MINUS)){
			try{
				if(arg0.getSource().isAccelerometerEnabled()){
					System.out.println("*** AccelerometerDisabled ***");
					arg0.getSource().setAccelerometerEnabled(false);				
					System.out.println("-");
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if(arg0.isPressed(WRButtonEvent.PLUS)){
			try{
				if(!arg0.getSource().isAccelerometerEnabled()){
					System.out.println("*** AccelerometerEnabled ***");
					arg0.getSource().setAccelerometerEnabled(true);
					System.out.println("+");
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}

	public void combinedInputReceived(WRCombinedEvent arg1) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	public void disconnected() {
		System.out.println("=== DISCONNECT === wiimoteNo:"+this.wiimoteNo);
		try {
			this.finalize();
		} catch (Throwable e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
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
