<%@page language="java" contentType="text/html; charset=UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional //EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml">
<head>
<meta http-equiv="Content-Type"	content="text/html; charset=UTF-8" />
<title>WiimoteOverHTML</title>
<style type="text/css">
table.param {
	border:1px solid #777777;
}
td.index {
	border:1px solid #777777;
	background-color:#9999FF;
	font-size:13px;
	padding:0px 5px;
}
td.val {
	border:1px solid #777777;
	background-color:#FFFFFF;
	padding:1px;
	font-size:12px;
}
select {
	width:300px;
}
input.txt {
	width:295px;
}
</style>
<script type="text/javascript" src="js/wiimote.js"></script>
<script type="text/javascript" >
  function call(value) {
    alert(value);
  }
</script>
<script type="text/javascript" src="http://localhost:8080/execute.json?callback=call"></script>


</head>
<body onload="changeParam();">
<form action="./execute">
<center>
<table class="param" border="0" cellpadding="0" cellspacing="1">
	<tr>
		<td class="index">method</td>
		<td class="val">
			<select name="method" onchange="changeParam();">
				<option value="findWiimote">[findWiimote]　Wiiリモコン探索・接続</option>
				<option value="isConnected">[isConnected]　Wiiリモコン接続状況取得</option>
				<option value="setVibrate">[setVibrate]　バイブレーション操作</option>
				<option value="isPressed">[isPressed]　ボタン押下状況取得</option>
				<option value="getStatus">[getStatus]　Wiiリモコン操作情報取得</option>
				<option value="setLED">[setLED]　LED点灯・消灯操作</option>
				<option value="releaseWiimote">[releaseWiimote]　Wiiリモコン切断</option>
			</select>
		</td>
	</tr>
	<tr>
		<td class="index">wiimote</td>
		<td class="val">
			<select name="wiimote">
				<option value="">指定なし</option>
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
	<tr>
		<td class="index">time<br><font color="red" style="font-size:9px;">※ﾐﾘ秒</font></td>
		<td class="val"><input class="txt" type="text" name="time" value="3000"/></td>
	</tr>
	<tr>
		<td class="index">button</td>
		<td class="val">
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
	<tr>
		<td class="index">light<br><font color="red" style="font-size:9px;">※ｶﾝﾏ区切り（1:ON 0:OFF）</font></td>
		<td class="val"><input class="txt" type="text" name="light" value="1,0,1,0"/></td>
	</tr>
	<tr>
		<td class="index">response</td>
		<td class="val">
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
