#!/usr/bin/env bash
if [ -d "$HOME/.m2/repository/org/spigotmc/spigot/$1-R0.1-SNAPSHOT" ]; 
    then echo "Not compiling Spigot $1 because it is already in the maven repository!" ; exit 0;
else echo "Compiling Spigot $1 because it is not in the maven repository!" ; 
fi ;
mkdir -p $HOME/spigot-build
pushd $HOME/spigot-build

echo "Downloading Spigot Build Tools for minecraft version $1"
wget https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar -O $HOME/spigot-build/BuildTools.jar

git config --global --unset core.autocrlf

echo "Building Spigot using Spigot Build Tools for minecraft version $1 (this might take a while)"
java -jar BuildTools.jar --rev $1 | grep Installing

echo "Installing Spigot jar in Maven"
mvn install:install-file -Dfile=$HOME/spigot-build/spigot-$1.jar -Dpackaging=jar -DpomFile=$HOME/spigot-build/Spigot/Spigot-Server/pom.xml
echo "Installing CraftBukkit jar in Maven"
mvn install:install-file -Dfile=$HOME/spigot-build/craftbukkit-$1.jar -Dpackaging=jar -DpomFile=$HOME/spigot-build/CraftBukkit/pom.xml
popd
