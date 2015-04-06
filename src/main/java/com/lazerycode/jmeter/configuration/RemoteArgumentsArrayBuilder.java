package com.lazerycode.jmeter.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import com.lazerycode.jmeter.properties.JMeterPropertiesFiles;
import com.lazerycode.jmeter.properties.PropertyContainer;

public class RemoteArgumentsArrayBuilder {

	public List<String> buildRemoteArgumentsArray(Map<JMeterPropertiesFiles, PropertyContainer> masterPropertiesMap){
		if(masterPropertiesMap == null){
			return Collections.emptyList(); 
		}
		
		List<String> result = new ArrayList<String> () ;
		for(Entry<JMeterPropertiesFiles, PropertyContainer> entry : masterPropertiesMap.entrySet()){
			Properties properties = entry.getValue().getFinalPropertyObject(); 
			switch(entry.getKey()){
				case SYSTEM_PROPERTIES : {
					result.addAll(buildTypedPropertiesForContainer(JMeterCommandLineArguments.SYSTEM_PROPERTY, properties)); 
					break; 
				}
				case GLOBAL_PROPERTIES :  {
					result.addAll(buildTypedPropertiesForContainer(JMeterCommandLineArguments.JMETER_GLOBAL_PROP, properties)); 
					break; 
				}
				default : break; 
			}
		}
		return result ; 
	}
	
	private List<String> buildTypedPropertiesForContainer(JMeterCommandLineArguments cmdLineArg, Properties props){ 
		List<String> result = new ArrayList<String> () ;
		for(Entry<Object,Object> e : props.entrySet()){
			if(cmdLineArg == JMeterCommandLineArguments.SYSTEM_PROPERTY){
				result.add(cmdLineArg.getCommandLineArgument()+e.getKey()); 
				result.add(e.getValue().toString());
			}else{
				result.add(cmdLineArg.getCommandLineArgument()+e.getKey()+"="+e.getValue()); 
			}
		}
		return result; 
	}
	
	
}
