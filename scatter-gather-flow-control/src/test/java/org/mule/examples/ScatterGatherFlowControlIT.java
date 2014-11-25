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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;
import org.mule.transport.NullPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.mail.imap.IMAPFolder;

public class ScatterGatherFlowControlIT extends FunctionalTestCase
{
	private static final String PATH_TO_TEST_PROPERTIES = "./src/test/resources/mule.test.properties";
	private static final Logger log = LoggerFactory.getLogger(ScatterGatherFlowControlIT.class); 
	
	private static final String REPLY1 = "a=3";
	private static final String REPLY2 = "b=4";
	private static String USER;
	private static String USER_ENC;
	private static String PASSWORD;
	private static String HOST;
	private String MESSAGE = "{ \"a\": 3, \"b\": 4 }";    
    
    @Override
    protected String getConfigResources()
    {
        return "scatter-gather.xml";
    }

    @BeforeClass
	public static void init() {
    	final Properties props = new Properties();
    	try {
    		props.load(new FileInputStream(PATH_TO_TEST_PROPERTIES));
    	} catch (Exception e) {
    		log.error("Error occured while reading mule.test.properties", e);
    	}
    	
    	PASSWORD = props.getProperty("smtp.password");    	
    	USER = props.getProperty("smtp.user");
    	USER_ENC = props.getProperty("smtp.user_enc");
    	HOST = props.getProperty("imap.host");
    	
    	System.setProperty("smtp.host", props.getProperty("smtp.host"));
    	System.setProperty("smtp.port", props.getProperty("smtp.port"));
    	System.setProperty("smtp.user", USER_ENC);
    	System.setProperty("smtp.password", PASSWORD);
    	System.setProperty("mail.to", USER);
    	System.setProperty("mail.from", USER);
    	System.setProperty("mail.subject", "Mule flow completed!");
    	
    }
    
    @Test
    public void httpPostToFlowUrlSentMessage() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("http.method", "POST");
        MuleMessage result = client.send("http://localhost:8081/", MESSAGE, props);
        Thread.sleep(10000);
        assertNotNull(result);
        assertFalse(result.getPayload() instanceof NullPayload);
        assertEquals("200", result.getInboundProperty("http.status"));
        String emailContent = deleteSentEmail().trim();
        Assert.assertTrue(emailContent.contains(REPLY1) && emailContent.contains(REPLY2));
    }
                   
    private String deleteSentEmail() throws MessagingException, IOException{
    	IMAPFolder folder = null;
        Store store = null;
        try 
        {
          Properties props = System.getProperties();
          props.setProperty("mail.store.protocol", "imaps");

          Session session = Session.getDefaultInstance(props, null);

          store = session.getStore("imaps");
          store.connect(HOST, USER, PASSWORD);

          folder = (IMAPFolder) store.getFolder("inbox");


          if(!folder.isOpen())
        	  folder.open(Folder.READ_WRITE);
          Message[] messages = folder.getMessages();
          if (messages.length > 0){
        	  messages[messages.length - 1].setFlag(Flags.Flag.DELETED, true);
        	  return messages[messages.length - 1].getContent().toString();
          }
          else{
        	  return null;
          }
        }
        finally 
        {
          if (folder != null && folder.isOpen()) { folder.close(true); }
          if (store != null) { store.close(); }
        }
    }
}
