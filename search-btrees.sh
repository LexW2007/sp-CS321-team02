#!/usr/bin/env bash

echo

for value in accepted-ip accepted-time invalid-ip invalid-time failed-ip failed-time reverseaddress-ip reverseaddress-time user-ip
do
	echo "Running btree search on "$value" using query file of all keys"
    time java -jar build/libs/SSHSearchBTree.jar --cache=1 --degree=0 --btree-file=output/btrees/SSH_log.txt.ssh.btree.$value.0 --query-file=data/queries/QUERY-$value.txt --cache-size=10000 --debug=0 > output/btree-search/"QUERY-"$value".0.txt"
done

for value in accepted-ip accepted-time invalid-ip invalid-time failed-ip failed-time reverseaddress-ip reverseaddress-time user-ip
do
	echo "Running btree search top 25 frequencies on "$value" using query file of all keys"
    java -jar build/libs/SSHSearchBTree.jar --cache=1 --degree=0 --btree-file=output/btrees/SSH_log.txt.ssh.btree.$value.0 --query-file=data/queries/QUERY-$value.txt --top-frequency=25 --cache-size=10000 --debug=0 > output/btree-search/"QUERY-"$value"-top25.0.txt"
done


echo
