#!/bin/bash

date

DIR=$1

cd $DIR

for file in `find . -name "*.c" -o -name "*.h" -type f`
do
	echo "Working on $file..."
	git log --pretty=oneline php-5.0.0..php-5.3.9 $file
done

echo "Done."

date
