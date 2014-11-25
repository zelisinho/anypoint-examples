/**
 * MuleSoft Examples
 * Copyright [2014] MuleSoft, Inc.
 *
 * This product includes software developed at
 * MuleSoft, Inc. (http://www.mulesoft.com/).
 */

package org.mule.examples;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.config.MuleProperties;
import org.mule.module.client.MuleClient;
import org.mule.processor.chain.SubflowInterceptingChainLifecycleWrapper;
import org.mule.tck.junit4.FunctionalTestCase;
import org.mule.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatamapperWithFlowRefIT extends FunctionalTestCase
{
	private static final String PATH_TO_TEST_PROPERTIES = "./src/test/resources/mule.test.properties";
	private static final Logger log = LoggerFactory.getLogger(DatamapperWithFlowRefIT.class); 
	
	private static final String MAPPINGS_FOLDER_PATH = "./mappings";
	private final String COMPANY_NAME = "Universal Exports";
	private final String COMPANY_NAME1 = "Best Widgets";
	private static List<String> accountIds = new ArrayList<String>();
	
    @Override
    protected String getConfigResources()
    {
        return "datamapper-with-flowref.xml,./src/test/resources/testflows/test-flows.xml";
    }

    @BeforeClass
	public static void init() {
    	final Properties props = new Properties();
    	try {
    	props.load(new FileInputStream(PATH_TO_TEST_PROPERTIES));
    	} catch (Exception e) {
    		log.error("Error occured while reading mule.test.properties", e);
    	}    	
    	System.setProperty("sfdc.user", props.getProperty("sfdc.user"));
		System.setProperty("sfdc.password", props.getProperty("sfdc.password"));
		System.setProperty("sfdc.securityToken", props.getProperty("sfdc.securityToken"));		
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
    public void importAccounts() throws Exception
    {
    	
        MuleClient client = new MuleClient(muleContext);
        String fileInputPath = "file://./src/main/resources/input";
        String payload = IOUtils.getResourceAsString(
                "companies.csv", this.getClass());
        client.dispatch(fileInputPath, payload, null);

        Thread.sleep(15000);
        
        Map<String, Object> account = getAccount(COMPANY_NAME);
        assertEquals("South East", account.get("Region__c"));
        getAccount(COMPANY_NAME1);        
    }
    
    private Map<String, Object> getAccount(String name) throws MuleException, Exception{
    	SubflowInterceptingChainLifecycleWrapper select = getSubFlow("selectAccountFromSalesforce");        										
    	select.initialise();
        MuleEvent response = select.process(getTestEvent(name, MessageExchangePattern.REQUEST_RESPONSE));        
        Map<String, Object> account = (Map<String, Object>)response.getMessage().getPayload();        
        accountIds.add(account.get("Id").toString());
        return account;
    }

    @After
    public void tearDown() {
    	SubflowInterceptingChainLifecycleWrapper delete = getSubFlow("deleteAccountFromSalesforce");
        try {
			delete.initialise();
			delete.process(getTestEvent(accountIds, MessageExchangePattern.REQUEST_RESPONSE));			
		} catch (Exception e) {
			e.printStackTrace();
		}
        		          
    }
}
