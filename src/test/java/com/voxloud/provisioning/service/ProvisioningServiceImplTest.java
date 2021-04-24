package com.voxloud.provisioning.service;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.voxloud.provisioning.business.DeviceConfiguration;
import com.voxloud.provisioning.common.exception.NotFoundException;
import com.voxloud.provisioning.entity.Device;
import com.voxloud.provisioning.entity.DeviceModel;
import com.voxloud.provisioning.repository.DeviceRepository;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class ProvisioningServiceImplTest {

	@MockBean
	private DeviceRepository deviceRepository ;
	
	@Autowired
	@Qualifier("conferenceDeviceConfiguration")
	private DeviceConfiguration conferenceDeviceConfiguration ;
	
	@Autowired
	@Qualifier("deskDeviceConfiguration")
	private DeviceConfiguration deskDeviceConfiguration ;
	
	@Autowired
	private ProvisioningService provisioningService ;
	
	@Test(expected = NotFoundException.class)
	public void testDeviceNotFoundWithException() {
		String macAddress = "aa-bb-cc-dd-ee-ffff" ;
		Mockito.when(deviceRepository.findById(macAddress)).thenThrow(new NotFoundException("Device not found with given mac-address : " + macAddress)) ;
		String returnStr = provisioningService.getProvisioningFile(macAddress) ;
	}
	
    @Test
	public void testDeskDeviceConfigurationFileWithoutOverrideFragment() {
		String macAddress = "aa-bb-cc-dd-ee-ff" ;
		Device deviceObj = new Device() ;
		deviceObj.setMacAddress("aa-bb-cc-dd-ee-ff");
		deviceObj.setUsername("john");
		deviceObj.setPassword("doe");
		deviceObj.setModel(DeviceModel.DESK);
		Optional<Device> device = Optional.of(deviceObj) ;
		
		Mockito.when(deviceRepository.findById(macAddress)).thenReturn(device) ;
		
		String returnStr = provisioningService.getProvisioningFile(macAddress) ;
		assertTrue(StringUtils.isNotBlank(returnStr) && returnStr.contains("username"));
		assertTrue(StringUtils.isNotBlank(returnStr) && !returnStr.contains("timeout"));
	}
    
    @Test
	public void testDeskDeviceConfigurationFileWithOverrideFragment() {
		String macAddress = "aa-bb-cc-dd-ee-ff" ;
		Device deviceObj = new Device() ;
		deviceObj.setMacAddress("aa-bb-cc-dd-ee-ff");
		deviceObj.setUsername("john");
		deviceObj.setPassword("doe");
		deviceObj.setOverrideFragment("domain=sip.anotherdomain.com\nport=5161\ntimeout=10");
		deviceObj.setModel(DeviceModel.DESK);
		Optional<Device> device = Optional.of(deviceObj) ;
		
		Mockito.when(deviceRepository.findById(macAddress)).thenReturn(device) ;
		
		String returnStr = provisioningService.getProvisioningFile(macAddress) ;
		assertTrue(StringUtils.isNotBlank(returnStr) && returnStr.contains("username"));
		assertTrue(StringUtils.isNotBlank(returnStr) && returnStr.contains("timeout"));
	}
    
    @Test
	public void testConferenceDeviceConfigurationFileWithOverrideFragment() throws JSONException {
		String macAddress = "1a-2b-3c-4d-5e-6f" ;
		Device deviceObj = new Device() ;
		deviceObj.setMacAddress("1a-2b-3c-4d-5e-6f");
		deviceObj.setUsername("eric");
		deviceObj.setPassword("blue");
		deviceObj.setOverrideFragment("{\"domain\":\"sip.anotherdomain.com\",\"port\":\"5161\",\"timeout\":10}");
		deviceObj.setModel(DeviceModel.CONFERENCE);
		Optional<Device> device = Optional.of(deviceObj) ;
		
		Mockito.when(deviceRepository.findById(macAddress)).thenReturn(device) ;
		String expectedJSONStr = "{\"password\":\"blue\",\"port\":\"5161\",\"domain\":\"sip.anotherdomain.com\",\"codecs\":[\"G711\",\"G729\",\"OPUS\"],\"timeout\":10,\"username\":\"eric\"}" ;
		
		String returnStr = provisioningService.getProvisioningFile(macAddress) ;
		assertTrue(StringUtils.isNotBlank(returnStr) && returnStr.contains("username"));
		assertTrue(StringUtils.isNotBlank(returnStr) && returnStr.contains("timeout"));
		JSONAssert.assertEquals(expectedJSONStr, returnStr, true);
	}
}
