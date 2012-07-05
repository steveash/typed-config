typed-config
============

Strongly typed configuration layer on top of apache commons configuration.

(This was originally a fork from http://code.google.com/p/config-proxy/ by Jonny Tyers, but I needed some 
additional features which resulted in a very different code base).


typed-config overview
============

Typed-config enables you to make configuration files accessible to Java applications through simple Java 
interfaces, with built-in validation, default values, and more.

Powered by Apache Commons Configuration, typed-config can be used with XML files (one or many) and properties 
files (one or many). typed-config is designed to be a thin wrapper over the top of commons-configuration and 
to re-expose existing features wherever possible.

Imagine a simple, readable XML configuration file for your application:

```xml
<configuration>
  <serverAddress>http://host1.example.com/</serverAddress>
  <downloadUrl>file.zip</downloadUrl>
  <proxy>http://proxy.example.com/</proxy>
</configuration>
```

and imagine accessing it easily through a Java interface:

```xml
public interface DownloadConfiguration {
  String getServerAddress();
  String getDownloadUrl();
  String getProxyAddress();
}
```

that is done just by creating the `DownloadConfiguration` interface exactly as shown above (no annotations in the
typical convention-over-configuration case), and then getting an instance of it by:

```java
DownloadConfiguration config = 
   ConfigProxyFactory.getDefault().make(DownloadConfiguration.class,
      new XMLConfiguration("myconfig.xml"));
```

more features
============
* javax.validation annotations like `@Min`, `@NotBlank` to express constraints
* Support for many data types including all primitives, enumeration types
* Support for extensibly adding your own data types
* Support arbitrary nesting of interfaces to mimic nested xml tags
* Support for returning Lists and Sets of types
* Extensible caching strategies to determine how values are cached
* Default values, default lookup values
* Return a HierarchicalConfiguration instance scoped to the current nested level of the configuration graph
* Lookup values to embed arbitrary object graph references in your config (think of Commons Config String Interpolator but going through the Expression Engine
* Map types that use a part of the configuration to be the key in the map
* Instances returned support hashcode, toString, and equals

using typed-config
============
typed-config is not in the public maven repo yet, but that is coming.  For now, the easiest way is just to
clone my git repo and deploy it in to your local maven artifact server.  Until more comprehensive documentation
is complete, please look at the integration tests:

* com.github.steveash.typedconfig.ConfigProxySimple1IntegrationTest
* com.github.steveash.typedconfig.ConfigProxySimple2IntegrationTest
* com.github.steveash.typedconfig.NestedConfig2IntegrationTest
* com.github.steveash.typedconfig.NestedConfig3IntegrationTest
* com.github.steveash.typedconfig.NestedMapConfigIntegrationTest
* com.github.steveash.typedconfig.LookupIntegrationTest


license
============

Copyright (c) 2012 Jonathan Tyers, Steve Ash

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.