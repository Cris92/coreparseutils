package com.logparse.core.logic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.logparse.core.logic.parsers.Parser;
import com.logparse.core.model.ParseObject;


@Component
public class LogParser {

	@Autowired
	private ApplicationContext context;
	 
	public void parseFileToCVS(String inputFilePath, String outputFilePath,String parserType,List<String> words,String lastWord,String separator) {
		
		
		@SuppressWarnings("resource")
		Parser parser=(Parser)context.getBean(parserType);
		BufferedReader br = null;
		FileReader fr = null;
		

		List<ParseObject> listObjects=new ArrayList<ParseObject>();
		try {

			fr = new FileReader(inputFilePath);
			br = new BufferedReader(fr);
		    parser.getValidString(br,listObjects,words,lastWord,separator);
		    parser.createCVS(outputFilePath,listObjects,words);
			
			

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

}
