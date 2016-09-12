options(echo=TRUE) # if you want see commands in output file / , warn=2
#install.packages("caret")
#install.packages("mlbench")
#install.packages("AppliedPredictiveModeling")
#install.packages("ROCR")
#install.packages("pROC")
#install.packages("randomForest")
#install.packages('e1071', dependencies=TRUE)

library("caret")
library("mlbench")
library("AppliedPredictiveModeling")
library("ROCR")
library("pROC")
library("randomForest")

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
paste("Result folder: ", resultFolder)
setwd(resultFolder)

trainFile <- paste(resultFolder, cochange, "train.csv", sep = "/")
testFile <- paste(resultFolder, "test.csv", sep = "/")

paste("Reading data from", resultFolder)
train <- read.csv(trainFile, sep=";")
test <- read.csv(testFile, sep=";")
file1Id <- train[1,1]
file1 <- train[1,2]
file2Id <- train[1,3]
file2 <- train[1,4]

cochanged<-train[,c("cochanged")]
train<-train[,c("issueType", "issuePriority", "issueAssignedTo", "issueSubmittedBy", "commenters", "devCommenters", "issueAge", "wordinessBody", "wordinessComments", "comments", "btwMdn", "clsMdn", "dgrMdn", "efficiencyMdn", "efvSizeMdn", "constraintMdn", "hierarchyMdn", "size", "ties", "density", "diameter", "committer", "committers", "commits", "fileAge", "addedLines", "deletedLines", "changedLines")]
test<-test[,c("issueType", "issuePriority", "issueAssignedTo", "issueSubmittedBy", "commenters", "devCommenters", "issueAge", "wordinessBody", "wordinessComments", "comments", "btwMdn", "clsMdn", "dgrMdn", "efficiencyMdn", "efvSizeMdn", "constraintMdn", "hierarchyMdn", "size", "ties", "density", "diameter", "committer", "committers", "commits", "fileAge", "addedLines", "deletedLines", "changedLines")]

#lastcol <- ncol(train)
#train[,lastcol]
#summary(train)
#summary(test)

# Transforming the class (cochanged == 1 to "Changed", otherwise "Not changed")
cochanged <- as.factor(ifelse(cochanged==1, "C", "N"))

# Transforming string/char metrics in factors
train$issueType <- factor(train$issueType)
train$issueAssignedTo <- factor(train$issueAssignedTo)
train$issueSubmittedBy <- factor(train$issueSubmittedBy)
train$committer <- factor(train$committer)

test$issueType <- factor(test$issueType)
test$issueAssignedTo <- factor(test$issueAssignedTo)
test$issueSubmittedBy <- factor(test$issueSubmittedBy)
test$committer <- factor(test$committer)

print("Training...")
rf_model <- train(cochanged ~ ., method="rf",  tuneLength = 2, trControl=trainControl(method = "boot"), data=train, importance = FALSE)

id <- which(!(test$issueType %in% levels(train$issueType)))
test$issueType[id] <- NA

id <- which(!(test$issueAssignedTo %in% levels(train$issueAssignedTo)))
test$issueAssignedTo[id] <- NA

id <- which(!(test$issueSubmittedBy %in% levels(train$issueSubmittedBy)))
test$issueSubmittedBy[id] <- NA

id <- which(!(test$committer %in% levels(train$committer)))
test$committer[id] <- NA

# Removing instances with information that no exists in test dataset
# For example, if in test dataset has an instance that issueType is Task
# and in train does not exist a issueType Task, then ommit the instance.
test<-na.omit(test)

print("Predicting...")

if (exists("filePredictions")) {
  filePredictions <- factor(c(filePredictions), file2)
  fileIdPredictions <- factor(c(filePredictions), file2Id)
} else {
  filePredictions <- factor(file2)
  fileIdPredictions <- factor(file2Id)
}

# Has obs. in test?
if (nrow(test) > 0) {
  predicted <- predict(rf_model, test)
} else {
  predicted <- NA
}

if (exists("predictions") == FALSE) {
  predictions <- c(predicted)
} else {
  predictions <- c(predictions, predicted);
}

output <- data.frame(factor(fileIdPredictions), factor(filePredictions), predictions, stringsAsFactors=TRUE)

write.table(output, "resultsTest.csv", append=TRUE, eol = "\n", sep=";", col.names = F, row.names = F)  
