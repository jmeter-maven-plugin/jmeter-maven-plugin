#!/usr/bin/env bash
echo "branch is ${TRAVIS_BRANCH}"
if [[ "${TRAVIS_BRANCH}" == 'main' ]] ; then
    echo "Generating settings.xml"
    mkdir -p ./target
    MVNSETTINGS='
    <settings>
        <servers>
            <server>
                <id>sonatype-nexus-snapshots</id>
                <username>'"${SONATYPE_USER}"'</username>
                <password>'"${SONATYPE_PASS}"'</password>
            </server>
        </servers>
    </settings>'
    echo ${MVNSETTINGS} > ./target/maven-settings.xml
    echo "Attempting to deploy snapshot..."
    mvn deploy -s ./target/maven-settings.xml -DskipTests
else
    echo "Not on main, skipping snapshot deployment"
fi
