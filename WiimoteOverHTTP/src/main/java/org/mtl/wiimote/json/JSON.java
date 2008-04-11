package org.mtl.wiimote.json;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * JSONクラス
 * @author nemoto
 * @version 2008/04/10
 */
public class JSON extends HashMap<String, Object>{
	private static final long serialVersionUID = 1L;

	private static final String _XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
			
	/**
	 * JSONに変換する
	 * @return JSON
	 */
	public String toJSONString(){
		return this.createJSON(this);
	}

	/**
	 * XMLに変換する
	 * @return JSON
	 */
	public String toXMLString(){
		return this.createXML(null, this);
	}

	/**
	 * JSONのStringデータを作成する
	 * @param str String
	 * @return JSON
	 */
	private String createJSON(String str){
		return '"'+str+'"';
	}
	
	/**
	 * JSONのMapデータを作成する
	 * @param map Map
	 * @return JSON
	 */
	private String createJSON(Map map){
		String json = "{";
		Iterator itr = map.keySet().iterator();
		while(itr.hasNext()){
			String key = (String)itr.next();
			json += '"'+key+'"'+":";
			Object val = map.get(key);
			if(val instanceof String){
				json += this.createJSON((String)val);
			}else if(val instanceof Integer){
				json += this.createJSON(((Integer)val).toString());
			}else if(val instanceof Long){
				json += this.createJSON(((Long)val).toString());
			}else if(val instanceof Double){
				json += this.createJSON(((Double)val).toString());
			}else if(val instanceof Map){
				json += this.createJSON((Map)val);
			}else if(val instanceof List){
				json += this.createJSON((List)val);
			}
			json += ",";
		}
		return json.replaceAll(",$", "}");
	}
	
	/**
	 * JSONのListデータを作成する
	 * @param list List
	 * @return JSON
	 */
	private String createJSON(List list){
		String json = "[";
		for(int i = 0; i < list.size(); i++){
			Object val = list.get(i);
			if(val instanceof String){
				json += this.createJSON((String)val);
			}else if(val instanceof Integer){
				json += this.createJSON(((Integer)val).toString());
			}else if(val instanceof Long){
				json += this.createJSON(((Long)val).toString());
			}else if(val instanceof Double){
				json += this.createJSON(((Double)val).toString());
			}else if(val instanceof Map){
				json += this.createJSON((Map)val);
			}else if(val instanceof List){
				json += this.createJSON((List)val);
			}
			json += ",";
		}
		return json.replaceAll(",$", "]");
	}

	
	/**
	 * XMLのMapデータを作成する
	 * @param map Map
	 * @return XML
	 */
	private String createXML(String pNode, Map map){
		String xml = new String();
		String attr = new String();
		Iterator itr = map.keySet().iterator();
		while(itr.hasNext()){
			String key = (String)itr.next();
			Object val = map.get(key);
			if(key.startsWith("@")){
				attr += " "+key.substring(1)+"="+'"'+(String)val+'"';
			}else if(val instanceof String){
				xml += "<"+key+">"+(String)val+"</"+key+">";
			}else if(val instanceof Integer){
				xml += "<"+key+">"+((Integer)val).toString()+"</"+key+">";
			}else if(val instanceof Long){
				xml += "<"+key+">"+((Long)val).toString()+"</"+key+">";
			}else if(val instanceof Double){
				xml += "<"+key+">"+((Double)val).toString()+"</"+key+">";
			}else if(val instanceof Map){
				xml += this.createXML(key, (Map)val)+"</"+key+">";
			}else if(val instanceof List){
				xml += this.createXML(key, (List)val);
			}
		}
		return (pNode==null?_XML_HEADER:"<"+pNode+attr+">")+xml;
	}
	
	/**
	 * XMLのListデータを作成する
	 * @param list List
	 * @return XML
	 */
	private String createXML(String key, List list){
		String xml = new String();
		for(int i = 0; i < list.size(); i++){
			Object val = list.get(i);
			if(val instanceof String){
				xml +=  "<"+key+">"+(String)val;
			}else if(val instanceof Integer){
				xml +=  "<"+key+">"+((Integer)val).toString();
			}else if(val instanceof Long){
				xml +=  "<"+key+">"+((Long)val).toString();
			}else if(val instanceof Double){
				xml +=  "<"+key+">"+((Double)val).toString();
			}else if(val instanceof Map){
				xml += this.createXML(key, (Map)val);
			}else if(val instanceof List){
				xml += this.createXML(key, (List)val);
			}
			xml += "</"+key+">";
		}
		return xml;
	}
}
