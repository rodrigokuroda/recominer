#!/bin/bash
echo "Installing CSV, SVN, Git, MySQL Server, Python and its dependencies..."
sudo apt-get -y install cvs subversion git mysql-server python python-mysqldb python-storm python-launchpadlib python-beautifulsoup python-feedparser python-dateutil python-setuptools

echo "Downloading Bicho, RepositoryHandler and CVSAnalY from Git..."
mkdir MetricsGrimoire
cd MetricsGrimoire/
git clone https://github.com/MetricsGrimoire/Bicho.git
git clone https://github.com/MetricsGrimoire/RepositoryHandler.git
git clone https://github.com/MetricsGrimoire/CVSAnalY.git

cd Bicho/
python setup.py install
sudo python setup.py install

cd ../RepositoryHandler
sudo python setup.py install

cd ../CVSAnalY/
sudo python setup.py install

sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java8-installer
sudo apt-get install maven