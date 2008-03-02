<%@page language="java" contentType="text/html; charset=UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional //EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml">
<head>
<meta http-equiv="Content-Type"	content="text/html; charset=UTF-8" />
<title>WiimoteOverHTML</title>
<script type="text/javascript" src="js/wiimote.js"></script>
<script type="text/javascript" >
  function call(value) {
    alert(value);
  }
</script>
<script type="text/javascript" src="http://localhost:8080/execute.json?callback=call"></script>


</head>
<body>
<form action="./execute">
<center>
<table border="1" cellpadding="0" cellspacing="0">
	<tr>
		<td>METHOD</td>
		<td>
			<select name="method" onchange="changeParam();">
				<option value="findRemote">Wiiリモコン探索</option>
				<option value="isConnected">Wiiリモコン接続状況取得</option>
				<option value="vibrateFor">バイブレーション操作</option>
				<option value="isPressed">ボタン押下状況取得</option>
				<option value="PositionInfo" disabled>傾きセンサー？状況取得</option>
				<option value="setLEDLights">LED点灯・消灯操作</option>
			</select>
		</td>
	</tr>
	<tr id="t_wiimote" style="display:none;">
		<td>WIIMOTE</td>
		<td>
			<select name="wiimote">
				<option value="0">0</option>
				<option value="1">1</option>
				<option value="2">2</option>
				<option value="3">3</option>
				<option value="4">4</option>
				<option value="5">5</option>
				<option value="6">6</option>
				<option value="7">7</option>
			</select>
		</td>
	</tr>
	<tr id="t_time" style="display:none;">
		<td>TIME</td>
		<td><input type="text" name="time" value="3000"/>※ミリ秒</td>
	</tr>
	<tr id="t_button" style="display:none;">
		<td>BUTTON</td>
		<td>
			<select name="button">
				<option value="ONE">ONE</option>
				<option value="TWO">TWO</option>
				<option value="A">A</option>
				<option value="B">B</option>
				<option value="HOME">HOME</option>
				<option value="PLUS">PLUS</option>
				<option value="MINUS">MINUS</option>
				<option value="UP">UP</option>
				<option value="DOWN">DOWN</option>
				<option value="RIGHT">RIGHT</option>
				<option value="LEFT">LEFT</option>
			</select>
		</td>
	</tr>
	<tr id="t_light" style="display:none;">
		<td>LIGHT</td>
		<td><input type="text" name="light" value="1,0,1,0"/>※カンマ区切り(1:ON 0:OFF)</td>
	</tr>
	<tr>
		<td>RESPONSE</td>
		<td>
			<select name="responseType" onchange="changeParam();">
				<option value="XML">XML</option>
				<option value="JSON">JSON</option>
			</select>
		</td>
	</tr>
</table>
<br>
<input type="submit" name="送信" />
</center>
</form>
</body>
</html>
