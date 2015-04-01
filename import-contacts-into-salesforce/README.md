# Import contacts into Salesforce
#### Enterprise, CloudHub ####

This application uses pre-packaged tools to intelligently connect with Salesforce. Based on a simple use case, the application takes a CSV file of contacts and uploads the contact information to an active Salesforce user account. It uses DataSense and the Anypoint DataMapper Transformer to map and transform data, thereby facilitating quick integration with this Software as a Service (SaaS) provider.

### Connect with Salesforce ###

At times, you may find that you need to connect one or more of your organization's on-premises systems with a SaaS such as Salesforce. Ideally, these independent systems would talk to each other and share data to enable automation of end-to-end business processes. Use Mule applications to facilitate communication between your on-prem system(s) and Salesforce. (Though this use case does not extend as far, you can also use Mule to facilitate communication between SaaS providers).

### Assumptions ###

This document assumes that you are familiar with Mule and the [Anypoint™ Studio interface](http://www.mulesoft.org/documentation/display/current/Anypoint+Studio+Essentials). To increase your familiarity with Studio, consider completing one or more [Anypoint Studio Tutorials](http://www.mulesoft.org/documentation/display/current/Basic+Studio+Tutorial). Further, this example assumes that you have a basic understanding of [Mule flows](http://www.mulesoft.org/documentation/display/current/Mule+Application+Architecture), [Mule Global Elements](http://www.mulesoft.org/documentation/display/current/Global+Elements), and Studio's [Anypoint DataMapper](http://www.mulesoft.org/documentation/display/current/Datamapper+User+Guide+and+Reference). 

This document describes the details of the example within the context of Anypoint Studio, Mule ESB’s graphical user interface, and includes configuration details for XML Editor where relevant. 

### Example Use Case ###

Though a simple example, this application nonetheless employs complex functionality to demonstrate a basic use case. The application accepts CSV files which contain contact information – name, phone number, email – and uploads them into a Salesforce account, automatically inserting the correct data into each Salesforce field. 

### Set Up and Run the Example ###

Complete the following procedure to create, then run this example in your own instance of Anypoint Studio. Skip ahead to the next section if you prefer to simply examine this example via code snippets.

1. Open the Example project in Anypoint Studio from [Anypoint Exchange](http://www.mulesoft.org/documentation/display/current/Anypoint+Exchange). *Do not run the application*.
1. Log in to your Salesforce account. From your account menu (your account is labeled with your name), select **Setup**.
1. In the left navigation bar, under the **Personal Setup** heading, click to expand the **My Personal Information** folder. 
1. Click **Reset My Security Token**. Salesforce resets the token and emails you the new one.
1. Access the email that Salesforce sent and copy the new token onto your local clipboard.
1. In your application in Studio, click the **Global Elements** tab. 
1. Double-click the Salesforce global element to open its **Global Element Properties** panel. In the **Security Token** field, paste the new Salesforce token you copied from the email. Alternatively, configure the global element in the XML Editor.
1. Change the contents of the **Username** and **Password** fields to your account-specific values, then click **OK** to save your changes. 
1. In the **Package Explorer**, right-click the connect-with-salesforce project name, then select **Run As > Mule Application**. Studio runs the application on the embedded server.  
1. This project includes a sample CSV file, called *contacts.csv*, that you can use to witness end-to-end functionality of the application. In the **Package Explorer**, click the *src/main/resources* folder to expand it, then find the *contacts.csv* file inside this folder.
1. Click and drag the *contacts.csv* file into an *input* folder in the same directory.
1. The File connector in the application polls the input folder every ten seconds. It picks up the CSV file, processes it, then deposits it into the output folder in the same directory. (Hit **F5** to refresh the contents of the input and output folders.)
1. In your browser, access your Salesforce account, then navigate to the **Contacts** tab.
1. Use the drop-down menu to display **All Contacts**, then scan your contacts for three new entries:  
	- Charles Bingley
	- Fitzwilliam Darcy
	- George Wickham
1. Stop the Mule application by clicking the square, red terminate button in the **Console**.
1. Delete the three sample contacts from your Salesforce account.

### How it Works ###

Using a single flow with three elements, this application accepts CSV files which contain contact information, then uploads the contacts to Salesforce. 

The [File connector](http://www.mulesoft.org/documentation/display/current/File+Connector) polls the input folder for new files every ten seconds. When it spots a new file, it reads it and passes the content to the [DataMapper Transformer](http://www.mulesoft.org/documentation/display/current/Datamapper+User+Guide+and+Reference). This transformer not only converts the format of the data from CSV to a collection, it automatically maps the input fields from the CSV file – FirstName, LastName, etc. – to output fields that Salesforce uses in a collection. Each mapping earns an arrow which helps you to visualize the activity that occurs within the DataMapper transformer. When it has converted all the contacts in the file to a collection of Salesforce-friendly data, the application uses a [Salesforce Connector](http://www.mulesoft.org/documentation/display/current/Salesforce+Connector) to push data into your Salesforce account. The connector's configurations specify the **operation** – *Create* – and the **sObject type** – *Contact* – which dictate exactly how the data uploads to Salesforce; in this case, it creates new contacts. 

While the application's functionality is relatively straightforward, the beauty of this project is illustrated through its use of [DataSense](http://www.mulesoft.org/documentation/display/current/DataSense). Rather than building the application serially – adding, then configuring each of the elements manually according to the order in which they appear in the flow– you can use DataSense to complete the most difficult configurations automatically. The following steps outline the process to build this application. 

1. Drop a File connector into your application, completing the simple configuration to enable it poll a specific folder for input files.
1. Next, add a Salesforce Connector to the flow. At this point, you can configure the connector with your Salesforce account-specific details and test the connection to Salesforce. Not only does the embedded DataSense functionality confirm that you have a clear channel for communication, it gathers metadata about Salesforce objects and the type of data it accepts. (The value of this metadata becomes apparent with the introduction of a DataMapper into the flow further in this procedure.)
1. When you click Test Connection, Mule tests the connection to Salesforce. With a valid username, password and security token, the connection test results in success and Mule saves your global element configurations. If any of the values are invalid, the connection test results in failure, and Mule does not save the global element, prompting you to correct the invalid configurations.
1. Back in the Salesforce connector properties editor, use the drop-down menus to select the **Operation** and **sObject** Type. Because the DataSense activity has gathered metadata about Salesforce's operations and data sObject types, Mule is able to present a list of Salesforce-specific values in the drop-down menus for each of these fields.
1. Having defined the Salesforce-friendly output, you can then drop a DataMapper between the elements in the flow to map CSV input fields to Salesforce output fields. Because DataSense has already acquired the operation and sObject information from Salesforce, the DataMapper demands that you configure only the input values. In this example application, we used an existing CSV example to define the input fields in DataMapper.
1. Click the edit icon next to **Type** in the Input panel to change the input type to **CSV**. 
1. Use the radio buttons to select **User Defined**, the click **Create/Edit Structure**...
1. Define the fields in the CSV file from which DataMapper will draw its input values. Click **OK**.
1. When you click **Create mapping**, Mule maps input fields to output. Where the input and output fields have identical names, DataMapper intelligently, and automatically, maps input to output, as with the fields in this example application. Otherwise, you can quickly map input to output manually by clicking and dragging input fields to output fields in the Data Mapping Console.
1. The configuration now complete, you can save, then run the application. Feed CSV files with contact information into the input folder, and watch the new contents appear in your Salesforce account.

### Documentation ###

Studio includes a feature that enables you to easily export all the documentation you have recorded for your project. Whenever you want to share your project with others outside the Studio environment, you can export the project's documentation to print, email or share online. Studio's auto-generated documentation includes:

- A visual diagram of the flows in your application
- The XML configuration which corresponds to each flow in your application
- The text you entered in the Notes tab of any building block in your flow

Follow [the procedure](http://www.mulesoft.org/documentation/display/current/Importing+and+Exporting+in+Studio#ImportingandExportinginStudio-ExportingStudioDocumentation) to export auto-generated Studio documentation.

### Go Further ###

- Learn more about [Connection Testing](http://www.mulesoft.org/documentation/display/current/Testing+Connections) and [DataSense](http://www.mulesoft.org/documentation/display/current/DataSense).
- Learn more about [Anypoint DataMapper](http://www.mulesoft.org/documentation/display/current/Datamapper+User+Guide+and+Reference).	