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
	echo "***** File $file *****" >> $OUTFILE
	for revision in `git log --pretty=%H php-5.0.0..php-5.3.9 -- $file`
	do
		echo "***** Revision $revision *****" >> $OUTFILE
		ruby ../git-interaction-churn.rb $revision $file >> $OUTFILE
		echo "***** End *****" >> $OUTFILE
	done
done

echo "Done."

date
