Maven JMeter Plugin
===================

The Maven JMeter Plugin allows you to automate JMeter tests in Maven.

This fork uses the official Apache JMeter maven artifacts

*For the latest documentation do a "mvn site:run" in the terminal*

*PLEASE NOTE: The Group ID and Artifact ID have changed!*
-----

 This is to bring the project in line with maven naming practice for non-maven plugins and to enable us to upload to the Sonatype OSS repos.  This means the next release of this project will be promoted to the maven central repo.

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


Usage
-----

### Add the plugin to your project

* Add this projects Maven repository to your project (or personal maven repo):

        <pluginRepositories>
		    <pluginRepository>
            	<id>Sonatype Repository</id>
            	<url>https://oss.sonatype.org/content/groups/staging</url>
            </pluginRepository>
		</pluginRepositories>

* Add the plugin to the build section of your pom's project :

		<plugin>
			<groupId>org.apache.jmeter</groupId>
			<artifactId>jmeter-maven-plugin</artifactId>
			<version>1.4-SNAPSHOT</version>
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

This one should be simple enough, unless you don't know about JMeter.  It can be a bit daunting and counterintuitive at first, but once you start to get the hang of it, it will be like second nature, and you will realize how powerful the tool can be.

Once you create your JMeter tests, you'll want to copy them to : `<Project Dir>/src/test/jmeter`

### Run the tests

	mvn verify

All your tests should run in maven now!

Full documentation
-------

Up to date documentation is available checking out the code and running `mvn site:run` on the command line, this information will also be mirrored on [http://jmeter.lazerycode.com](http://jmeter.lazerycode.com) in the near future.

Contributing
------------

1. Fork it.
2. Create a branch (`git checkout -b my_plugin`)
3. Commit your changes (`git commit -am "Added feature"`)
4. Push to the branch (`git push origin my_plugin`)
5. Create an [Issue][1] with a link to your branch
6. Enjoy a refreshing Diet Coke and wait