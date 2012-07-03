#!/bin/bash

date

DIR=$1
OUTFILE=$2

cd $DIR

rm $OUTFILE
touch $OUTFILE

for file in `find . -name "*.c" -o -name "*.h" -type f`
do
	echo "Working on $file..."
	git log php-5.0.0..php-5.3.9 -p -- $file | grep -i -e "^@@" -e "^commit" >> $OUTFILE
done

echo "Done."

date
