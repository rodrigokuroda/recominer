sudo debconf-set-selections <<< 'mysql-server mysql-server/root_password root'
sudo debconf-set-selections <<< 'mysql-server mysql-server/root_password_again root'
sudo apt-get -y install mysql-server