FROM ubuntu:16.04

MAINTAINER Rodrigo Kuroda <rodrigokuroda@alunos.utfpr.edu.br>

# Update apt
RUN apt-get update -y && apt-get upgrade -y

# Install add-apt-repository command
RUN apt-get install -y python-software-properties software-properties-common

# Enabling repositories
RUN add-apt-repository main && \
  add-apt-repository universe && \
  add-apt-repository restricted && \
  add-apt-repository multiverse

# Install Oracle Java 8: accept license, add third party repository, install and clean
RUN \
  echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
  add-apt-repository -y ppa:webupd8team/java && \
  apt-get update && \
  apt-get install -y oracle-java8-installer && \
  rm -rf /var/lib/apt/lists/* && \
  rm -rf /var/cache/oracle-jdk8-installer

# Update apt
RUN apt-get update -y && apt-get upgrade -y

# Install  CSV, SVN, Git, MySQL Server, Python and its dependencies for Bicho/CVSAnalY and Recominer
RUN apt-get install -y --no-install-recommends wget unzip oracle-java8-installer oracle-java8-set-default openssh-client \
  cvs subversion git r-base r-base-dev python python-openssl \
  python-mysqldb python-storm python-launchpadlib python-beautifulsoup python-feedparser python-dateutil python-setuptools \ 
  && apt-get clean

# Install MySQL 5.7 with default password as 'root' for root user
RUN bash -c 'debconf-set-selections <<< "mysql-server-5.7 mysql-server/root_password password root"'
RUN bash -c 'debconf-set-selections <<< "mysql-server-5.7 mysql-server/root_password_again password root"'
RUN apt-get -y install mysql-server-5.7

# Create directory where Bicho, RepositoryHandler and CVSAnalY from MetricsGrimoire will be placed
RUN mkdir /opt/MetricsGrimoire 
WORKDIR /opt/MetricsGrimoire

# Downloading Bicho, RepositoryHandler and CVSAnalY from GitHub
RUN \
  git clone https://github.com/MetricsGrimoire/Bicho.git /opt/MetricsGrimoire/Bicho && \
  git clone https://github.com/MetricsGrimoire/RepositoryHandler.git /opt/MetricsGrimoire/RepositoryHandler && \
  git clone https://github.com/MetricsGrimoire/CVSAnalY.git /opt/MetricsGrimoire/CVSAnalY

# Install Bicho
WORKDIR /opt/MetricsGrimoire/Bicho
RUN python setup.py install

# Install RepositoryHandler
WORKDIR /opt/MetricsGrimoire/RepositoryHandler
RUN python setup.py install

# Install CVSAnalY
WORKDIR /opt/MetricsGrimoire/CVSAnalY
RUN python setup.py install
