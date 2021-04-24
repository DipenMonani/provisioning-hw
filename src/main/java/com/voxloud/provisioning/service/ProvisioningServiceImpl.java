package com.voxloud.provisioning.service;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.voxloud.provisioning.business.DeviceConfiguration;
import com.voxloud.provisioning.common.exception.BadRequestException;
import com.voxloud.provisioning.common.exception.NotFoundException;
import com.voxloud.provisioning.entity.Device;
import com.voxloud.provisioning.entity.DeviceModel;
import com.voxloud.provisioning.repository.DeviceRepository;

@Service
public class ProvisioningServiceImpl implements ProvisioningService {

	private static final Logger LOGGER = getLogger(ProvisioningServiceImpl.class);
	
	@Autowired
	private DeviceRepository deviceRepository ;

	@Value("${provisioning.domain}")
	private String provisioningDomain ;
	
	@Value("${provisioning.port}")
	private String provisioningPort ;
	
	@Value("${provisioning.codecs}")
	private String provisioningCodecs ;
	
	@Autowired
	@Qualifier("conferenceDeviceConfiguration")
	private DeviceConfiguration conferenceDeviceConfiguration ;
	
	@Autowired
	@Qualifier("deskDeviceConfiguration")
	private DeviceConfiguration deskDeviceConfiguration ;
	
    public String getProvisioningFile(String macAddress) {
        
    	if(StringUtils.isBlank(macAddress)) {
    		throw new BadRequestException("mac-address is missing in request") ;
    	}

    	Device device = deviceRepository.findById(macAddress).orElseThrow(() -> new NotFoundException("Device not found with given mac-address : " + macAddress)) ;
    	Map<String, Object> provisioningMap = new HashMap<>() ;
    	String returnStr = new String();		
    	
    	provisioningMap.put("username", device.getUsername()) ;
    	provisioningMap.put("password", device.getPassword()) ;	
    	provisioningMap.put("domain", provisioningDomain) ;
    	provisioningMap.put("port", provisioningPort) ;
    	provisioningMap.put("codecs", provisioningCodecs);
    	
    	// Below code added for managing return string type based on device model
    	
    	if(device.getModel() != null && device.getModel() == DeviceModel.CONFERENCE)
    	{
    		return conferenceDeviceConfiguration.generateDeviceConfigurationFile(provisioningMap, device.getOverrideFragment()) ;
    	} else if(device.getModel() != null && device.getModel() == DeviceModel.DESK)
    	{
    		return deskDeviceConfiguration.generateDeviceConfigurationFile(provisioningMap, device.getOverrideFragment()) ;
    	} else {
    		// TODO default implementation of map conversion
    	}
    	
    	return returnStr;
    }
    
    
    
    
}
