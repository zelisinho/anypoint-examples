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
import org.mule.api.config.MuleProperties;
import org.mule.module.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadToSftpAfterConvertingJsonToXmlIT extends FunctionalTestCase
{
	private static final String PATH_TO_TEST_PROPERTIES = "./src/test/resources/mule.test.properties";
	private static final Logger log = LoggerFactory.getLogger(UploadToSftpAfterConvertingJsonToXmlIT.class); 
	
	private static String PASSWORD;
	private static String USER;
	private static String HOST;
	private static String PORT;
	private static String HOME;
	private static String PATH;
	
	private static String MESSAGE = "";
	private static String REPLY = "<?xmlversion=\"1.0\"encoding=\"UTF-8\"?>\n<employees>\n<employee>\n<name>John</name>\n<lastName>Doe</lastName>\n<addresses>\n<address>\n<street>123MainStreet</street>\n<zipCode>111</zipCode>\n</address>\n<address>\n<street>987CypressAvenue</street>\n<zipCode>222</zipCode>\n</address>\n</addresses>\n</employee>\n<employee>\n<name>Jane</name>\n<lastName>Doe</lastName>\n<addresses>\n<address>\n<street>345MainStreet</street>\n<zipCode>111</zipCode>\n</address>\n<address>\n<street>654SunsetBoulevard</street>\n<zipCode>333</zipCode>\n</address>\n</addresses>\n</employee>\n</employees>";
	
    @Override
    protected String getConfigResources()
    {
        return "upload-to-sftp.xml";
    }

    @BeforeClass
    public static void init() throws IOException{
    	final Properties props = new Properties();
    	try {
    	props.load(new FileInputStream(PATH_TO_TEST_PROPERTIES));
    	} catch (Exception e) {
    		log.error("Error occured while reading mule.test.properties", e);
    	}
    	try {
			MESSAGE = FileUtils.readFileToString(new File("./src/test/resources/message.json"));
		} catch (IOException e) {
			e.printStackTrace();
		}    	    
    	USER = props.getProperty("sftp.user");
    	PASSWORD = props.getProperty("sftp.password");
    	PORT = props.getProperty("sftp.port");
    	HOME = props.getProperty("sftp.home");		
    	PATH = props.getProperty("sftp.path");
    	HOST = props.getProperty("sftp.host");
    	
    	System.setProperty("sftp.user", USER);
    	System.setProperty("sftp.password", PASSWORD);
    	System.setProperty("sftp.port", PORT);
    	System.setProperty("sftp.path", PATH);
    	System.setProperty("sftp.host", HOST);
		File dataDirectory = new File(HOME);
		if (dataDirectory.exists()) {
		    FileUtils.deleteDirectory(dataDirectory);
		}
		dataDirectory.mkdirs();    			
    }
    
    

    @Test
    public void testDataMapper() throws Exception
    {
    	
        MuleClient client = new MuleClient(muleContext);
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("http.method", "POST");
        MuleMessage result = client.send("http://0.0.0.0:8081/", MESSAGE, props);
        assertEquals(REPLY, result.getPayloadAsString().replace(" ", ""));
        
    }

    private static final String MAPPINGS_FOLDER_PATH = "./mappings";

	@Override
	protected Properties getStartUpProperties() {
		Properties properties = new Properties(super.getStartUpProperties());

		String pathToResource = MAPPINGS_FOLDER_PATH;
		File graphFile = new File(pathToResource);

		properties.put(MuleProperties.APP_HOME_DIRECTORY_PROPERTY,
				graphFile.getAbsolutePath());

		return properties;
	}	    
}
