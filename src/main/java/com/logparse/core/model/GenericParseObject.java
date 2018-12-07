package com.logparse.core.model;

import java.util.HashMap;
import java.util.Map;

public class GenericParseObject extends ParseObject {
	private Map<String, String> words;

	public GenericParseObject() {
		words=new HashMap<String,String>();
	}

	public String getWords(String key) {
		return words.get(key);
	}

	public void putWords(String key, String value) {
		this.words.put(key, value);
	}
}
