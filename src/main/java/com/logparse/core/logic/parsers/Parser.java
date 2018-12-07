package com.logparse.core.logic.parsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import com.logparse.core.model.ParseObject;

public interface Parser {
	
	abstract void getValidString(BufferedReader reader,List<ParseObject> listObjects,List<String> words,String lastWord,String separator) throws IOException;
	public void createCVS(String outputFilePath,List<ParseObject> listObjects,List<String> words) throws IOException;

}
