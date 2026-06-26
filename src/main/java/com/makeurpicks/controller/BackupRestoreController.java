package com.makeurpicks.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.makeurpicks.dto.DatabaseBackupDto;
import com.makeurpicks.service.BackupRestoreService;

@RestController
@RequestMapping("/admin/backup")
@PreAuthorize("hasRole('ADMIN')")
public class BackupRestoreController {

	@Autowired
	private BackupRestoreService backupRestoreService;

	@RequestMapping(value = "/export", method = RequestMethod.GET)
	public ResponseEntity<byte[]> exportDatabase() {
		try {
			DatabaseBackupDto backup = backupRestoreService.exportDatabase();
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			
			// Format nicely so the backup is human-readable and pretty
			byte[] jsonBytes = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(backup);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setContentDispositionFormData("attachment", "myp_backup.json");
			headers.setContentLength(jsonBytes.length);

			return new ResponseEntity<>(jsonBytes, headers, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/import", method = RequestMethod.POST)
	public ResponseEntity<String> importDatabase(@RequestParam("file") MultipartFile file) {
		if (file == null || file.isEmpty()) {
			return new ResponseEntity<>("Uploaded file is empty or missing", HttpStatus.BAD_REQUEST);
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			
			DatabaseBackupDto backup = mapper.readValue(file.getBytes(), DatabaseBackupDto.class);
			backupRestoreService.importDatabase(backup);
			return new ResponseEntity<>("Database successfully restored", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Failed to restore database: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
