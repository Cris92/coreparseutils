package com.logparse.core.logic.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.logparse.core.model.ParseObject;
import com.logparse.core.model.ParseObjectArianna;
import com.opencsv.CSVWriter;

public class AriannaParser implements Parser {

	@Override
	public void getValidString(BufferedReader br, List<ParseObject> listObjects,List<String> words,String lastWord,String separator) throws IOException {

		ParseObjectArianna parseObject = new ParseObjectArianna();
		String s = null;
		while ((s = br.readLine()) != null) {
			if (parseObject == null) {
				parseObject = new ParseObjectArianna();
			}
			if (s.startsWith("Launching")) {
				continue;
			}
			if (s.startsWith("platenumbernat")) {
				String[] keyValue = s.replaceAll("\\s", "").split("-");
				if (keyValue != null && keyValue.length > 1) {
					parseObject.setPlateNumberNat(keyValue[1]);
				}
			}
			if (s.startsWith("obuid")) {
				String[] keyValue = s.replaceAll("\\s", "").split("-");
				if (keyValue != null && keyValue.length > 1) {
					parseObject.setObuid(keyValue[1]);
				}
			}
			if (s.startsWith("plate")) {
				String[] keyValue = s.replaceAll("\\s", "").split("-");
				if (keyValue != null && keyValue.length > 1) {
					parseObject.setPlate(keyValue[1]);
				}
			}
			if (s.startsWith("time")) {
				String[] keyValue = s.replaceAll("\\s", "").split("-");
				String dateString = null;
				if (keyValue != null && keyValue.length > 1) {
					dateString = keyValue[1];
				}
				if (dateString.length() >= 14) {
					String year = dateString.substring(0, 4);
					String month = dateString.substring(4, 6);
					String day = dateString.substring(6, 8);
					String hours = dateString.substring(8, 10);
					String minutes = dateString.substring(10, 12);
					String seconds = dateString.substring(12, 14);

					parseObject.setTime(day + "/" + month + "/" + year + " " + hours + ":" + minutes + ":" + seconds);
				} else {
					parseObject.setTime(dateString);
				}
			}
			if (s.startsWith("tbaid")) {
				String[] keyValue = s.replaceAll("\\s", "").split("-");
				if (keyValue != null && keyValue.length > 1) {
					parseObject.setTbaid(keyValue[1]);
				}
			}
			if (s.startsWith("guid")) {
				String[] keyValue = s.replaceAll("\\s", "").split("-");
				if (keyValue != null && keyValue.length > 1) {
					parseObject.setGuid(keyValue[1]);
				}
			}
			if (s.startsWith("response")) {
				String[] keyValue = s.replaceAll("\\s", "").split(":");
				if (keyValue != null && keyValue.length > 1) {
					parseObject.setResponse(keyValue[1]);
				}
			}
			if (s.startsWith("Finished")) {
				if (parseObject.getResponse() != null
						&& parseObject.getResponse().replaceAll("\\s+", "").equalsIgnoreCase("000")) {
					listObjects.add(parseObject);
				}
				parseObject = null;
			}
		}

	}

	@Override
	public void createCVS(String outputFilePath,List<ParseObject> listObjects,List<String> words) throws IOException {
		File file = new File(outputFilePath);
		FileWriter outputfile = new FileWriter(file);
		CSVWriter writer = new CSVWriter(outputfile);
		String[] header = { "Plate Number Nat", "Obuid", "Plate", "Tbaid", "Activation sent (GMT +0)", "Guid" };
		writer.writeNext(header);
		
		for (ParseObject pGeneric : listObjects) {
			ParseObjectArianna p=(ParseObjectArianna)pGeneric;
			String[] data = { p.getPlateNumberNat(), p.getObuid(), p.getPlate(), p.getTbaid(), p.getTime(),
					p.getGuid() };
			writer.writeNext(data);
		}
		writer.close();
	}
}
