#!/bin/bash
wget https://cran.r-project.org/src/base/R-3/R-3.3.1.tar.gz
tar -zxvf R-3.3.1.tar.gz
cd ./R-3.3.1
sudo apt-get install xorg-dev libcurl3 libcurl4-openssl-dev
./configure
make
make check
make install
R
install.packages("caret")
install.packages("mlbench")
install.packages("AppliedPredictiveModeling")
install.packages("ROCR")
install.packages("pROC")
install.packages("randomForest")