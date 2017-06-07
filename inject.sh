#!/bin/sh

echo "Git project fetched. Starting maven dependency resolution..."
for d in $(find . -name 'pom.xml')
do
    NAME=$(dirname "${d//\.\///Users/jary/redhat/rhmap/fis2-ecom-services/}")
    echo $NAME
    cd "$NAME"
    echo "pom.xml file detected in: $NAME, fetching dependencies..."
    eval 'mvn -Dmaven.repo.local=/tmp/artifacts/m2 package -DskipTests -e -Dfabric8.skip=true -B'
done
echo "maven dependency resolution complete"

