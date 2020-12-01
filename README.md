# IDLReasoner

## Analysis operations

IDLReasoner is a Java library that allows to automatically analyze IDL specifications and perform a number of operations on them, namely:

1. **isValidIDL**: 
	- *Input*: 1) IDL specification; 2) API operation parameters.
	- *Output*: Boolean indicating whether the IDL specification is valid or not (i.e. it does not contain dead nor false optional parameters and at least one valid request can be generated).
1. **isDeadParameter**:
	- *Input*: 1) IDL specification; 2) API operation parameters; 3) Parameter to check.
	- *Output*: Boolean indicating whether the parameter passed is dead or not (i.e. it can never be used in an API request due to inconsistencies in the IDL specification).
1. **isFalseOptional**:
	- *Input*: 1) IDL specification; 2) API operation parameters; 3) Parameter to check.
	- *Output*: Boolean indicating whether the parameter passed is false optional or not (i.e. it is required despite being declared as optional. This also happens because of inconsistencies in the IDL specification).
1. **getRandomRequest**:
	- *Input*: 1) IDL specification; 2) API operation parameters.
	- *Output*: Valid API request (i.e. a request satisfying all dependencies and including all required parameters).
1. **isValidRequest**:
	- *Input*: 1) IDL specification; 2) API operation parameters; 3) Request to check.
	- *Output*: Boolean indicating whether the request is valid or not.
1. **isValidPartialRequest**:
	- *Input*: 1) IDL specification; 2) API operation parameters; 3) Request to check.
	- *Output*: Boolean indicating whether the request is partially valid or not (i.e. some parameters still need to be set to make it a fully valid request).
1. **getAllRequests**:
	- *Input*: 1) IDL specification; 2) API operation parameters.
	- *Output*: All valid API requests. As a precondition, all parameters of the operation must have a finite domain.
1. **getNumberOfRequests**:
	- *Input*: 1) IDL specification; 2) API operation parameters.
	- *Output*: Total number of valid API requests. As a precondition, all parameters of the operation must have a finite domain.


<!---

This is an Analyzer that analyses the inter-dependencies bewteen parameters in an API REST.
The Analyzer object need the API Specifications giving by a URL (In yaml or json), a idl file, the operation name
and the operation type

```java
	Analyzer a = new Analyzer({.ild file}, {API Specifications URL},
	 {Operation type (REEST)}, {Operation name});
```

## The Project structure

This is the current UML Class Diagram:

![UML Class Diagram](extra_resources/uml.png)

The ResolutorCreator class is the class where the resolver specified in the config.properties file will be created, currently the unique resolutor is Minizinc, but it is possible to add a new resolutor as Choco. Also this class will call a different constructor according to operating system, because the command console will be different depending on the operating system. 
 
```java
		public ResolutorCreator() {

		this.osName = System.getProperty("os.name");
		this.extractDataFromProperties();

		if(this.compiler.equals("Minizinc")) {	
			
			if(this.osName.contains("Windows")) {

				this.curentCompiler = new WindowsResolutor(this.fileRoute, this.solver);
				
			}else{
				
				this.curentCompiler = new MinizincResolutor(this.fileRoute, this.solver);
			}
			

		}else {
			this.curentCompiler = new Resolutor(fileRoute);
		}

```

So if we want to add a new resolutor we must create a Resolutor child and then, we must create a child of the new class made according to the operating system.

--->
