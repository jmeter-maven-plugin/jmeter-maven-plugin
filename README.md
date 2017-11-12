#JMeter Maven Plugin
=================================

[![Join the chat at https://gitter.im/jmeter-maven-plugin/jmeter-maven-plugin](https://badges.gitter.im/jmeter-maven-plugin/jmeter-maven-plugin.svg)](https://gitter.im/jmeter-maven-plugin/jmeter-maven-plugin?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/jmeter-maven-plugin/jmeter-maven-plugin.svg?branch=master)](https://travis-ci.org/jmeter-maven-plugin/jmeter-maven-plugin)
[![codecov](https://codecov.io/gh/jmeter-maven-plugin/jmeter-maven-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/jmeter-maven-plugin/jmeter-maven-plugin)
[![Dependency Status](https://www.versioneye.com/user/projects/56e686a3df573d00495abe1d/badge.svg?style=flat)](https://www.versioneye.com/user/projects/56e686a3df573d00495abe1d)
[![GitHub release](https://img.shields.io/github/release/jmeter-maven-plugin/jmeter-maven-plugin.svg)](http://jmeter.lazerycode.com/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.lazerycode.jmeter/jmeter-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.lazerycode.jmeter/jmeter-maven-plugin)
[![JitPack](https://jitpack.io/v/jmeter-maven-plugin/jmeter-maven-plugin.svg)](https://jitpack.io/#jmeter-maven-plugin/jmeter-maven-plugin)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/com.lazerycode.jmeter/jmeter-maven-plugin/badge.svg)](http://www.javadoc.io/doc/com.lazerycode.jmeter/jmeter-maven-plugin)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Stack Overflow](https://img.shields.io/:stack%20overflow-jmeter_maven_plugin-brightgreen.svg)](https://stackoverflow.com/questions/tagged/jmeter-maven-plugin)

A Maven plugin that provides the ability to run JMeter tests as part of your build

See the [CHANGELOG](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/blob/master/CHANGELOG.md) for change information.  

All the documentation you need to configure the plugin is available on the [github wiki](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/wiki).

Last version is **2.5.1** and is compatible with **Apache JMeter 3.3**

This plugin requires **JDK 1.8** or higher since 2.2.0. 

#Basic Usage
-----

### Add the plugin to your project

* Add the plugin to the build section of your pom's project :

		<plugin>
			<groupId>com.lazerycode.jmeter</groupId>
			<artifactId>jmeter-maven-plugin</artifactId>
			<version>2.5.1</version>
			<executions>
				<execution>
					<id>jmeter-tests</id>
					<phase>verify</phase>
					<goals>
						<goal>jmeter</goal>
					</goals>
			   </execution>
			</executions>
		</plugin>

### Reference JMX files

Once you have created your JMeter tests, you'll need to copy them to `<Project Dir>/src/test/jmeter`.  By default this plugin will pick up all the .jmx files in that directory, to specify which tests should be run please see the project documentation.

### Run the tests

	`mvn verify`

All your tests will run in maven!

#Documentation
-----

All the documentation you need to configure the plugin is available on the [github wiki](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/wiki).

Beginners should start with the [Basic Configuration](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/wiki/Basic-Configuration) section.

For advanced POM configuration settings have a look at the [Advanced Configuration](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/wiki/Advanced-Configuration) section.

#Community
-----

## Users Group

A place to discuss usage of the maven-jmeter-plugin, let people know how you use it here.

Homepage: [http://groups.google.com/group/maven-jmeter-plugin-users](http://groups.google.com/group/maven-jmeter-plugin-users)

Group Email: [maven-jmeter-plugin-users@googlegroups.com](mailto:maven-jmeter-plugin-users@googlegroups.com)

## Devs Group

A place to discuss the development of the maven-jmeter-plugin, or ask about features you would like to see added.

Homepage: [http://groups.google.com/group/maven-jmeter-plugin-devs]( http://groups.google.com/group/maven-jmeter-plugin-devs)

Group Email: [maven-jmeter-plugin-devs@googlegroups.com](mailto:maven-jmeter-plugin-devs@googlegroups.com)

## Website

The official website is available at [http://jmeter.lazerycode.com](http://jmeter.lazerycode.com)

#Contributing
------------

1. Fork it.
2. Create a branch (`git checkout -b my_plugin`)
3. Commit your changes (`git commit -am "Added feature"`)
4. Push to the branch (`git push origin my_plugin`)
5. Create a new [Issue](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/issues) with a link to your branch, or just make a Pull Request.
6. Enjoy a refreshing Coffee or Tea or orange juice and wait
