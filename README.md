Maven JMeter Plugin
===================

The Maven JMeter Plugin allows you to automate JMeter tests in Maven.

This fork use the JMeter module tree and the poms attached to the JMeter issue [#49753](https://issues.apache.org/bugzilla/show_bug.cgi?id=49753)


Usage
-----

### Add the plugin to your project

* Add this fork's Maven repository to your project (or personal maven repo):

		<repository>
			<id>Maven JMeter Plugin</id>
			<url>http://yciabaud.github.com/jmeter-maven-plugin/repository</url>
		</repository>

* Add the plugin to the build section of your pom's project :

		<plugin>
			<groupId>org.apache.jmeter</groupId>
			<artifactId>maven-jmeter-plugin</artifactId>
			<version>1.2</version>
			<executions>
				<execution>
					<id>jmeter-tests</id>
					<phase>verify</phase>
					<goals>
						<goal>jmeter</goal>
					</goals>
			   </execution>
			</executions>
			<configuration>
				<reportDir>${project.build.directory}/jmeter-reports</reportDir>
				<jmeterUserProperties>
					<!-- for user properites -->
				</jmeterUserProperties>
			</configuration>
		</plugin>

I'll go over some of the options later.

### Reference JMX files

This one should be simple enough, unless you don't know about JMeter.  It can be a bit daunting and counterintuitive at first, but once you start to get the hang of it, it will be like second nature, and you will realize how powerful the tool can be.

Once you create your JMeter tests, you'll want to copy them to : `<Project Dir>/src/test/jmeter`

### Copy properties files to your Maven project

* Copy over jmeter.properties
* Copy `<JMETER_HOME>/bin/jmeter.properties` to `<Project Dir>/src/test/jmeter`.  

You can make any tweaks necessary to the jmeter.properties file you see fit.

### Run the tests

	mvn verify

All your tests should run in maven now!

Options
-------

* `<jmeterUserProperties>`

In this section, you can define properties in JMeter files.  For example, you could define a hostname in your JMeter test like this: `${__P(someVariableName, localhost)`

Then, in your pom.xml, you can define that variable like so:

		<jmeterUserProperties>
		   <someVariableName>hostname.com</someVariableName>
		</jmeterUserProperties>

This has the same effect as using -D on the command line, as described here (search for section 2.4.7).

You should now have all you need to run maven-jmeter-plugin.


Contributing
------------

1. Fork it.
2. Create a branch (`git checkout -b my_plugin`)
3. Commit your changes (`git commit -am "Added feature"`)
4. Push to the branch (`git push origin my_plugin`)
5. Create an [Issue][1] with a link to your branch
6. Enjoy a refreshing Diet Coke and wait

