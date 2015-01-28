# Salesforce ID retrieval Example

This example application illustrates how to use Mule ESB to build a simple HTTP application to query Salesforce in a dynamic way. After reading this document, creating and running the example in Mule, you should be able to leverage what you have learned to create an HTTP request-response application that is able to retrieve requested data from Salesforce instance based on your criteria.

### Assumptions

This document describes the details of the example within the context of Anypoint™ Studio, Mule ESB’s graphical user interface (GUI). This document assumes that you are familiar with Mule ESB and the [Anypoint Studio interface](http://www.mulesoft.org/documentation/display/current/Anypoint+Studio+Essentials). To increase your familiarity with Mule Studio, consider completing one or more [Anypoint Studio Tutorials](http://www.mulesoft.org/documentation/display/current/Basic+Studio+Tutorial).

### Example Use Case

In this example, a user makes an HTTP request to the endpoint containing predefined set of parameters that are extracted and used while building a Salesforce query. Afterwards, the application receives the response and provides it to the end user. 

### Set Up and Run the Example

As with other [example templates](http://www.mulesoft.org/documentation/display/current/Mule+Examples), you can create template applications straight out of the box in Anypoint Studio. You can tweak the configurations of these use case-based examples to create your own customized applications in Mule.

Follow the procedure below to create, then run the Login using HTML Form application.

1. [Create, then run](http://www.mulesoft.org/documentation/display/current/Mule+Examples#MuleExamples-CreateandRunExampleApplications) the example application in Anypoint Studio or Standalone.
1. Open your browser and hit **http://localhost:8081/?object=contact&value=young&key=name&field=Id**.

	+	object - specify Salesforce object type, e.g. User, Account, Contact, etc. 
	+	field - a valid field name for the given object you need, e.g. Id for Contact.
	+	searchKey -  a valid field name for the given object you wil use for matching, e.g. Name for Contact.
	+	searchValue - a value for the *searchKey* field that will be used for matching, e.g. *young* for Contact Name. All records that contain the *searchValue* value as a substring of the given *searchKey* field value are returned, i.e. contacts with names such as John Young or Michael Young will be returned. The more specific value, the less results you will obtain. 

2. You will see the retrieved data structure.

### How it Works

The **Salesforce ID retrieval** example application contains a simple [flow](http://www.mulesoft.org/documentation/display/current/Mule+Application+Architecture) which receives end user HTTP requests, process them and returns the result to the client.

The sections below elaborate further on the configurations of the application and how it works to respond to end user requests.

### salesforceIdRetrievalFlow

This flow makes use of two [building blocks](http://www.mulesoft.org/documentation/display/current/Elements+in+a+Mule+Flow) to receive and respond to an end user requests. When an end user request encounters the application, the first building block it meets is [HTTP Inbound Endpoint](http://www.mulesoft.org/documentation/display/current/HTTP+Connector). Because it has a two-way message exchange pattern, this HTTP endpoint is responsible for both receiving requests from and sending responses to the end user.

Then, [Salesforce component](http://www.mulesoft.org/documentation/display/current/Salesforce+Connector) executes a query created using Mule Expression Language. Basically, parameters from the request URL are extracted. The Expression block transforms Salesforce data structure into a list of objects that Mule moves back to the [HTTP Inbound Endpoint](http://www.mulesoft.org/documentation/display/current/HTTP+Connector) which simply returns the message payload as the response to an end user in the form of HTML.
 
### Go Further

- Learn more about the [HTTP endpoint](http://www.mulesoft.org/documentation/display/current/HTTP+Connector).
- Learn more about the [Mule Expression Language](http://www.mulesoft.org/documentation/display/current/Mule+Expression+Language+MEL) 
- Learn more about the [Expression component](http://www.mulesoft.org/documentation/display/current/Expression+Component+Reference) 
- Learn more about the [Salesforce component](http://www.mulesoft.org/documentation/display/current/Salesforce+Connector)