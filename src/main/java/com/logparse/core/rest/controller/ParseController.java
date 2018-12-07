package com.logparse.core.rest.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.logparse.core.logic.LogParser;

@RestController
@RequestMapping(path = "logparse")
public class ParseController {

	@Autowired
	LogParser logParser;

	@RequestMapping(path = "/test")
	public Response test() {
		return Response.ok().build();
	}

	@RequestMapping(path = { "/createCVS/{parserType}" }, method = RequestMethod.POST, consumes = {
			"multipart/form-data" })
	public void createCVS(@RequestParam("file") MultipartFile file, @PathVariable("parserType") String parserType,
			@RequestParam("lastWord") String lastWord, @RequestParam("separator") String separator,
			@RequestParam("wordsNumber") String wordsNumber, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		// Converting sent file to temp stored file
		File convFile = File.createTempFile("logToCvs_" + new Date().getTime(), ".txt");
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();

		List<String> words = new ArrayList<String>();
		if (StringUtils.isNumeric(wordsNumber)) {
			for (int i = 1; i <= Integer.parseInt(wordsNumber); i++) {
                   words.add(request.getParameter("word"+i));
			}
		}
		// Starting of
		String filePath = convFile.getAbsolutePath();
		String returnFilePath = File.createTempFile("logToCvs_" + new Date().getTime(), ".cvs").getAbsolutePath();
		logParser.parseFileToCVS(filePath, returnFilePath, parserType, words, lastWord, separator);
		response.setHeader("Content-Disposition", "attachment; filename=" + convFile.getName());
		File returnFile = new File(returnFilePath);
		org.apache.commons.io.IOUtils.copy(new FileInputStream(returnFile), response.getOutputStream());
		response.flushBuffer();
		returnFile.delete();
		convFile.delete();
	}

}
