package com.voxloud.provisioning.business;

import java.util.Map;

public interface DeviceConfiguration {

	public String generateDeviceConfigurationFile(Map<String, Object> provisioningMap, String overrideFragment) ;
}
