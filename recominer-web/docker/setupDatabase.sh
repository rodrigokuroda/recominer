#!/bin/bash
# Author: Rodrigo Kuroda <rodrigokuroda@alunos.utfpr.edu.br>
set -e
service mysql start
mysql -uroot -p$MYSQL_ROOT_PASSWORD -e "CREATE DATABASE cxf_issues CHARACTER SET utf8 COLLATE utf8_general_ci"
mysql -uroot -p$MYSQL_ROOT_PASSWORD -e "CREATE DATABASE cxf_vcs CHARACTER SET utf8 COLLATE utf8_general_ci" 
mysql -uroot -p$MYSQL_ROOT_PASSWORD -e "CREATE DATABASE cxf CHARACTER SET utf8 COLLATE utf8_general_ci" 
mysql -uroot -p$MYSQL_ROOT_PASSWORD -e "CREATE DATABASE recominer" 
mysql -uroot -p$MYSQL_ROOT_PASSWORD cxf_issues < /opt/recominer/cxf_issues.sql
mysql -uroot -p$MYSQL_ROOT_PASSWORD cxf_vcs < /opt/recominer/cxf_vcs.sql
mysql -uroot -p$MYSQL_ROOT_PASSWORD cxf < /opt/recominer/cxf.sql
mysql -uroot -p$MYSQL_ROOT_PASSWORD recominer < /opt/recominer/recominer.sql
service mysql stop
