package com.jingfm.api.builder;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class JSONBuilder<T> {
	
	protected String root;
	
	public JSONBuilder(){
		root = "";
	}
	
	/**
	 * Sets root of the JSON params, useful when building object in joins
	 * 
	 * @param root
	 */
	public void setRoot(String root){
		this.root = root;
	}
	
	public abstract T build(JSONObject jsonObject) throws JSONException;
	public abstract T buildError(JSONObject jsonObject) throws JSONException;
}
