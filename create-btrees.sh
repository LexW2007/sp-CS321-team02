#!/usr/bin/env bash

for value in accepted-ip accepted-time invalid-ip invalid-time failed-ip failed-time reverseaddress-ip reverseaddress-time user-ip
do
   time java -jar build/libs/SSHCreateBTree.jar --cache=1 --degree=0 --sshFile=data/SSH_Files/SSH_log.txt --type=$value --cache-size=10000 --database=yes --debug=1
   mv SSH_log.txt.ssh.btree.$value.0 output/btrees/
   mv dump-$value.0.txt output/dump-files/
done
