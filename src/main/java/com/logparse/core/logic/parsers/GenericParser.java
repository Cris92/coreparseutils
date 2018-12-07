package com.logparse.core.logic.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.logparse.core.model.GenericParseObject;
import com.logparse.core.model.ParseObject;
import com.logparse.core.model.ParseObjectArianna;
import com.opencsv.CSVWriter;

public class GenericParser implements Parser {

	@Override
	public void getValidString(BufferedReader br, List<ParseObject> listObjects,List<String> words,String lastWord,String separator) throws IOException {

		GenericParseObject parseObject = new GenericParseObject();
		String s = null;
		while ((s = br.readLine()) != null) {
			for (String word : words) {
				if (parseObject == null) {
					parseObject = new GenericParseObject();
				}
				if (s.contains(word)) {
					String[] keyValue = s.replaceAll("\\s", "").split(separator);
					if (keyValue != null && keyValue.length > 1) {
						parseObject.putWords(word,keyValue[1]);
					}
				}
				if (s.startsWith(lastWord)) {
					listObjects.add(parseObject);
					parseObject = null;
				}
			}
		}

	}

	@Override
	public void createCVS(String outputFilePath, List<ParseObject> listObjects,List<String> words) throws IOException {
		File file = new File(outputFilePath);
		FileWriter outputfile = new FileWriter(file);
		CSVWriter writer = new CSVWriter(outputfile);
		List<String> headerList =new ArrayList<String>();
		for(String s:words) {
			headerList.add(s);
		}
		writer.writeNext(headerList.toArray(new String[0]));

		for (ParseObject pGeneric : listObjects) {
			GenericParseObject p = (GenericParseObject) pGeneric;
			List<String> dataList=new ArrayList<String>();
			for(String w:words) {
				dataList.add(p.getWords(w));
			}
			String[] data = dataList.toArray(new String[0]);
			writer.writeNext(data);
		}
		writer.close();
	}
}
