# Upload to SFTP after converting JSON to XML  

This example application illustrates the concept of datamapping to convert JSON data to XML. It also shows you how to configure the SFTP connector.

###Example Use Case
In this example JSON data is sent to the mule application through an HTTP end point. This data is then converted to the XML format using the datamapper transformer after which the message payload is uploaded to the SFTP folder. 

###Set up and run the example
1. Import the project into yopur workspace and run the project as a mule application.

2. Make a POST request using Postman to http://localhost:8081 with following JSON message body:

	    {
	    "employees": {
	    "employee": [
	      {
	        "name": "John",
	        "lastName": "Doe",
	        "addresses": {
	          "address": [
	            {
	              "street": "123 Main Street",
	              "zipCode": "111"
	            },
	            {
	              "street": "987 Cypress Avenue",
	              "zipCode": "222"
	            }
	          ]
	        }
	      },
	      {
	        "name": "Jane",
	        "lastName": "Doe",
	        "addresses": {
	          "address": [
	            {
	              "street": "345 Main Street",
	              "zipCode": "111"
	            },
	            {
	              "street": "654 Sunset Boulevard",
	              "zipCode": "333"
	            }
	          ]
	        }
	      }
	    ]
	    }
	    } 
	
3. Verify if the file was uploaded at ***http://ftp-server.demo.solarwinds.com/*** with the following credentials

       login: demo
       password: demo
       folder: Upload

4. The SFTP filename (5e379df0-0bef-11e4-b08a-5c514f927c90.dat) that is generated can be extracted from the log output of studio:

	    INFO  2014-07-15 09:12:27,759 [[data-mapper].connector.http.mule.default.receiver.02] org.mule.transport.sftp.SftpMessageDispatcher: Successfully wrote file '5e379df0-0bef-11e4-b08a-5c514f927c90.dat' to sftp://demo:****@ftp-server.demo.solarwinds.com:22/Upload/

   You can sort by date modified on the web page and click to download the file.
   
### Go further
* Read about the Datamapper Transformer [here](http://www.mulesoft.org/documentation/display/current/Datamapper+User+Guide+and+Reference)
* Read about the Mule SFTP endpoint [here](http://www.mulesoft.org/documentation/display/current/SFTP+Transport+Reference)
