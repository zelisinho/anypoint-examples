/**
 * MuleSoft Examples
 * Copyright [2014] MuleSoft, Inc.
 *
 * This product includes software developed at
 * MuleSoft, Inc. (http://www.mulesoft.com/).
 */

package org.mule.examples;

import static org.junit.Assert.assertEquals;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RestApiWithApiKitTest extends FunctionalTestCase
{

    private static String MESSAGE = "teams/BAR";
    private static String REPLY;
    
    @Override
    protected String getConfigResources()
    {
        return "rest-api-with-apikit.xml";
    }

    @Override
	protected Properties getStartUpProperties() {
		Properties properties = new Properties(super.getStartUpProperties());
		properties.put(MuleProperties.APP_HOME_DIRECTORY_PROPERTY,
				"./src/main/api/");

		return properties;
	}

    @Before
    public void init(){
    	try {
			REPLY = FileUtils.readFileToString(new File("./src/test/resources/reply.json"));			
		} catch (IOException e) {
			e.printStackTrace();
		}    	
    }
    
    @Test
    public void httpToGetRestApi() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("http.method", "GET");
        MuleMessage result = client.send("http://localhost:8081/api/" + MESSAGE, "", props);
        assertNotNull(result);
        assertFalse(result.getPayload() instanceof NullPayload);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode tree1 = mapper.readTree(REPLY);
        JsonNode tree2 = mapper.readTree(result.getPayloadAsString());
        assertTrue(tree1.equals(tree2));
        
        props.put("http.method", "DELETE");
        result = client.send("http://localhost:8081/api/" + MESSAGE, "", props);
        assertNotNull(result);
        assertFalse(result.getPayload() instanceof NullPayload);
        assertEquals("204", result.getInboundProperty("http.status"));
        
    }

    
}
