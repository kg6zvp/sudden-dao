#!/bin/bash

# import keybase private/public keypair

#keybase pgp export -s | gpg --allow-secret-key-import --import -
#keybase pgp export | gpg --import -

# set version to release
#mvn versions:set -DnewVersion=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec|sed 's/-SNAPSHOT//')

mvn clean deploy -P release
