FROM openjdk:8

MAINTAINER Rodrigo Kuroda <rodrigokuroda@alunos.utfpr.edu.br>

# Update apt
RUN apt-get update -y && apt-get upgrade -y

# Install wget
RUN apt-get install -y --no-install-recommends wget ca-certificates \ 
  && apt-get clean

# Download Recominer
RUN wget -P /opt/recominer https://github.com/rodrigokuroda/recominer/releases/download/0.7/recominer-web-0.7.jar

# Dynamically create database URL and run Recominer Web
ADD setEnv.sh /opt
RUN ["chmod", "+x", "/opt/setEnv.sh"]
CMD ["./opt/setEnv.sh"]

