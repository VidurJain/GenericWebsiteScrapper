#!/bin/sh
#author: Parag Arora (parag.arora@gmail.com)
#use this script while deploying gettify server
stty -echo 
read -p "Enter mysql root password: " password; echo 
stty echo
echo "Creating user gettify"
mysql -uroot -p$password -e"create user 'gettify'@'localhost' identified by 'Gettify123'"
echo "Granting all on *.* to gettify"
mysql -uroot -p$password -e"grant all on *.* to 'gettify'@'localhost'"