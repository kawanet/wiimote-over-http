package org.mtl.wiimote.service.validator;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.GenericValidator;
import org.mtl.wiimote.service.Constant;

//XXX 今のところ未使用
public class ParamValidator {

	/**
	 * WiimoteServiceActionのパラメータ検証を行う
	 * @param request
	 * @return パラメータに不備・不正がない場合はtrue
	 */
	public boolean validateWiimoteServerAction(HttpServletRequest request){
		String method 	= request.getParameter(Constant.METHOD);
		String wiimote 	= request.getParameter(Constant.WIIMOTE);
		String time 	= request.getParameter(Constant.TIME);
		String button	= request.getParameter(Constant.BUTTON);
		String light	= request.getParameter(Constant.LIGHT);
		if(!GenericValidator.isBlankOrNull(method)){
			if(method.equals(Constant.FIND_REMOTE)){
				return true;
			}else if(method.equals(Constant.POSITION_INFO) && 
					!GenericValidator.isBlankOrNull(wiimote) && GenericValidator.isInt(wiimote)){
				return true;
			}else if(method.equals(Constant.VIBRATE_FOR) && 
					!GenericValidator.isBlankOrNull(wiimote) && GenericValidator.isInt(wiimote) &&
					!GenericValidator.isBlankOrNull(time) && GenericValidator.isLong(time)){
				return true;
			}else if(method.equals(Constant.IS_PRESSED) && 
					!GenericValidator.isBlankOrNull(wiimote) && GenericValidator.isInt(wiimote) &&
					!GenericValidator.isBlankOrNull(button) && GenericValidator.matchRegexp(time, "HOME|MINUS|PLUS|A|B|ONE|TWO|UP|DOWN|LEFT|RIGHT")){
				return true;
			}else if(method.equals(Constant.SET_LED_LIGHTS) && 
					!GenericValidator.isBlankOrNull(wiimote) && GenericValidator.isInt(wiimote) &&
					!GenericValidator.isBlankOrNull(light) && GenericValidator.matchRegexp(time, "^[01],[01],[01],[01]$")){
				return true;
			}
			
//			if(	(!method.equals(Constant.FIND_REMOTE) && !method.equals(Constant.POSITION_INFO) && !paramMap.containsKey(Constant.WIIMOTE)) || 
//				(method.equals(Constant.VIBRATE_FOR) && !paramMap.containsKey(Constant.TIME)) || 
//				(method.equals(Constant.IS_PRESSED) && !paramMap.containsKey(Constant.BUTTON)) || 
//				(method.equals(Constant.SET_LED_LIGHTS) && !paramMap.containsKey(Constant.LIGHT))){
//				return false;
//			}
		}
		return false;
	}
}
