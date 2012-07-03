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
	for revision in `git log --pretty=oneline php-5.0.0..php-5.3.9 -- $file | awk '{print $1}'`
	do
		echo "***** Revision $revision *****" >> $OUTFILE
		echo "***** File $file *****" >> $OUTFILE
		git blame -l $revision -- $file >> $OUTFILE
		echo "***** End *****" >> $OUTFILE
	done
done

echo "Done."

date
