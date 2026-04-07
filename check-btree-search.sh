#!/usr/bin/env bash

for value in accepted-ip accepted-time invalid-ip invalid-time failed-ip failed-time reverseaddress-ip reverseaddress-time user-ip
do
	echo
	echo -n "Test QUERY-$value.txt: "
	dos2unix output/btree-search/"QUERY-"$value".0.txt" >& /dev/null

	sort --stable output/btree-search/"QUERY-"$value".0.txt" > sort1
	mv sort1 output/btree-search/"QUERY-"$value".0.txt"

	sort --stable results/btree-search/query-"$value".0.txt > sort2
	mv sort2 results/btree-search/query-"$value".0.txt

  	diff -w output/btree-search/"QUERY-"$value".0.txt" "results/btree-search/"query-"$value".0.txt"" > diff.log
  	if test "$?" = "0"
  	then
    	echo "  PASSED!"
	else
    	echo "  FAILED! See diff below"
		echo
		cat diff.log
		/bin/rm -f diff.log
		echo
  	fi
done
echo

for value in accepted-ip accepted-time invalid-ip invalid-time failed-ip failed-time reverseaddress-ip reverseaddress-time user-ip
do
	echo
	dos2unix output/btree-search/QUERY-"$value"-top25.0.txt >& /dev/null

    sort --stable "output/btree-search/QUERY-$value-top25.0.txt" > sort1
    mv sort1 "output/btree-search/QUERY-$value-top25.0.txt"

    sort --stable results/db-search/"$value"-top25.txt > sort2
    mv sort2 "results/db-search/$value-top25.txt"

	cat "output/btree-search/QUERY-$value-top25.0.txt" | awk '{print $2}' | sort -rn  > count1
	cat "results/db-search/$value-top25.txt" | awk '{print $2}' | sort -rn  > count2
	diff -w count1 count2
  	if test "$?" = "0"
  	then
		echo "Test top 25 frequencies on QUERY-$value.txt:  PASSED!"
	  else
		echo "Test top 25 frequencies on QUERY-$value.txt:  FAILED! See diff below"
		# do a full diff if counts don't match
  		diff -w "output/btree-search/QUERY-$value-top25.0.txt" "results/db-search/$value-top25.txt" > diff.log
		echo
		cat diff.log
		/bin/rm -f diff.log
		echo
  	fi
done
echo

/bin/rm -f sort1 sort2
/bin/rm -f count1 count2
