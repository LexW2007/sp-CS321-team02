#!/usr/bin/env bash

echo
echo "Checking your dump files against sample dump files..."
echo

for value in accepted-ip accepted-time invalid-ip invalid-time failed-ip failed-time reverseaddress-ip reverseaddress-time user-ip
do
	echo
	dos2unix output/dump-files/dump-$value.0.txt >& /dev/null

	sort --stable output/dump-files/dump-$value.0.txt > sort1
	mv sort1 "output/dump-files/dump-$value.0.txt"

	sort --stable results/dump-files/dump-$value.0.txt > sort2
	mv sort2 "results/dump-files/dump-$value.0.txt"

	diff -w "output/dump-files/dump-$value.0.txt" "results/dump-files/dump-$value.0.txt" > diff.log
	if test "$?" = "0"
	then
		echo "Test type $value:  PASSED!"
	else
		echo "Test type $value:  FAILED! See diff below"
		echo
		cat diff.log
		/bin/rm -f diff.log
		echo
	fi
done

/bin/rm -f sort1 sort2
echo


