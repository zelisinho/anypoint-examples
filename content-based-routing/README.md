# Content Based Routing


This example shows Anypoint™ Studio's routing capablities based on the content. It routes messages according to the logic you supply in the Choice Router. Flow variables are then set and invoked using Mule Expression Language. 

###Assumptions

This tutorial assumes that you have downloaded and installed Anypoint Studio. If you do not have any previous experience with Eclipse or an Eclipse-based IDE, please review the brief introduction to the Anypoint Studio interface or complete the Basic Studio Tutorial. 

### Sample Use Case

The application receives an HTTP request. It then transforms a query parameter into a flow variable. The message is routed according to the flow variable associated with the message. A new payload is set based on the routing logic, this payload is then sent as an HTTP response.

### Set Up and Run the Example

1. **Open the example** project in studio.
2. In your application in Studio, click the **Global Elements** tab. Double-click the HTTP Listener global element to open its **Global Element Properties** panel. Change the contents of the **port** field to required HTTP port e.g. 8081
3. **Run this project** as a Mule Application.
4. Open any Web browser and **go to**:
      
        http://localhost:8081/?language=Spanish

     Your browser presents a message that reads "Hola!"
   Check the console log in Studio and look for a log message that reads

       
         INFO  2017-09-08 13:24:01,695 [[MuleRuntime].io.03] org.mule.runtime.core.api.processor.LoggerMessageProcessor: The reply "Hola!" means "hello" in Spanish.

5. In your browser’s address bar, **replace URL** with:
  
        http://localhost:8081/?language=French
        
    and then press enter.
    
      Your browser presents a message that reads "Bonjour!" Check the console log in Studio again and look for a log message that reads
      
            INFO  2017-09-08 13:26:01,695 [[MuleRuntime].io.03] org.mule.runtime.core.api.processor.LoggerMessageProcessor: The reply "Bonjour!" means "hello" in French.
            
6. Try requesting the **URL without a query parameter**:
 
        http://localhost:8081 
        
    Your browser presents a message that reads "Hello!"
Check the console log in Studio again and look for a log message that reads

      
        INFO  2017-09-08 13:27:01,691 [[MuleRuntime].io.03] org.mule.runtime.core.api.processor.LoggerMessageProcessor: No language specified. Using English as a default. 

        INFO  2017-09-08 13:27:01,695 [[MuleRuntime].io.03] org.mule.runtime.core.api.processor.LoggerMessageProcessor: The reply Hello! means "hello" in English
 

### Go Further

* Check out the tutorial for this example [here](http://www.mulesoft.org/documentation/display/current/Content-Based+Routing+Tutorial)

* See a more complex example of content-based routing in the [Foreach Processing and Choice Routing Example](http://www.mulesoft.org/documentation/display/current/Foreach+Processing+and+Choice+Routing+Example) and the [Service Orchestration and Choice Routing Example](http://www.mulesoft.org/documentation/display/current/Service+Orchestration+and+Choice+Routing+Example).

* Get a deeper explanation about the [Mule message](http://www.mulesoft.org/documentation/display/current/Mule+Message+Structure) and anatomy of a [Mule application](http://www.mulesoft.org/documentation/display/current/Mule+Application+Architecture).
         
