#!/bin/bash

rm -f *.jks
rm -f *.cert

uPwd="123users"
fPwd="123feeds"
cPwd="123client"

for i in {0..2}; do
  for j in {0..2}; do
    f="feeds$j-ourorg$i.jks"
    h="users$i-ourorg$j.jks"
    g="users$i-ourorg$j.cert"
    l="feeds$j-ourorg$i.cert"
    n="users$i-ourorg$j"
    m="feeds$j-ourorg$i"
    k="client-ts.jks"

    if [ $i -eq 0 ]; then
      keytool -genkeypair -alias "$n" -keyalg RSA -validity 365 -keystore "$h" -storetype pkcs12 -ext SAN=dns:"$n" -dname "CN=Users.Users, OU=TP2, O=SD2223, L=LX, S=LX, C=PT" -storepass "$uPwd" -keypass "$uPwd"

      keytool -exportcert -alias "$n" -keystore "$h" -file "$g" -storepass "$uPwd"

      keytool -importcert -file "$g" -alias "$n" -keystore "$k" -storepass "$cPwd" -noprompt
    fi

    keytool -genkeypair -alias "$m" -keyalg RSA -validity 365 -keystore "$f" -storetype pkcs12 -ext SAN=dns:"$m" -dname "CN=Feeds.Feeds, OU=TP2, O=SD2223, L=LX, S=LX, C=PT" -storepass "$fPwd" -keypass "$fPwd"

    keytool -exportcert -alias "$m" -keystore "$f" -file "$l" -storepass "$fPwd"

    keytool -importcert -file "$l" -alias "$m" -keystore "$k" -storepass "$cPwd" -noprompt
  done
done
