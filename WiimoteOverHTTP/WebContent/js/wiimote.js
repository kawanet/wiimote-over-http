function changeParam(){
	var method = document.getElementById("method").value;
	tWiimote = document.getElementById("t_wiimote");
	tTime = document.getElementById("t_time");
	tButton = document.getElementById("t_button");
	tLight = document.getElementById("t_light");
	if(method == "findRemote"){
		tWiimote.style.display 	= "none";
		tTime.style.display 		= "none";
		tButton.style.display 	= "none";
		tLight.style.display 		= "none";
	}else if(method == "isConnected"){
		tWiimote.style.display 	= "block";
		tTime.style.display 		= "none";
		tButton.style.display 	= "none";
		tLight.style.display 		= "none";
	}else if(method == "vibrateFor"){
		tWiimote.style.display 	= "block";
		tTime.style.display 		= "block";
		tButton.style.display 	= "none";
		tLight.style.display 		= "none";
	}else if(method == "isPressed"){
		tWiimote.style.display 	= "block";
		tTime.style.display 		= "none";
		tButton.style.display 	= "block";
		tLight.style.display 		= "none";
	}else if(method == "PositionInfo"){
		tWiimote.style.display 	= "block";
		tTime.style.display 		= "none";
		tButton.style.display 	= "none";
		tLight.style.display 		= "none";
	}else if(method == "setLEDLights"){
		tWiimote.style.display 	= "block";
		tTime.style.display 		= "none";
		tButton.style.display 	= "none";
		tLight.style.display 		= "block";
	}	
}
