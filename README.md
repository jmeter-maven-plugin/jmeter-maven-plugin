Maven JMeter Plugin
===================

The Maven JMeter Plugin allows you to automate JMeter tests in Maven.

*PLEASE NOTE: The Group ID and Artifact ID have changed since version 1.4!*
-----

This is to bring the project in line with maven naming practice for non-maven plugins and to enable us to upload to the Sonatype OSS repository.  This project is now in the maven central repository.

Community
-----

### Website

Website for this project: [http://jmeter.lazerycode.com/](http://jmeter.lazerycode.com/)

A couple of google groups have been created for discussion about this plugin:

### Users Group

A place to discuss usage of the maven-jmeter-plugin, let people know how you use it here.

Homepage: [http://groups.google.com/group/maven-jmeter-plugin-users](http://groups.google.com/group/maven-jmeter-plugin-users)

Group Email: [maven-jmeter-plugin-users@googlegroups.com](mailto:maven-jmeter-plugin-users@googlegroups.com)

### Devs Group

A place to discuss the development of the maven-jmeter-plugin, or ask about features you would like to see added.

Homepage: [http://groups.google.com/group/maven-jmeter-plugin-devs](http://groups.google.com/group/maven-jmeter-plugin-devs)

Group Email: [maven-jmeter-plugin-devs@googlegroups.com](mailto:maven-jmeter-plugin-devs@googlegroups.com)

Build Server
-----

You can see the state of the current codebase by looking at the CI server - [http://ci.lazerycode.com](http://ci.lazerycode.com)

Full Documentation
-------

Up to date documentation is available by checking out the code and running `mvn site:run` on the command line, this information will also be mirrored on [http://jmeter.lazerycode.com](http://jmeter.lazerycode.com) in the near future.

Basic Usage
-----

### Add the plugin to your project

* Add the plugin to the build section of your pom's project :

		<plugin>
			<groupId>com.lazerycode.jmeter</groupId>
			<artifactId>jmeter-maven-plugin</artifactId>
			<version>1.4.1</version>
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

See the full documentation for information on how to configure the plugin.

Contributing
------------

1. Fork it.
2. Create a branch (`git checkout -b my_plugin`)
3. Commit your changes (`git commit -am "Added feature"`)
4. Push to the branch (`git push origin my_plugin`)
5. Create an [Issue][1] with a link to your branch
6. Enjoy a refreshing Diet Coke and wait
