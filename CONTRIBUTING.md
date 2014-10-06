# Overview

This repository contains the core components of the [EMF-IncQuery project](http://eclipse.org/incquery).  EMF-IncQuery is a framework for defining declarative graph queries over EMF models, and executing them efficiently without manual coding in an imperative programming language such as Java.

With EMF-IncQuery, you can:

* Define model queries using a high level yet powerful query language (supported by state-of-the-art Xtext-based development tools)
* Execute the queries efficiently and incrementally, with proven scalability for complex queries over large instance models
* Integrate queries into your applications using essential feature APIs including IncQuery Viewers, Databinding, Validation and Query-based derived features with notifications.

# Installation
To install EMF-IncQuery to your Eclipse instance, you can use the update sites created and maintained by the project. Before installation, you will need a current version of EMF and Xtext installed.

The update site locations are described in [http://eclipse.org/incquery/download.php](http://eclipse.org/incquery/download.php), while their contents are updated in [the installation wiki page](http://wiki.eclipse.org/EMFIncQuery/UserDocumentation/Installation).

# Most important links

 * [Project homepage](http://eclipse.org/incquery)
 * [Download links](http://http://eclipse.org/incquery/download.php)
 * [Documentation pages](http://wiki.eclipse.org/EMFIncQuery)

# Building and Contributing

## Building EMF-IncQuery
This repository does not contain generated code, making first build from source somewhat complicated. A detailed description is maintained in the [development environment wiki page]
(http://wiki.eclipse.org/EMFIncQuery/DeveloperDocumentation/DevEnvironment).

EMF-IncQuery maintains Maven/Tycho-based builds. It can be executed by executing the following lines:

    mvn -f releng/org.eclipse.incquery.parent/pom.xml clean install
    
The created update sites will be available in:

  * **Core plug-ins**: releng/org.eclipse.incquery.update/target/repository/
  * **All plug-ins**: releng/org.eclipse.incquery.update.extras/target/repository/

## Contributing

The development of EMF-IncQuery follows the [Eclipse Development Process](http://www.eclipse.org/projects/dev_process/development_process.php). For contributions, this means that all potential contributors need to

 * Fill out an [Contributor Licence Agreement](http://www.eclipse.org/legal/CLA.php) by logging in to the [Eclipse Project Forge](https://projects.eclipse.org/user/login/sso) and selecting the Contributor Licence Agreement. This is required for all projects hosted at eclipse.org, but it is enough to do it once for all projects.
 * Upload the contributions to the eclipse.org infrastructure. The preferred way is using Gerrit, but we also accept patches to Bugzilla issues. 
   * Gerrit url: ssh://«username»@git.eclipse.org:29418/incquery/org.eclipse.incquery
   * Bugzilla issues: [https://bugs.eclipse.org/bugs/buglist.cgi?list_id=7877175&query_format=advanced&bug_status=UNCONFIRMED&bug_status=NEW&bug_status=ASSIGNED&bug_status=REOPENED&product=Incquery]()
   * An up-to-date description of this information is available from the [Eclipse Wiki Contribution Page](http://wiki.eclipse.org/Development_Resources/Contributing_via_Git)

----
All code in this repository is available under the Eclipse Public License v1.0: [http://www.eclipse.org/legal/epl-v10.html]()