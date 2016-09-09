#!/bin/bash
if [ -z ${1} -o -z ${2} ]
then
  echo 'Empty parameters. Example: ./miner.sh ProjectName http://svn.projectname.com/svn';
  exit 1;
fi
if [ ! -e ${1} ]
then
  echo 'Creating directory: '${1};
  mkdir ${1};
fi
DB_NAME=$(echo ${1} | awk '{print tolower($0)}');
echo 'Database: '${DB_NAME}'_vcs';
echo 'Initializing mining...';
#mysql -uroot -proot -e "drop schema ${1}_vcs";
mysql -uroot -proot -e "create schema ${1}_vcs";
cvsanaly2 --debug --writable-path=./${1} --save-logfile=./${1}/vcs_logfile --db-user=root --db-password=root --db-database=${DB_NAME}_vcs --metrics-all --metrics-noerr --extensions=CommitsLOCDet,FileTypes ${2} > ${1}/vcs_miner.log;
exit 0;