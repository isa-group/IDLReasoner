# IDL-Analyzer

This is an Analyzer that analyses the inter-dependencies bewteen parameters in an API REST.
The Analyzer object need the API Specifications giving by a URL (In yaml or json), a idl file, the operation name
and the operation type

```java
	Analyzer a = new Analyzer({.ild file}, {API Specifications URL},
	 {Operation type (REEST)}, {Operation name});
```



