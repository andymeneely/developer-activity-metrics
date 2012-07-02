#!/bin/sh

REPO=./churn-repo

rm -rf $REPO

mkdir $REPO
cd $REPO
git init

git config user.name "Alice"
git config user.email "alice@example.com"

echo -e "a\r\nb\r\nc\r\nd\r\ne\r\nf\r\ng\r\n" > a.txt
cp a.txt b.txt
cp a.txt c.txt

git add .
git commit -m "initial import"

git config user.name "Bob"
git config user.email "bob@example.com"

sed -i 's/b/bb/g' a.txt

git add . 
git commit -m "changing b -> bb in a.txt"

sed -i 's/bb/bbb/g' a.txt
sed -i 's/a/aa/g' a.txt

git add . 
git commit -m "changing bb->bbb, a->aa in a.txt"

