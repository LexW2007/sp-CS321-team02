#!/usr/bin/env bash

for value in accepted-ip accepted-time invalid-ip invalid-time failed-ip failed-time reverseaddress-ip reverseaddress-time user-ip
do
  echo
  dos2unix output/db-search/DB-QUERY-"$value"-top25.txt >& /dev/null

  sort --stable "output/db-search/DB-QUERY-$value-top25.txt" > dbsort1
  mv dbsort1 "output/db-search/DB-QUERY-$value-top25.txt"

  sort --stable "results/db-search/$value-top25.txt" > dbsort2
  mv dbsort2 "results/db-search/$value-top25.txt"

  cat "output/db-search/DB-QUERY-$value-top25.txt" | awk '{print $2}' | sort -rn  > count1
  cat "results/db-search/$value-top25.txt" | awk '{print $2}' | sort -rn  > count2
  diff -w count1 count2
  if test "$?" = "0"
  then
  	echo "Test DB-QUERY-$value.txt:  PASSED!"
  else
  	echo "Test DB-QUERY-$value.txt:  FAILED!"
	echo
	# do a full diff if counts don't match
    diff -w "output/db-search/DB-QUERY-$value-top25.txt" "results/db-search/$value-top25.txt" > diff.log
	cat diff.log
	/bin/rm -f diff.log
	echo
  fi
done
echo

/bin/rm -f dbsort1 dbsort2
/bin/rm -f count1 count2
