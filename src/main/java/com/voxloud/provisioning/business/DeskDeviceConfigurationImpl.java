package com.voxloud.provisioning.business;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.voxloud.provisioning.common.exception.GenericServerException;

@Service(value="deskDeviceConfiguration")
public class DeskDeviceConfigurationImpl implements DeviceConfiguration {

	private static final Logger LOGGER = getLogger(DeskDeviceConfigurationImpl.class);

	@Override
	public String generateDeviceConfigurationFile(Map<String, Object> provisioningMap, String overrideFragment) {
		
		String returnStr = new String() ;
		
		try
		{
			// Below code added to check for Override fragment
	    	
	    	if(StringUtils.isNotBlank(overrideFragment)) {
	    		Map<String, Object> overrideFragmentMap = new HashMap<>() ;
	    		overrideFragmentMap = Arrays.stream(overrideFragment.split("\n"))
							    .map(s -> s.split("="))
							    .collect(Collectors.toMap(
							        a -> a[0],  
							        a -> a[1]   
							    ));
						provisioningMap.putAll(overrideFragmentMap);
			}
	    	
	    	returnStr = convertMapToPropertyFile(provisioningMap) ;
	    	
		} catch (Exception e) {
			LOGGER.error("Some error occured while converting override fragment to property : " + e.getMessage(), e);
			throw new GenericServerException("Some error occured while converting override fragment to property : " + e.getMessage()) ;
		}
		
		return returnStr;
	}
	
	private String convertMapToPropertyFile(Map<String, Object> provisioningMap) {
    	StringBuffer returnStr = new StringBuffer();
    	
    	try {
    		provisioningMap.forEach((k, v) -> returnStr.append(k.toString() + "=" + v.toString() + System.lineSeparator()));
		} catch (Exception e) {
			LOGGER.error("Some error occured while converting response to properties : " + e.getMessage(), e);
			throw new GenericServerException("Some error occured while converting response to properties : " + e.getMessage()) ;
		}
    	return returnStr.toString() ;
    }
}
