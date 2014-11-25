# DataMapper with flowref lookup 

This application uses pre-packaged tools to append data to a message payload and perceptively connect with Salesforce. The example takes a CSV file of new account information, figures out which region each account belongs to, appends region information to the payload, then uploads the new accounts to an active Salesforce user account. It uses Mule DataSense and Anypoint DataMapper to map and transform data, thereby facilitating quick integration with this Software as a Service (SaaS) provider.

#### Connect with Salesforce ####

At times, you may find that you need to connect one or more of your organization's on-premise systems with a SaaS application such as Salesforce. Ideally, these independent systems would talk to each other and share data to enable automation of end-to-end business processes. Use Mule applications to facilitate communication between your on-prem system(s) and Salesforce. (Though this use case does not extend as far, you can also use Mule to facilitate communication between SaaS providers.)

#### DataMapper and FlowRefLookup ####

Beyond transforming and mapping data from one format to another, you can use an Anypoint DataMapper Transformer to access other flows in a Mule application to acquire additional information. Use a FlowRefLookup Table to acquire information outside the message, then append it to the payload.

### Assumptions ###

This document assumes that you are familiar with Mule and the [Anypoint™ Studio interface](http://www.mulesoft.org/documentation/display/current/Anypoint+Studio+Essentials). To increase your familiarity with Studio, consider completing one or more [Anypoint Studio Tutorials](http://www.mulesoft.org/documentation/display/current/Basic+Studio+Tutorial). Further, this example assumes that you have a basic understanding of [Mule flows](http://www.mulesoft.org/documentation/display/current/Mule+Application+Architecture), [Mule Global Elements](http://www.mulesoft.org/documentation/display/current/Global+Elements), and Studio's [Anypoint DataMapper](http://www.mulesoft.org/documentation/display/current/Datamapper+User+Guide+and+Reference). 

This document describes the details of the example within the context of Anypoint Studio, Mule ESB’s graphical user interface, and includes configuration details for XML Editor where relevant.  

### Example Use Case ###

The use case upon which this example is based represents a reasonably common requirement to upload new account information into Salesforce. From a CSV file containing information about new accounts (company name, billing address, etc.), a user wishes to use the addresses of the companies to determine to which sales region they belong, then upload all the account details – including sales region – to Salesforce. This example application performs these actions using one Mule application.

### Set Up and Run the Example ###

Complete the following procedure to create, then run this example in your own instance of Mule Studio. Skip ahead to the next section if you prefer to simply examine this example.

To witness end-to-end functionality, you must have an active Salesforce account into which you don't mind inserting a new custom field for "region" and two sample accounts. After this application automatically uploads these accounts, you can manually delete them, and the custom field, in your Salesforce account.

#### Create a Custom Field in Salesforce ####

1. Log in to your Salesforce account, then navigate to the **Setup** tab. Now click on **Customize -> Accounts -> Fields** under the Build section on the left side of the page.
1. Now click on **New** under the Account Custom Fields & Relationships section.
    - In Step 1 of the new field process, select **Text**, then click **Next** to continue.
    - In Step 2, enter values for your new field as per the table below, then click **Next** to continue.

	    	Field Label		Region
		    Length			50
		    Field Name		Region

1. In Step 4, check the **Account Layout** box, then click **Save**.
1. Your new field name appears followed by a double-underscore and a lowercase "c":  Region__c  This is the new field to which DataMapper will map the region data it acquires from another flow.

#### Create the Example Application ####

1. [Create](http://www.mulesoft.org/documentation/display/current/Mule+Examples#MuleExamples-template) the example application in Mule Studio, using the **DataMapper with FlowRefLookup** template. *Do not run the application*.
1. Log in to your Salesforce account. From your account menu (your account is labeled with your name), select **Setup**.
1. In the left navigation bar, under the **Personal Setup** heading, click to expand the **My Personal Information** folder. 
1. Click **Reset My Security Token**. Salesforce resets the token and emails you the new one.
1. Access the email that Salesforce sent and copy the new token onto your local clipboard.
1. In your SaaS Integration application in Mule Studio, click the **Global Elements** tab. 
1. Double-click the Salesforce global element to open its **Global Element Properties** panel. In the **Security Token** field, paste the new Salesforce token you copied from the email. Alternatively, configure the global element in the XML Editor.
2. Change the contents of the **Username** and **Password** fields to your account-specific values, then click **OK** to save your changes. 

#### Run the Example Application ####

1. In the **Package Explorer**, under the *src/main/resources* folder create two new folders named **input** and **output**. Now, run the example as a Mule Application. 
1. This project includes a sample CSV file, called companies.csv, that you can use to witness end-to-end functionality of the application. In the **Package Explorer**, click the *src/main/resources* folder to expand it, then find the *companies.csv* file inside this folder.
1. Click and drag the *companies.csv* file into the *input* folder in the same directory.
1. The File Endpoint in the application polls the input folder every ten seconds. It picks up the CSV file, processes it, then deposits it into the output folder in the same directory. (Hit **F5** to refresh the contents of the *input* and *output* folders.)
1. In your browser, access your Salesforce account, then navigate to the **Accounts** tab.
1. Use the drop-down menu to display **All Accounts,** then scan your contacts for two new entries:  
           
           - Universal Exports
           - Best Widgets
1. You should get the following output in your console window:
        
          State to lookup is: FL
          INFO  2014-07-22 12:36:45,800 [REFORMAT0_0] org.mule.api.processor.LoggerMessageProcessor: Region is : South East
          State to lookup is: CA
          INFO  2014-07-22 12:36:45,804 [REFORMAT0_0] org.mule.api.processor.LoggerMessageProcessor: Region is : West Coast
1. Stop the Mule application by clicking the square, red terminate button in the **Console**.
1. Delete the two sample accounts from your Salesforce account.
1. Delete the custom field, Region, from your Salesforce account.

### How it Works ###

Using two flows, this application accepts CSV files which contain account information, uses the "state" data to append a sales region to the message, then uploads the contacts to Salesforce. 

#### CreateNewSalesforceAccountFlow ####

The [File Endpoint](http://www.mulesoft.org/documentation/display/current/File+Connector) polls the input folder for new files every ten seconds. When it spots a new file, it reads it and passes the content to the [Anypoint DataMapper transformer](http://www.mulesoft.org/documentation/display/current/Datamapper+User+Guide+and+Reference). This transformer not only converts the format of the data from CSV to a collection, it automatically maps the input fields from the CSV file – company_name, company_address, etc. – to output fields that Salesforce uses in a collection. Each mapping earns an arrow which helps you to visualize the activity that occurs within the DataMapper transformer.

The DataMapper also utilizes a [FlowRef Lookup](http://www.mulesoft.org/documentation/display/current/Using+DataMapper+Lookup+Tables) Table. This Lookup Table accesses another flow in the application to acquire the sales region for each new account. DataMapper invokes the LookupSalesRegionFlow which uses the company_state data to determine into which sales region the account falls. DataMapper then maps this newly acquired data to the custom field in Salesforce, Region__c.

When it has converted all the account information in the file to a collection of Salesforce-friendly data, the application uses a [Salesforce Connector](http://www.mulesoft.org/extensions/salesforce-cloud-connector) to push data into your Salesforce account. The connector's configurations specify the **operation** – *Create* – and the **sObject** type – *Account* – which dictate exactly how the data uploads to Salesforce; in this case, it creates new accounts. 

#### LookupSalesRegionFlow ####

This flow consists of a [Groovy](http://www.mulesoft.org/documentation/display/current/Groovy+Component+Reference) component and a [Logger](http://www.mulesoft.org/documentation/display/current/Logger+Component+Reference).  The script in the component uses state information in the message payload to calculate the sales region to which the account belongs. Invoked by the FlowRefLookup table in DataMapper, this flow exists only to determine a sales region for each account in the CSV file.

### Building the Application ###

While the application's functionality is relatively straightforward, the beauty of this project is illustrated through its use of [DataSense](http://www.mulesoft.org/documentation/display/current/DataSense). Rather than building the application serially – adding, then configuring each of the elements manually according to the order in which they appear in the flow – you can use DataSense to complete the most difficult configurations automatically. The following steps outline the process to build this application.

1. Place a Groovy component into your application, then configure the script it contains as per the following.

		<scripting:component doc:name="Groovy">
 
            <scripting:script engine="Groovy">def region = "UNKNOWN"
 
				def state = payload['state']
				 
				if (state != null) {
				 
				 state = state.toUpperCase()
				 
				}
				 
				println "State to lookup is: " + state
				 
				switch (state) {
		 
		        case ["CT","ME","MA","NH","VT","RI","NY","NJ","DE","DC","MD","NH"]:
		 
		            region = "North East"
		 
		            break
		 
		        case ["AL","AR","FL", "GA","LA" ,"SC","NC","TN","TX"]:
		 
		            region = "South East"
		 
		            break
		 
		        case ["ID","IL", "IA","KS","MT", "WY","ND","SD","OH" ]:
		 
		            region = "Mid West"
		 
		            break
		 
		        case ["AZ","CO","OK","NM", "NV"]:
		 
		            region = "South West"
		 
		            break
		 
		        case ["CA","HI","WA","OR", "AK"]:
		 
		            region = "West Coast"
		 
		            break
		 
		   		}
				return ["region":region]
			</scripting:script>
        </scripting:component> 
2. Add a **Logger** to the flow, after the Groovy component.
3. Create a new flow in your application, then rename it if you wish.
4. Place a **File** endpoint into your new flow, completing the simple configuration to enable it poll a specific folder for input files.  

		Display Name			File
		Path					src/main/resources/input
		Move to Directory		src/main/resources/output
		Polling Frequency		10000

1. Add a **Logger** component after the File endpoint.
2. Next, add a **Salesforce Connector** to the flow. At this point, you can configure the connector with your Salesforce account-specific details and test the connection to Salesforce. Not only does the embedded Mule DataSense functionality confirm that you have a clear channel for communication, it gathers metadata about Salesforce objects and the type of data it accepts, including the custom Region__c field you created in Salesforce. (The value of this metadata becomes apparent with the introduction of a DataMapper into the flow further in this procedure.)
	1. Modify the display name for the connector, if you wish, then click the (plus) next to the **Config Reference** drop-down to create a new **Global Element**. 
    1. Select the **Salesforce** global element, then click **OK**.
	1.     Enter values in the **Username**, **Password** and **Security token** fields, then click **OK**. (See the Set Up section above for details on how to acquire the security token.) Notice that Studio automatically enables DataSense in the global element.

1. When you click **OK**, Mule tests the connection to Salesforce (see image below). With a valid username, password and security token, the connection test succeeds and Mule saves your global element configurations. If any of the values are invalid, the connection fails, and Mule does not save the global element, prompting you to correct the invalid configurations.
1. Back in the Salesforce connector **Pattern Properties** panel, use the drop-down menus to select the **Operation** and **sObject** Type. Because the DataSense activity has gathered metadata about Salesforce's operations and data sObject types, Mule is able to present a list of Salesforce-specific values in the drop-down menus for each of these fields. Select **Create**.
2. Having defined the Salesforce-friendly output, you can then drop a **DataMapper** transformer between the Logger and the connector in the flow to map CSV input fields to Salesforce output fields. Note that DataSense has already acquired the operation and sObject information from Salesforce and populated the output fields for you. 
3. To complete the configuration of this transformer, you need only enter the Input values. In this example application, we used an existing CSV example to define the input fields in DataMapper.
4. When you save the DataMapper configurations, Mule maps input fields to output. Where the input and output fields have identical names, DataMapper intelligently, and automatically, maps input to output. Otherwise – as with this example – you can quickly map input to output manually by clicking and dragging input fields to output fields in the Data Mapping Console (see below).  The table below the image indicates the fields as mapped from input to output.

		Input				Output
		company_address		BillingStreet
		company_city		BillingCity
		company_name		Name
		company_state		BillingState
		company_zip			BillingPostalCode
		region				Region__c

1. The configuration now complete, you can save, then run the application.
2. Feed CSV files with contact information into the input folder, and watch the new contents appear in your Salesforce account (see image below).

### Documentation ###

Studio includes a feature that enables you to easily export all the documentation you have recorded for your project. Whenever you want to share your project with others outside the Studio environment, you can export the project's documentation to print, email, or share online. Studio's auto-generated documentation includes:

- A visual diagram of the flows in your application
-     The XML configuration which corresponds to each flow in your application
-     The text you entered in the Documentation tab of any building block in your flow

Follow the [procedure](http://www.mulesoft.org/documentation/display/current/Importing+and+Exporting+in+Studio#ImportingandExportinginStudio-ExportingStudioDocumentation) to export auto-generated Studio documentation.

### Go Further ###

- Learn more about [Connection Testing](http://www.mulesoft.org/documentation/display/current/Testing+Connections) and [DataSense](http://www.mulesoft.org/documentation/display/current/DataSense).
- Learn more about the [Anypoint DataMapper Transformer](http://www.mulesoft.org/documentation/display/current/Datamapper+User+Guide+and+Reference).