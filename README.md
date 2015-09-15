# Overview

This repository contains the core components of the [EMF-IncQuery project](http://eclipse.org/incquery).  EMF-IncQuery is a framework for defining declarative graph queries over EMF models, and executing them efficiently without manual coding in an imperative programming language such as Java.

With EMF-IncQuery, you can:

* Define model queries using a high level yet powerful query language (supported by state-of-the-art Xtext-based development tools)
* Execute the queries efficiently and incrementally, with proven scalability for complex queries over large instance models
* Integrate queries into your applications using essential feature APIs including IncQuery Viewers, Databinding, Validation and Query-based derived features with notifications.

# Installation
To install EMF-IncQuery to your Eclipse instance, you can use the update sites created and maintained by the project. Before installation, you will need a current version of EMF and Xtext installed.

The update site locations are described in [http://eclipse.org/incquery/download.php](http://eclipse.org/incquery/download.php).

# Most important links

 * [Project homepage](http://eclipse.org/incquery)
 * [Download links](http://http://eclipse.org/incquery/download.php)
 * [Documentation pages](http://wiki.eclipse.org/EMFIncQuery)

# Building and Contributing

## Building EMF-IncQuery
This repository does not contain generated code, making first build from source somewhat complicated. A detailed description is maintained in the [development environment wiki page](http://wiki.eclipse.org/EMFIncQuery/DeveloperDocumentation/DevEnvironment).

EMF-IncQuery maintains Maven/Tycho-based builds, however for bootstrapping reasons it can be executed in two parts. It can be executed by executing the following lines:

    mvn -f releng/org.eclipse.incquery.parent/pom.xml -Dmaven.runtime=true clean install    
    mvn -f releng/org.eclipse.incquery.parent/pom.xml clean install
    
The created update sites will be available in:

  * **Core plug-ins**: releng/org.eclipse.incquery.update/target/repository/
  * **All plug-ins** (including experimental ones): releng/org.eclipse.incquery.update.extras/target/repository/


## Contributing to EMF-IncQuery

Please read https://wiki.eclipse.org/EMFIncQuery/DeveloperDocumentation/Contributing to understand our contribution process.

# License

All code in this repository is available under the Eclipse Public License v1.0: [http://www.eclipse.org/legal/epl-v10.html]()