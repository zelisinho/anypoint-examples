#Adding a new customer to Workday Revenue Management Example

The Workday Connector facilitates connections between Mule integration applications and Workday by allowing you to connect to a Workday database.

### Assumptions ###

This document assumes that you are familiar with Mule and the [Anypoint™ Studio interface](http://www.mulesoft.org/documentation/display/current/Anypoint+Studio+Essentials). To increase your familiarity with Studio, consider completing one or more [Anypoint Studio Tutorials](http://www.mulesoft.org/documentation/display/current/Basic+Studio+Tutorial). Further, this example assumes that you have a basic understanding of [Mule flows](http://www.mulesoft.org/documentation/display/current/Mule+Application+Architecture), [Mule Global Elements](http://www.mulesoft.org/documentation/display/current/Global+Elements), and Studio's [Anypoint DataMapper](http://www.mulesoft.org/documentation/display/current/Datamapper+User+Guide+and+Reference). 

This document describes the details of the example within the context of Anypoint Studio, Mule ESB’s graphical user interface.

### Example Use Case ###

Though a simple example, this application nonetheless employs complex functionality to demonstrate a basic use case. The application accepts a piece of XML code which contains customer information –  customer name, status and category – and creates a customer record in a Workday instance, automatically inserting the correct data into each field. 

### Set Up and Run the Example ###

Complete the following procedure to create, then run this example in your own instance of Anypoint Studio.

1. [Create](http://www.mulesoft.org/documentation/display/current/Mule+Examples#MuleExamples-CreateandRunExampleApplications) the example application in Anypoint Studio. *Do not run the application*.
1. In your application in Studio, click the **Global Elements** tab. 
1. Double-click the Workday Revenue Management global element to open its **Global Element Properties** panel. 
1. Change the contents of the **user**, **password** and **endpoint** fields to your account-specific values as follows:

		user						<USER>@<DOMAIN>
		password					<PASSWORD>
		endpoint					<ENDPOINT_URL>
 
4. Then click **OK** to save your changes. 
1. In the **Package Explorer**, right-click the adding-a-new-customer-to-workday-revenue-management project name, then select **Run As > Mule Application**. Studio runs the application on the embedded server.  
1. Make an HTTP POST request to *http://localhost:9090/* with the request body as follows:

		<?xml version="1.0" encoding="UTF-8"?>
		<root>
			<Account>
				<CustomerName>John Doe</CustomerName>
				<BusinessEntityName>John Doe</BusinessEntityName>
				<Customer_Category_Reference_Type>Customer_Category_ID</Customer_Category_Reference_Type>
				<Customer_Category_Reference_Value>CUSTOMER_CATEGORY-5</Customer_Category_Reference_Value>
				<Customer_Status_Reference_Type>Business_Entity_Status_Value_ID</Customer_Status_Reference_Type>
				<Customer_Status_Reference_Value>ACTIVE</Customer_Status_Reference_Value>
			</Account>
		</root>

	To send this request, use a browser extension such as [Advanced Rest Client](https://chrome.google.com/webstore/detail/advanced-rest-client/hgmloofddffdnphfgcellkdfbfbjeloo) (Google Chrome), or the [curl](http://curl.haxx.se/) command line utility. 
8. Login to your Workday account.
9. Navigate to View Customer Report and enter John Doe in the search field.
10. Verify that the customer was added.

### How it Works ###

Using a single flow with four elements, this application accepts XML with the customer information, then uploads a customer to Workday. 

The [HTTP endpoint](http://www.mulesoft.org/documentation/display/current/HTTP+Connector) listens to POST requests at the predefined URL. When such request arrives, it converts the input stream using [Byte Array to String transformer](http://www.mulesoft.org/documentation/display/current/Using+Transformers) and passes the content to the [DataMapper Transformer](http://www.mulesoft.org/documentation/display/current/Datamapper+User+Guide+and+Reference). This transformer converts the format of the data from XML to a POJO, a Workday request. Each mapping earns an arrow which helps you to visualize the activity that occurs within the DataMapper transformer. After data conversion, the application uses a [Workday Connector](http://www.mulesoft.org/documentation/display/current/Workday+Connector) to push data into your Workday system. The connector's configuration specifies the **operation** – *Put customer*. 

### Documentation ###

Studio includes a feature that enables you to easily export all the documentation you have recorded for your project. Whenever you want to share your project with others outside the Studio environment, you can export the project's documentation to print, email or share online. Studio's auto-generated documentation includes:

- A visual diagram of the flows in your application
- The XML configuration which corresponds to each flow in your application
- The text you entered in the Notes tab of any building block in your flow

Follow [the procedure](http://www.mulesoft.org/documentation/display/current/Importing+and+Exporting+in+Studio#ImportingandExportinginStudio-ExportingStudioDocumentation) to export auto-generated Studio documentation.

### Go Further ###

- Learn more about the [Anypoint DataMapper Transformer](http://www.mulesoft.org/documentation/display/current/Datamapper+User+Guide+and+Reference).