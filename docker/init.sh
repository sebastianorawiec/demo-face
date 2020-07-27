#! /bin/sh
service ssh start
# we pass ENCPASS as environment variable
java -Dspring.profiles.active=azure -Djasypt.encryptor.password=$ENCPASS -jar demo-0.0.1-SNAPSHOT.jar
