#!/bin/sh
OPTIONS="-keystore $1.jks -storepass 123456 -keypass 123456 -alias net_node"
keytool $OPTIONS -genkey -alias net_node -ext EKU=serverAuth,clientAuth
keytool $OPTIONS -export -file $1.cert
