#!/bin/bash
# Author: Rodrigo Kuroda <rodrigokuroda@alunos.utfpr.edu.br>

# Building recominer database container
docker build -t recominer-db:0.1 -f Dockerfile-Database .

# Creating directory to persistence data from database container
mkdir ~/data

# Running container
# Persistence data will be stored in host directory "~/data"
docker run --name recominer-db -v ~/data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=root -d recominer-db:0.1

# Restore dump
docker exec recominer-db /opt/recominer/setupDatabase.sh

docker run --name recominer-db -v ~/data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=root -d recominer-db:0.1

# Building recominer web app container
docker build -t recominer-web:0.1 -f Dockerfile .

# Running recominer web app, linking with database container and listening on port 8080 
docker run --name recominer-web --link recominer-db:mysql -p 8080:8080 -d recominer-web:0.1
