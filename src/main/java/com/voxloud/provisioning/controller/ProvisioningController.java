package com.voxloud.provisioning.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.voxloud.provisioning.service.ProvisioningService;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpStatus.OK;

import org.slf4j.Logger;

@RestController
@RequestMapping("/api/v1")
public class ProvisioningController {

	private static final Logger LOGGER = getLogger(ProvisioningController.class);
	
	@Autowired
	ProvisioningService provisioningService ;

	@GetMapping("/provisioning/{mac-address}")
	public ResponseEntity getProvisioningFile(@PathVariable("mac-address") String macAddress) {
		return new ResponseEntity<>(provisioningService.getProvisioningFile(macAddress), OK);
	}
}