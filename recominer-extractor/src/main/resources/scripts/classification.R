options(echo=TRUE) # if you want see commands in output file / , warn=2

library("caret")
library("mlbench")
library("AppliedPredictiveModeling")
library("randomForest")
library("e1071")

set.seed(10)

# Reading parameters passed
args <- commandArgs(trailingOnly = TRUE)
print(args)

workingDirectory <- args[1]
projectName <- args[2]
commit <- args[3]
file <- args[4]
cochange <- args[5]

# Folder structure: PROJECT/ISSUE/COMMIT/FILE
resultFolder <- paste(workingDirectory, projectName, commit, file, sep = "/")
print(paste("Result folder: ", resultFolder))
setwd(resultFolder)

sub.folders <- list.dirs(resultFolder, recursive=FALSE)

for(cochangeSubfolder in sub.folders) {
  trainFile <- paste(cochangeSubfolder, "train.csv", sep = "/")
  testFile <- paste(resultFolder, "test.csv", sep = "/")
  
  print(paste("Reading data from", resultFolder))
  train <- read.csv(trainFile, sep=";", na.strings=c("NA",""))
  test <- read.csv(testFile, sep=";", na.strings=c("NA",""))
  
  file1Id <- train[1,1]
  file1 <- train[1,2]
  file2Id <- train[1,3]
  file2 <- train[1,4]
  
  #cochanged<-train[,c("cochanged")]
  train<-train[,c("commenters", "devCommenters", "issueAge", "wordinessBody", "wordinessComments", "comments", "btwMdn", "clsMdn", "dgrMdn", "efficiencyMdn", "efvSizeMdn", "constraintMdn", "hierarchyMdn", "size", "ties", "density", "diameter", "committers", "commits", "fileAge", "addedLines", "deletedLines", "changedLines", "cochanged")]
  test<-test[,c("commenters", "devCommenters", "issueAge", "wordinessBody", "wordinessComments", "comments", "btwMdn", "clsMdn", "dgrMdn", "efficiencyMdn", "efvSizeMdn", "constraintMdn", "hierarchyMdn", "size", "ties", "density", "diameter", "committers", "commits", "fileAge", "addedLines", "deletedLines", "changedLines", "cochanged")]
  
  
  # Transforming the class (cochanged == 1 to "Changed", otherwise "Not changed")
  train$cochanged <- as.factor(ifelse(train$cochanged==1, "C", "N"))
  
  # Check if has two factors
  if (length(levels(train$cochanged)) == 2) {
  
    print(paste("Training for file", file1Id, "and", file2Id))
    rf_model <- train(cochanged ~ ., method="rf",  tuneLength = 2, trControl=trainControl(method = "boot"), data=train, importance = FALSE)
    
    # Removing instances with information that no exists in test dataset
    # For example, if in test dataset has an instance that issueType is Task
    # and in train does not exist a issueType Task, then ommit the instance.
    test<-na.omit(test)
  
    print("Predicting...")
  
    # Has obs. in test?
    if (nrow(test) > 0) {
      predicted <- predict(rf_model, test)
      # gets the first one (and only one) probability for predicted class (C or N)
      probability <- predict(rf_model, test, type = "prob")[1, predicted]
    } else {
      predicted <- NA
      probability <- NA
    }
  } else {
    print(paste("Train file", trainFile, "does not have two factors for training."))
    predicted <- NA
    probability <- NA
  }
  output <- data.frame(file2Id, file2, predicted, probability, stringsAsFactors=TRUE)
  write.table(output, "resultsTest.csv", append=TRUE, eol = "\n", sep=";", col.names = F, row.names = F)  
}