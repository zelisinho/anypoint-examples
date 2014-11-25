/**
 * MuleSoft Examples
 * Copyright [2014] MuleSoft, Inc.
 *
 * This product includes software developed at
 * MuleSoft, Inc. (http://www.mulesoft.com/).
 */

package org.mule.examples;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.MuleMessage;
import org.mule.api.config.MuleProperties;
import org.mule.module.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;
import org.mule.transport.NullPayload;

public class XMLWithSoapIT extends FunctionalTestCase
{
	private String MESSAGE;
	private String REPLY1 = "<runningTotal>500</runningTotal>";
	private String REPLY2 = "<status>ADMITTED</status>";
	
	private static final String MAPPINGS_FOLDER_PATH = "./mappings";
	
	 @Override
	 protected String getConfigResources()
	 {
	  return "Hospital_Admissions_SOA.xml,mocks.xml";
	 }
	
	 @Before
	 public void setUp(){
		 try {
			 MESSAGE = FileUtils.readFileToString(new File("./src/test/resources/message.xml"));			 
		} catch (IOException e) {
			e.printStackTrace();
		}
	 }
	
	@Override
	protected Properties getStartUpProperties() {
		Properties properties = new Properties(super.getStartUpProperties());

		String pathToResource = MAPPINGS_FOLDER_PATH;
		File graphFile = new File(pathToResource);

		properties.put(MuleProperties.APP_HOME_DIRECTORY_PROPERTY,
				graphFile.getAbsolutePath());

		return properties;
	}
	 
	 @Test
	 public void testXMLwithSoap() throws Exception
	 {
		  MuleClient client = new MuleClient(muleContext);
		  Map<String, Object> props = new HashMap<String, Object>();
		  props.put("http.method", "POST");
		  MuleMessage result = client.send("http://localhost:8081/AdmissionService", MESSAGE, props);
		  assertNotNull(result);
		  assertFalse(result.getPayload() instanceof NullPayload);
		  assertTrue(result.getPayloadAsString().contains(REPLY1));
		  assertTrue(result.getPayloadAsString().contains(REPLY2));
	 }

}
