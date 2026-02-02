#!/bin/bash
keytool -genkey -v -keystore release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias omnimind -dname "CN=OmniMind, OU=Dev, O=OmniMind, L=City, S=State, C=US" -storepass password -keypass password
mv release-key.jks app/
echo "Keystore generated in app/release-key.jks"
