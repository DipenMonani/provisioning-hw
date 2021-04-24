package com.voxloud.provisioning.business;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voxloud.provisioning.common.exception.GenericServerException;

@Service(value = "conferenceDeviceConfiguration")
public class ConferenceDeviceConfigurationImpl implements DeviceConfiguration {

	private static final Logger LOGGER = getLogger(ConferenceDeviceConfigurationImpl.class);
	
	@Override
	public String generateDeviceConfigurationFile(Map<String, Object> provisioningMap, String overrideFragment) {
		String returnStr = new String() ;

		try
		{
			if(provisioningMap != null && StringUtils.isNotBlank(provisioningMap.get("codecs").toString())) {
				provisioningMap.put("codecs", Stream.of(provisioningMap.get("codecs").toString().split(",")).collect(Collectors.toList()));
			}
			
	    	// Below code added to check for Override fragment
	    	
	    	if(StringUtils.isNotBlank(overrideFragment)) {
	    		ObjectMapper mapper = new ObjectMapper();
	    		Map<String, Object> overrideFragmentMap = new HashMap<>() ;
	    		
	    		overrideFragmentMap = mapper.readValue(overrideFragment, new TypeReference<Map<String, Object>>() {});
				provisioningMap.putAll(overrideFragmentMap);
	        }	
			
			returnStr = convertMapToJSONFile(provisioningMap) ;

		} catch(Exception e) {
			LOGGER.error("Some error occured while converting override fragment to json : " + e.getMessage(), e);
			throw new GenericServerException("Some error occured while converting override fragment to json : " + e.getMessage()) ;
		}

		return returnStr ;
	}

	private String convertMapToJSONFile(Map<String, Object> provisioningMap) {
    	String returnStr = new String();
    	ObjectMapper mapper = new ObjectMapper();
    	try {
			returnStr = mapper.writeValueAsString(provisioningMap) ;
		} catch (JsonProcessingException e) {
			LOGGER.error("Some error occured while converting response to json : " + e.getMessage(), e);
			throw new GenericServerException("Some error occured while converting response to json : " + e.getMessage()) ;
		}
    	return returnStr ;
    }
}
