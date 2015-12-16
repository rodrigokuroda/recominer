#!/bin/bash
if [ -z ${1} ] || [ -z ${2} ]
then
  echo 'Empty parameters. Example: ./miner.sh ProjectName URL';
  exit 1;
fi
if [ ! -e ${1} ]
then
  echo 'Creating directory: '${1};
  mkdir ${1};
fi
DB_NAME=$(echo ${1} | awk '{print tolower($0)}');
PROJECT_NAME=$(echo ${1} | awk '{print toupper($0)}');
echo 'Database: '${DB_NAME}'_issues';
echo 'Initializing mining...';
#mysql -uroot -proot -e "drop schema ${1}_issues";
mysql -uroot -proot -e "create schema ${1}_issues";
bicho --backend-user=chavesrules --backend-password=acapulcorules --db-user-out=root --db-password-out=root --db-database-out=${DB_NAME}_issues -d 20 -g -b bg -u "${2}" >> ~/Bicho/${1}/miner.log
exit 0;