#!/bin/bash

date

DIR=$1
OUT=$2

cd $DIR

for file in `find . -name "*.c" -o -name "*.h" -type f`
do
	echo $file
	outfile=`echo $file | sed 's/\//\~/g'`
	#echo $outfile
	git blame --incremental $file > $OUT/$outfile
done

echo "Done."

date
