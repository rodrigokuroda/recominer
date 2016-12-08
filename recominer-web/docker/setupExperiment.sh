#!/bin/bash
# Author: Rodrigo Kuroda <rodrigokuroda@alunos.utfpr.edu.br>
set -e

echo "Downloading workspaces..."
if [ ! -d ~/recominer/workspace ]; then
    mkdir ~/recominer/workspace 
fi

if [ ! -d ~/recominer/workspace/CXF-3742.zip ]; then
    wget -P ~/recominer/workspace https://github.com/rodrigokuroda/recominer/releases/download/0.7/CXF-3742.zip
    unzip -d ~/recominer/workspace/CXF-3742 ~/recominer/workspace/CXF-3742.zip
fi

if [ ! -d ~/recominer/workspace/CXF-4220.zip]; then
    wget -P ~/recominer/workspace https://github.com/rodrigokuroda/recominer/releases/download/0.7/CXF-4220.zip
    unzip -d ~/recominer/workspace/CXF-4220 ~/recominer/workspace/CXF-4220.zip
fi

if [ ! -d ~/recominer/workspace/CXF-4872.zip]; then
    wget -P ~/recominer/workspace https://github.com/rodrigokuroda/recominer/releases/download/0.7/CXF-4872.zip
    unzip -d ~/recominer/workspace/CXF-4872 ~/recominer/workspace/CXF-4872.zip
fi

if [ ! -d ~/recominer/workspace/CXF-5426.zip]; then
    wget -P ~/recominer/workspace https://github.com/rodrigokuroda/recominer/releases/download/0.7/CXF-5426.zip
    unzip -d ~/recominer/workspace/CXF-5426 ~/recominer/workspace/CXF-5426.zip
fi

if [ ! -d ~/recominer/workspace/CXF-6105.zip]; then
    wget -P ~/recominer/workspace https://github.com/rodrigokuroda/recominer/releases/download/0.7/CXF-6105.zip
    unzip -d ~/recominer/workspace/CXF-6105 ~/recominer/workspace/CXF-6105.zip
fi

if [ ! -d ~/recominer/workspace/CXF-6122.zip]; then
    wget -P ~/recominer/workspace https://github.com/rodrigokuroda/recominer/releases/download/0.7/CXF-6105.zip
    unzip -d ~/recominer/workspace/CXF-6122 ~/recominer/workspace/CXF-6105.zip
fi

echo "Downloading databases..."
if [ ! -f ~/recominer/experiment-db.zip ]; then 
    wget -P ~/recominer https://github.com/rodrigokuroda/recominer/releases/download/0.7/experiment-db.zip
    unzip -d ~/recominer ~/recominer/experiment-db.zip
fi

echo "Restoring databases..."
mysql -uroot -e "CREATE DATABASE IF NOT EXISTS cxf_issues CHARACTER SET utf8 COLLATE utf8_general_ci"
mysql -uroot -e "CREATE DATABASE IF NOT EXISTS cxf_vcs CHARACTER SET utf8 COLLATE utf8_general_ci" 
mysql -uroot -e "CREATE DATABASE IF NOT EXISTS cxf CHARACTER SET utf8 COLLATE utf8_general_ci" 
mysql -uroot -e "CREATE DATABASE IF NOT EXISTS recominer" 
mysql -uroot cxf_issues < ~/recominer/cxf_issues.sql
mysql -uroot cxf_vcs < ~/recominer/cxf_vcs.sql
mysql -uroot cxf < ~/recominer/cxf.sql
mysql -uroot recominer < ~/recominer/recominer.sql

echo "Starting Recominer..."
if [ ! -f ~/recominer/recominer-web.zip ]; then 
	wget -P ~/recominer https://github.com/rodrigokuroda/recominer/releases/download/0.7/recominer-web-0.7.jar
fi

java -Xmx1024m -Xms256m -Xss256k -jar ~/recominer/recominer-web-0.7.jar
