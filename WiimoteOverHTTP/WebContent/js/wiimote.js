function changeParam(){
	var method = document.getElementById("method").value;
	tWiimote = document.getElementById("wiimote");
	tTime = document.getElementById("time");
	tButton = document.getElementById("button");
	tLight = document.getElementById("light");
	if(method == "findWiimote"){
		tWiimote.disabled 			= true;
		tTime.disabled 					= true;
		tButton.disabled 				= true;
		tLight.disabled 				= true;
	}else if(method == "releaseWiimote"){
		tWiimote.disabled 			= false;
		tTime.disabled 					= true;
		tButton.disabled 				= true;
		tLight.disabled 				= true;
	}else if(method == "isConnected"){
		tWiimote.disabled 			= false;
		tTime.disabled 					= true;
		tButton.disabled 				= true;
		tLight.disabled 				= true;
	}else if(method == "setVibrate"){
		tWiimote.disabled 			= false;
		tTime.disabled 					= false;
		tButton.disabled 				= true;
		tLight.disabled 				= true;
	}else if(method == "isPressed"){
		tWiimote.disabled 			= false;
		tTime.disabled 					= true;
		tButton.disabled 				= false;
		tLight.disabled 				= true;
	}else if(method == "getStatus"){
		tWiimote.disabled 			= false;
		tTime.disabled 					= true;
		tButton.disabled 				= true;
		tLight.disabled 				= true;
	}else if(method == "setLED"){
		tWiimote.disabled 			= false;
		tTime.disabled 					= false;
		tButton.disabled 				= true;
		tLight.disabled 				= false;
	}	
}
