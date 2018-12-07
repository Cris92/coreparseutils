package com.logparse.core.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.logging.impl.Log4jLogger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.logparse.core.property.PropertiesConfiguration;

public class HtmlParseUtils {
	
	static Map<String, String> countryMap;
	static Logger logger=Log4jLogger.getLogger(HtmlParseUtils.class);
	public static int loadChilds(int dbRowId, Map<String, String> fatherList, List<WebElement> childs, BufferedWriter bw2,
			List<String> codes) throws IOException {
		for (WebElement we : childs) {
			List<WebElement> columns = we.findElements(By.tagName("td"));
			String code = columns.get(1).getText();
			if (!StringUtils.isEmpty(columns.get(6).getText())) {
				if (codes.contains(code)) {
					Log4jLogger.getLogger(HtmlParseUtils.class).info("Codice Figlio" + code + " duplicato");
					continue;
				} else {
					codes.add(code);
				}
				// id
				bw2.write(String.valueOf(dbRowId));
				bw2.write(",");
				// name
				bw2.write("\"" + columns.get(2).getText() + "\"");
				// clientid
				bw2.write(",-1,");
				// father id
				bw2.write(fatherList.get(columns.get(6).getText()));
				// isDeleted
				bw2.write(",,");
				// colCode
				bw2.write("\"" + columns.get(1).getText().replace("*", "") + "\"");
				bw2.newLine();
			}
			dbRowId++;
		}
		return dbRowId;
	}

	public static int loadFathers(int dbRowId, Map<String, String> fatherList, List<WebElement> rows, List<WebElement> childs,
			BufferedWriter fathersFileBufferedWriter, List<String> codes) throws IOException {
		for (WebElement we : rows) {
			List<WebElement> columns = we.findElements(By.tagName("td"));
			String code = columns.get(1).getText();
			if (StringUtils.isEmpty(columns.get(6).getText())) {
				if (codes.contains(code)) {
					Log4jLogger.getLogger(HtmlParseUtils.class).info("Codice Padre" + code + " duplicato");
					continue;
				} else {
					codes.add(code);
				}
				// id
				fathersFileBufferedWriter.write(String.valueOf(dbRowId));
				fathersFileBufferedWriter.write(",");
				// name
				fathersFileBufferedWriter.write("\"" + columns.get(2).getText() + "\"");
				// clientid
				fathersFileBufferedWriter.write(",-1,");
				// father id
				fathersFileBufferedWriter.write(countryMap.get(code.substring(0, 2)) != null ? countryMap.get(code.substring(0, 2)) : "");
				// isDeleted
				fathersFileBufferedWriter.write(",,");
				// colCode
				fathersFileBufferedWriter.write("\"" + columns.get(1).getText().replace("*", "") + "\"");

				fathersFileBufferedWriter.newLine();
				fatherList.put(columns.get(1).getText().replace("*", ""), String.valueOf(dbRowId));
			} else {
				childs.add(we);
			}
			dbRowId++;
		}
		return dbRowId;
	}

	public static void loadCountriesIds(){
		String csvFile = PropertiesConfiguration.getCountriesMapping();
		countryMap = new HashMap<String, String>();
		String line = "";
		String cvsSplitBy = ",";

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] country = line.split(cvsSplitBy);

				countryMap.put(country[5].replaceAll("\"", ""), country[0]);

			}

		} catch (Exception e) {
			logger.error("Error retrieving countrycodes ", e);
			e.printStackTrace();
		} 
	}
}
