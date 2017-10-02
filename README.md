# Recominer [![Build Status](https://travis-ci.org/rodrigokuroda/recominer.svg?branch=master)](https://travis-ci.org/rodrigokuroda/recominer)

Recominer is a tool aiming to help developers to make changes into source code file. Therefore, using historical information of the commits and issues from software project, it is applied Machine Learning's Classification and Association Rules in order to predict files that are change-prone to complete a particular issue.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

You will need to install the following software:

```
* [Java 8](https://www.java.com/download/) to run Recominer
* [MySQL Database Server](https://dev.mysql.com/downloads/mysql/) for save data
* [Python 2.x](https://www.python.org/downloads/) to run Bicho and CVSAnalY2
* [Bicho](https://github.com/MetricsGrimoire/Bicho) to extract data from issue tracker repository
* [CVSAnalY2](https://github.com/MetricsGrimoire/CVSAnalY) to extract data from version control repository
* [Git/SVN/CVS] to extract data from version control repository.
```

These software needs to be configured in your Operational System's classpath in order to Recominer work (python, csvanaly, bicho, java, git, etc).

### Installing

To build a binary of Recominer (Extractor and Web), you will need the Maven and run the following command on root path of projet:
```
mvn clean build
```

To extract data from repository, you will need run the following command:
```
java -jar recominer-extractor-[version].jar --projectName=[NAME] --versionControlUrl=[url] --repositoryPath=[local_path_of_downloaded_repostiory] --issueTrackerUrl=[url] --issueTrackerSystem=[BUGZILLA|JIRA|GITHUB]
```

A practical example:
```
java -jar recominer-extractor-0.6.0.jar -D --projectName=CXF --versionControlUrl=git://git.apache.org/cxf.git --repositoryPath=/home/user/repositories/cxf --issueTrackerUrl=https://issues.apache.org/jira/browse/CXF --issueTrackerSystem=JIRA
``` 

To run the Recominer Web module (interface to view the prediction results), you will need run the following command:
```
java -jar recominer-web-[version].jar
```

## Running the tests

The unit and integration tests can be run by Maven with following command:
```
mvn test
```

## Built With

* [Spring Boot](https://projects.spring.io/spring-boot/) - The application framework used
* [Spring Batch](https://projects.spring.io/spring-batch/) - The application framework used
* [Angular JS](https://angularjs.org/) - The front-end framework used in Web module
* [Maven](https://maven.apache.org/) - Dependency Management

## Contributing

Please, fell free to make your suggestion and contribution. You can submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/rodrigokuroda/recominer/tags). 

## Authors

* **Rodrigo Kuroda** - *Developer and researcher* - [Rodrigo Kuroda's GitHub](https://github.com/rodrigokuroda)
* **Igor Wiese** - *Researcher* - [Igor Wiese's GitHub](https://github.com/igorwiese)
* **Reginaldo Ré** - *Researcher*

See also the list of [contributors](https://github.com/rodrigokuroda/recominer/contributors) who participated in this project.

## License

This project is licensed under the Apache 2.0 License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* This software was developed during Rodrigo Kuroda's Master's Degree at Federal Technological University of Paraná - UTFPR.
