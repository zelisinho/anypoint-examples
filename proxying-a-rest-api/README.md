# Proxying a REST API #

This example shows how to proxy your API. Applications send service requests to your proxy, which in turn calls the real API.

### Assumptions ###

This document assumes that you are familiar with Mule and the [Anypoint™ Studio interface](http://www.mulesoft.org/documentation/display/current/Anypoint+Studio+Essentials). To increase your familiarity with Studio, consider completing one or more [Anypoint Studio Tutorials](http://www.mulesoft.org/documentation/display/current/Basic+Studio+Tutorial). Further, this example assumes that you have a basic understanding of [Mule flows](http://www.mulesoft.org/documentation/display/current/Mule+Application+Architecture) and [Mule Global Elements](http://www.mulesoft.org/documentation/display/current/Global+Elements).

Furthermore, this document assumes that you have a REST API that has not been built to run on Mule ESB
 


### Example Use Case ###

To demonstrate the basic procedure of creating a proxy application, this document uses the public Box API as an example REST API to stand in for any REST API that you have that you might want to proxy through a Mule application. The specific configuration for Box is summarized here, but you will need to replace this with the corresponding information for your own RESTful services that you wish to proxy.

### Set Up and Run the Example ###

To follow along with the steps in this example, you must have a [box.com](https://app.box.com/files) account, which you can create for free if you don't already have one.

#### Registering an App in the Box Developer Portal ####

The steps below are only needed in this particular example so that you can test your finished proxy for the Box API by simulating an API call from an application. They don't necessarily match the steps you need to carry out to test your own API.

1. Go to Box's developer portal: [developers.box.com](https://developers.box.com/)
1. If you do not have an account, you need to create one [here](https://app.box.com/signup/personal). If you have one, click **My apps** in the upper-right corner of the [page](https://developers.box.com/).
2. Click **Create a Box Application** in the panel on the right. Give it any name, such as MyProxy, then select the **Content API**. 
1. Click **Configure Application**.
1. Look for the *client_id* and the *client_secret*. Copy these to a safe place, as you will need them later.
1. Add a *redirect_url*. For the purpose of this exercise, any https URL will do, even https://www.google.com.

Leave the box developer portal open for now, as you will return here later to request an OAuth token. Because the OAuth token expires very quickly, it's best to build the flow before you request it.

#### Building the Proxy in Studio ####

Next, build your proxy application in Mule Studio. Your proxy application needs to:

1. Accept incoming service calls from applications and route them to the Box API.
1. Copy any message headers from the service call and pass them along to the Box API.
1. Disable the default status code exception check to allow any error messages that the Box API returns to be passed on to the application. 
1. Capture message headers from the Box API's response and attach them to the response message.
1. Route the response to the application that made the service call.

The following steps describe how to obtain a token for the Box API and use it to test the proxy you have just built by simulating an API call from an application.

1. Deploy your Mule Project to the embedded Mule server by right-clicking the project in the Package Explorer, then selecting **Run As... > Mule Application**.
2. In any Web browser, enter the following URL: 

		http://localhost:8081/oauth2/authorize?response_type=code&client_id=<CLIENT_ID>

	Replace <CLIENT_ID> in the URL above with the client_id provided by Box when you registered your new app.
3. Box will prompt you to log in with your username and password. You can use your personal credentials or create a new test account.
4. Before you click **Grant access to Box**, you should be ready for the following steps, as the token code you will obtain will expire in only 30 seconds.
Be ready to send **http://localhost:8081/oauth2/token** as an HTTP **POST** request that includes a body with the properties below:

		Attribute		Value
		grant_type		authorization_code
		code			<fill this in during the next step>
		client_id		<client_id provided by Box when you registered your app>
		client_secret	<client_secret provided by Box when you registered your app>

	To send this request, use a browser extension such as [Postman](https://chrome.google.com/webstore/detail/postman-rest-client/fdmmgilgnpjigdojojpjoooidkmcomcm) (Google Chrome), or the [curl](http://curl.haxx.se/) command line utility. If using Postman, click **x-www-form-urlencoded** tab and insert the aforementioned key/value pairs.

5. Once you have prepared for the next step, go back to the browser page where you entered your Box credentials and click **Grant access to Box**.
6. The browser is redirected to the page you set as the redirect on your Box app. For this exercise, the page itself is irrelevant, but the full URL will include an extra parameter named code. For example:

		https://www.google.com/?state=&code=<CODE>

7. Copy the value of &lt;CODE&gt; from the URL and paste it into your POST request so that its properties are the following:
	
		Attribute		Value
		grant_type		authorization_code
		code			<code provided by redirect URL>
		client_id		<client_id provided by Box when you registered your app>
		client_secret	<client_secret provided by Box when you registered your app>
1. Send the request.
1. This POST request returns a JSON object with several fields. Copy the value corresponding to *access_token*, as you will need it soon. The *access_token* lasts for an hour before expiring.
1. Now you can make proper requests to your proxy. You must include *access_token* on every request as a header with the name Authorization.

		Header			Value
		Authorization	Bearer <access_token>

	The value of the header must include the word Bearer followed by a space and then the access_token. For example:
	
		Authorization=Bearer 1234123412341234

Try making a **GET** request to [http://localhost:8081/2.0/folders/0](http://localhost:8081/2.0/folders/0), remembering to include the Authorization header. 

### How it Works ###

Follow the anatomy described here to build a proxy application in Mule Studio that abstracts your API to a new layer. Your proxy application needs to:

1. Accept incoming service calls from applications and route them to the URI of your target API.
1. Copy any message headers from the service call and pass them along to your API.
1. Avoid passing internal Mule headers both to the API and back to the requester.
1. Add a flag that ensures that your target API's HTTP status codes are returned to the requesting app, and not overwritten by the proxy's own status codes.
1. Capture message headers from your API's response and attach them to the response message.
1. Route the response to the application that made the service call.

Follow the instructions below to build the proxy application, using either the visual drag-and-drop editor or the XML editor, or some combination. Instructions for both editors are provided below.

1. Create a new Mule Project by going to **File > New... > Mule Project**, naming it **proxying-a-rest-api**.
2. Drag an **HTTP endpoint** into a new flow. This is an inbound endpoint for your proxy application and receives all the requests that reference its address. 
	
	If you click on the HTTP icon, Studio opens the endpoint's properties editor in the console below the canvas. Here, you can configure the inbound endpoint to be reached through a custom address by setting the host and port, or switching to the advanced tab and specifying an address. For the purposes of this example, keep all the default settings, except for the **Display Name**, which you can change to Receive *HTTP requests from apps*.

		Display Name	Receive HTTP requests from apps
		Host			localhost
		Port			8081
		Path	 

	Setting a path for an inbound endpoint is not recommended on a proxy. If your inbound endpoint is reached through an address that includes a path  (ex: through *http://somehost:someport/flow1*), then this path will form part of the property http.request, which is needed later. The *http.request* property will only resolve correctly if it does not include a /path.
3. Drag a second **HTTP endpoint** into the same flow. This second endpoint acts as an outbound endpoint and passes requests to the Box API, receives responses from the API, and passes the responses back to the application that initiated the API call.
4. Click on the HTTP icon to open its properties editor. In the General tab, change the **Display Name** to *Send requests to API*.
5. Check the box to **Enable HTTPS**. The Box API requires all incoming calls be through the HTTPS protocol. This might not be the case for your API.
6. Delete the default value that appears in the **Host** field, leaving it blank.
7. In the **Advanced** tab, set the address to:

		https://api.box.com#[message.inboundProperties['http.request']]
	
	The **http.request** inbound property references the URI subpath of the request that reaches the HTTP inbound endpoint. If your proxy application receives an HTTP request through http://localhost:8081/2.0/folders/0 then http.request contains the value *2.0/folders/0*. As this is a REST API, the requests that apps send to the API include arguments as part of the URI. By appending  *#[message.inboundProperties['http.request']]* onto the end of the URL, your proxy application captures these arguments and forwards them to the Box API.

8. In the **References** tab, add a new Connector Reference by clicking on the green plus sign next to the field. This will create a global element, which encapsulates reusable connection settings.
9. On the Choose Global Type window, select **HTTP\HTTPS**, then click **OK**.
1. Studio opens the Global Element Properties window and prompts you to name your global element. Name it **httpConnector**.
1. Drag a **Property** transformer in between the two HTTP endpoints. Configure it as shown:

		Display Name	Disable exception check
		Operation		Set Property
		Name			http.disable.status.code.exception.check

	By setting the variable *http.disable.status.code.exception.check* to true, you are adding a flag to the message that indicates to Mule that any HTTP status codes generated by the Box API must be returned to the requesting app, without being overwritten by the proxy's own status codes.

12. To deal with the message headers, you'll need to perform a series of simple operations. As these same operations have to be processed both with incoming and outgoing messages, it makes sense to encapsulate this set of tasks into a reusable sub-flow that you can call twice. You must add:

	1. a **sub-flow** outside the current flow, created by dragging a sub-flow component to the empty space below the flow 
	1.     a **flow reference** element after the HTTP inbound endpoint that comes from the requesting app
	1.     another **flow reference** element after the HTTP outbound endpoint to your API.


	Whenever the message reaches one of the **flow reference** elements, the logic in the referenced **sub-flow** is executed, then the execution of the rest of the main flow is resumed.
1. Click on the sub-flow and rename it to **copy-headers**.
1. Configure both flow reference elements so that they reference the sub-flow you created:

		Display Name	Copy HTTP Headers
		Flow name		copy-headers

15. When an application makes a call to your API, that call may include headers that the API needs to receive. The proxy application must capture all incoming headers and pass them along, unaltered. In Mule, any incoming message headers that enter the proxy application are treated as inbound properties, which are not forwarded to your API. Thus, the proxy application must take HTTP inbound properties and transform them into **outbound properties**, which are sent to the API via the outbound endpoint. In the new sub-flow that you created below the main flow, drag and drop a **property** component.
16. Configure this property transformer as shown:

		Display Name	Copy All HTTP Headers
		Operation		Copy Properties
		Name			*

	Set like this, the transformer copies all inbound properties and sets them as outbound properties.

17. Still in the sub-flow, after the property transformer, add three more property transformers and configure them as follows:

First Property transformer:

		Display Name	Remove Content Length
		Operation		Remove Property
		Name			Content-Length

Second Property transformer:

		Display Name	Remove MULE properties
		Operation		Remove Property
		Name			MULE_*

Third Property transformer:

		Display Name	Remove X_MULE properties
		Operation		Remove Property
		Name			X_MULE*

The output of a well-built proxy should be identical to its input. Mule auto-generates a few headers that are meant for using within the flow and that are irrelevant to your API, when making all HTTP headers into outbound properties (as instructed in the previous step) these headers will be passed on as well. The Property transformers covered in this step take care of removing these unnecessary headers.

You now have a minimum proxy that can route requests and responses to and from your REST API.

### Documentation ###

Studio includes a feature that enables you to easily export all the documentation you have recorded for your project. Whenever you want to share your project with others outside the Studio environment, you can export the project's documentation to print, email or share online. Studio's auto-generated documentation includes:

- A visual diagram of the flows in your application
- The XML configuration which corresponds to each flow in your application
- The text you entered in the Notes tab of any building block in your flow

Follow [the procedure](http://www.mulesoft.org/documentation/display/current/Importing+and+Exporting+in+Studio#ImportingandExportinginStudio-ExportingStudioDocumentation) to export auto-generated Studio documentation.

### Go Further ###

- Mulesoft offer an out of the box solution for proxying and managing existing APIs using the Anypoint API Platform. To see the detailed documentation and capabilities, please refer to: [Proxying Your API](http://www.mulesoft.org/documentation/display/current/Proxying+Your+API)
