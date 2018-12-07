package com.logparse.core.rest.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.logging.impl.Log4jLogger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logparse.core.property.PropertiesConfiguration;
import com.logparse.core.utils.FileUtils;
import com.logparse.core.utils.HtmlParseUtils;

@RestController
@RequestMapping(path = "htmlparse")
public class HtmlParseController {

	Map<String, String> countryMap;

	private static Logger logger=Log4jLogger.getLogger(HtmlParseController.class);
	@RequestMapping(path = "/isoCodes/CVSHierarchy")
	public Response parseIsoCodesPageCVSHierarchy(@RequestParam("link") String htmlPage,
			@RequestParam("startId") int startId) throws IOException, InterruptedException {
		// Official ISO 3166-2 page ending with /
		// https%3A%2F%2Fwww.iso.org%2Fobp%2Fui%2F%23iso%3Acode%3A3166%3A

		// Creation of driver for web scraping with phantom js
		DesiredCapabilities caps = new DesiredCapabilities();
		caps.setJavascriptEnabled(true);
		caps.setCapability("takesScreenshot", true);

		// Location of phantom js driver
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
				"C:\\Users\\svilu\\Desktop\\phantomjs\\phantomjs.exe");
		WebDriver driver = null;
		//Creation working folders
		File csvFolder = new File(PropertiesConfiguration.getIso3166_2Folder() + "CSV");
		if (!csvFolder.exists()) {
			csvFolder.mkdirs();
		}
		// Loading countries father id
		HtmlParseUtils.loadCountriesIds();

		for (String s : Locale.getISOCountries()) {
			logger.info("Data manipulation for java.Locale " + s);
			// Creation of 2 files, for iso code 3166-2 fathers and childs
			File fathersFile = new File(
					PropertiesConfiguration.getIso3166_2Folder() + "CSV\\" + s + "_ISOCODE_3166-2_Part0.csv");
			File childsFile = new File(
					PropertiesConfiguration.getIso3166_2Folder() + "CSV\\" + s + "_ISOCODE_3166-2_Part1.csv");
			Map<String, String> fatherList = new HashMap<String, String>();

			// Starting the driver and scraping page
			driver = new PhantomJSDriver(caps);
			driver.get(htmlPage + s);
			WebElement table = null;
			synchronized (driver) {
				driver.wait(4000);
			}
			try {
				table = driver.findElement(By.id("subdivision"));
			} catch (Exception e) {
				continue;
			}
			List<WebElement> rows = table.findElements(By.tagName("tr"));
			List<WebElement> childs = new ArrayList<WebElement>();

			// We remove the first row cause is the header row
			rows.remove(0);

			int dbRowId = startId;
			if (fathersFile.createNewFile() && childsFile.createNewFile()) {
				try {

					logger.info("Files created");
					FileOutputStream fathersFileOutputStream = new FileOutputStream(fathersFile);
					BufferedWriter fathersFileBufferedWriter = new BufferedWriter(
							new OutputStreamWriter(fathersFileOutputStream));
					FileOutputStream childsFileOutputStream = new FileOutputStream(childsFile);
					BufferedWriter childsFileBufferedWriter = new BufferedWriter(
							new OutputStreamWriter(childsFileOutputStream));

					List<String> alredyInsertedCodes = new ArrayList<String>();
					logger.info("Identified " + rows.size() + " rows");

					dbRowId = HtmlParseUtils.loadFathers(dbRowId, fatherList, rows, childs, fathersFileBufferedWriter,
							alredyInsertedCodes);
					fathersFileBufferedWriter.close();
					fathersFileOutputStream.close();
					logger.debug("FathersList dimension: " + fatherList.size());
					logger.debug("childsList dimension: " + childs.size());

					if (!childs.isEmpty()) {
						dbRowId = HtmlParseUtils.loadChilds(dbRowId, fatherList, childs, childsFileBufferedWriter,
								alredyInsertedCodes);
						childsFileBufferedWriter.close();
						childsFileOutputStream.close();
					}
				}

				catch (Exception e) {
					Log4jLogger.getLogger(this.getClass()).error("Error: ", e);

					if (driver != null) {
						driver.quit();
					}
					e.printStackTrace();
					return Response.serverError().build();
				}
			}
			if (driver != null) {
				driver.quit();
			}
		}
		return Response.ok().build();

	}

	@RequestMapping(path = "/isoCodes/clearFolder")
	public Response deleteFiles0Size() throws IOException {
		// Cycling on CSV folder to delete 0 size files
		File dir = new File(PropertiesConfiguration.getIso3166_2Folder() + "CSV");
		if (!dir.exists()) {
			dir.createNewFile();
		}
		File[] directoryListing = dir.listFiles();
		List<String> deletedFiles = new ArrayList<String>();
		List<String> keepedFiles = new ArrayList<String>();
		Map<String, List<String>> returnedEntity = new HashMap<String, List<String>>();
		if (directoryListing != null) {

			for (File child : directoryListing) {
				if (child.length() == 0) {
					logger.debug("Deleted file: " + child.getName());
					deletedFiles.add(child.getName());
					child.delete();
				} else {
					keepedFiles.add(child.getName());
					logger.debug("Keeped file: " + child.getName());
				}
			}
		}
		returnedEntity.put("Deleted files", deletedFiles);
		returnedEntity.put("Keeped Files", keepedFiles);
		return Response.ok(returnedEntity).build();
	}

	@RequestMapping(path = "/isoCodes/joincsv")
	public Response loadCSV() throws IOException {
		File newFile = new File(PropertiesConfiguration.getIso3166_2Folder() + "countries_joined.csv");
		File dir = new File(PropertiesConfiguration.getIso3166_2Folder() + "CSV");
		File[] directoryListing = dir.listFiles();
		OutputStream output = new BufferedOutputStream(new FileOutputStream(newFile, true));
		try {
			if (directoryListing != null) {
				for (File child : directoryListing) {
					FileUtils.appendFile(output, child);
				}
			}
		} catch (Exception e) {
			logger.error("Error on file: ", e);
		} finally {
			IOUtils.closeQuietly(output);
		}
		return Response.ok().build();
	}

	@RequestMapping(path = "/isoCodes/updateComuniFatherId")
	public void updateRegionsId() throws IOException {

		// File with second layer of iso 3166-2
		File f = new File(PropertiesConfiguration.getIso3166_2Folder() + "CSV\\IT_ISOCODE_3166-2_Part1.csv");
		// File with name,id of subdivisions
		File f2 = new File(PropertiesConfiguration.getProvinceMapping());
		// Destination file of queries of update
		File f3 = new File(PropertiesConfiguration.getIso3166_2Folder() + "updatefathercomuni.sql");
		if (!f3.exists()) {
			f3.createNewFile();
		}
		BufferedReader br = null;
		BufferedReader br2 = null;
		Map<String, String> provinceId = new HashMap<String, String>();
		FileOutputStream fos = new FileOutputStream(f3);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		try {

			String line = null;
			br = new BufferedReader(new FileReader(f));
			while ((line = br.readLine()) != null) {
				String[] provinciaNew = line.split(",");
				provinceId.put(provinciaNew[1].replaceAll("\"", ""), provinciaNew[0]);
			}

			br2 = new BufferedReader(new FileReader(f2));
			while ((line = br2.readLine()) != null) {
				// use comma as separator
				String[] provincia = line.split(",");
				bw.write("/*" + provincia[1] + "*/update ocaryna.lists set father_id=" + provinceId.get(provincia[1])
						+ " where father_id=" + provincia[0] + ";");
				bw.newLine();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			br.close();
			br2.close();
			bw.close();
		}

	}

}
