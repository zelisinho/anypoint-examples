/**
 * MuleSoft Examples
 * Copyright 2014 MuleSoft, Inc.
 *
 * This product includes software developed at
 * MuleSoft, Inc. (http://www.mulesoft.com/).
 */

package org.mule.examples;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;

public class SalesforceIdRetrievalIT extends FunctionalTestCase {

	private static final String PARAMS = "?object=user&searchKey=name&searchValue=mule";
	private static final String TEST_DIR = "./src/test/resources";
	private static String REPLY;
	
	@BeforeClass
	public static void init() throws IOException{
		REPLY = FileUtils.readFileToString(new File(TEST_DIR + "/reply.txt")).replace("\n", "");
		
		Properties props = new Properties();
		props.load(new FileInputStream(TEST_DIR + "/mule.test.properties"));
		
		System.setProperty("sfdc.username", props.getProperty("sfdc.username"));
    	System.setProperty("sfdc.securityToken", props.getProperty("sfdc.securityToken"));
    	System.setProperty("sfdc.password", props.getProperty("sfdc.password"));

	}
	
	@Override
    protected String getConfigResources()
    {
        return "salesforce-id-retrieval.xml";
    }
	
	@Test
	public void testGetData() throws Exception {
		MuleClient client = new MuleClient(muleContext);
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("http.method", "GET");
        MuleMessage result = client.send("http://localhost:8081/" + PARAMS, "", props);
        assertEquals(result.getPayloadAsString(), REPLY);
	}
}
