JMeter Maven Plugin
=================================


[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Build Status](https://travis-ci.org/jmeter-maven-plugin/jmeter-maven-plugin.svg?branch=master)](https://travis-ci.org/jmeter-maven-plugin/jmeter-maven-plugin)
[![codecov](https://codecov.io/gh/jmeter-maven-plugin/jmeter-maven-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/jmeter-maven-plugin/jmeter-maven-plugin)

[![GitHub release](https://img.shields.io/github/release/jmeter-maven-plugin/jmeter-maven-plugin.svg?colorB=brightgreen)](http://jmeter.lazerycode.com/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.lazerycode.jmeter/jmeter-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.lazerycode.jmeter/jmeter-maven-plugin)
[![Javadocs](https://www.javadoc.io/badge/com.lazerycode.jmeter/jmeter-maven-plugin.svg)](https://www.javadoc.io/doc/com.lazerycode.jmeter/jmeter-maven-plugin)
[![JitPack](https://jitpack.io/v/jmeter-maven-plugin/jmeter-maven-plugin.svg)](https://jitpack.io/#jmeter-maven-plugin/jmeter-maven-plugin)

[![Stack Overflow](https://img.shields.io/:stack%20overflow-jmeter_maven_plugin-brightgreen.svg)](https://stackoverflow.com/questions/tagged/jmeter-maven-plugin)
[![Open Source Helpers](https://www.codetriage.com/jmeter-maven-plugin/jmeter-maven-plugin/badges/users.svg)](https://www.codetriage.com/jmeter-maven-plugin/jmeter-maven-plugin)
[![Twitter](https://img.shields.io/twitter/url/https/github.com/jmeter-maven-plugin/jmeter-maven-plugin.svg?style=social)](https://twitter.com/intent/tweet?text=Integrate+easily+%40ApacheJMeter+in+your+%23Maven+project+with+jmeter-maven-plugin:&url=https%3A%2F%2Fgithub.com%2Fjmeter-maven-plugin%2Fjmeter-maven-plugin)

A Maven plugin that provides the ability to run JMeter tests as part of your build

See the [CHANGELOG](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/blob/master/CHANGELOG.md) for change information.  

All the documentation you need to configure the plugin is available on the [Github Wiki](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/wiki).

The latest version is **3.1.0**, it requires Maven >= **3.5.2** and defaults to **Apache JMeter 5.2.1**.

This plugin requires a JDK between **8** and **11**. If using Java 11, ensure you use recent version to avoid facing this [Bug](https://bugs.openjdk.java.net/browse/JDK-8210005), also read [This](https://stackoverflow.com/a/52510406/460802). 

Running the GUI
-----

Once you have added the plugin to your project you will be able to invoke the JMeter GUI using the following command:

```
mvn jmeter:configure jmeter:gui
```

If you haven't added the plugin to your project you can still invoke it (provided you have a valid pom.xml in your project) by using the following:

```
mvn com.lazerycode.jmeter:jmeter-maven-plugin:configure com.lazerycode.jmeter:jmeter-maven-plugin:gui
```

Basic Usage
-----

### Add the plugin to your project

Add the plugin to the build section of your pom's project :

```
<plugin>
    <groupId>com.lazerycode.jmeter</groupId>
    <artifactId>jmeter-maven-plugin</artifactId>
    <version>3.1.0</version>
    <executions>
        <!-- Generate JMeter configuration -->
        <execution>
            <id>configuration</id>
            <goals>
                <goal>configure</goal>
            </goals>
        </execution>
        <!-- Run JMeter tests -->
        <execution>
            <id>jmeter-tests</id>
            <goals>
                <goal>jmeter</goal>
            </goals>
        </execution>
        <!-- Fail build on errors in test -->
        <execution>
            <id>jmeter-check-results</id>
            <goals>
                <goal>results</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Reference JMX files and CSV data

Once you have created your JMeter tests, you'll need to copy them to `<Project Dir>/src/test/jmeter`.  
By default this plugin will pick up all the .jmx files in that directory, you can also put data files in this folder and reference them in your plan.
To specify which tests should be run, see the [Selecting-Tests-To-Run](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/wiki/Selecting-Tests-To-Run#2) section of the Wiki.

### Run the tests

```
mvn clean verify
```

All your tests will run in maven!

Documentation
-----

All the plugin configuration documentation is available on the [Github Wiki](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/wiki).

Beginners should start with the [Basic Configuration](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/wiki/Basic-Configuration) section.

For advanced POM configuration settings have a look at the [Advanced Configuration](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/wiki/Advanced-Configuration) section.

Tutorials
-----

- [Shift left your performance tests with JMeter and Maven](https://www.ubik-ingenierie.com/blog/shift-left-performance-tests-jmeter-maven/)

Support
-----

If you'd like to help support the maintainers you can donate using the sponsorship button at the top of the page, or you can purchase this book:

[<kbd><img src="https://raw.githubusercontent.com/jmeter-maven-plugin/jmeter-maven-plugin/master/master-jmeter-from-load-test-to-devops-medium.png" /></kbd>](https://leanpub.com/master-jmeter-from-load-test-to-devops/)

Want to help
-----

Have a look at our list of outstanding issues:

- [Next Milestone](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/milestone/30)

Community
-----

### Users Group

A place to discuss usage of the maven-jmeter-plugin, let people know how you use it here.

Homepage: [http://groups.google.com/group/maven-jmeter-plugin-users](http://groups.google.com/group/maven-jmeter-plugin-users)

Group Email: [maven-jmeter-plugin-users@googlegroups.com](mailto:maven-jmeter-plugin-users@googlegroups.com)

### Devs Group

A place to discuss the development of the maven-jmeter-plugin, or ask about features you would like to see added.

Homepage: [http://groups.google.com/group/maven-jmeter-plugin-devs]( http://groups.google.com/group/maven-jmeter-plugin-devs)

Group Email: [maven-jmeter-plugin-devs@googlegroups.com](mailto:maven-jmeter-plugin-devs@googlegroups.com)

### Website

The official website is available at [https://jmeter.lazerycode.com](https://jmeter.lazerycode.com)

We love it when people [Contribute](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/blob/master/CONTRIBUTING.md)!
