-- MySQL dump 10.13  Distrib 5.6.28, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: avro
-- ------------------------------------------------------
-- Server version	5.6.28-0ubuntu0.15.04.1-log

--
-- Table structure for table `ar_prediction`
--
DROP SCHEMA `avro_test`;
CREATE SCHEMA `avro_test`;

DROP TABLE IF EXISTS `avro_test`.`ar_prediction`;
CREATE TABLE `avro_test`.`ar_prediction` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `commit_id` int(11) NOT NULL,
  `fileset_id` int(11) NOT NULL,
  `predicted_fileset_id` int(11) NOT NULL,
  `rank` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `fileset_id` (`fileset_id`,`commit_id`,`predicted_fileset_id`)
) ;

--
-- Table structure for table `commit_metrics`
--

DROP TABLE IF EXISTS `avro_test`.`commit_metrics`;
CREATE TABLE `avro_test`.`commit_metrics` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `commit_id` int(11) NOT NULL,
  `committer_id` int(11) NOT NULL,
  `committer_name` varchar(64) NOT NULL,
  `revision` varchar(64) NOT NULL,
  `date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `commit_id` (`commit_id`)
) ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `commits`
--

DROP TABLE IF EXISTS `avro_test`.`commits`;
CREATE TABLE `avro_test`.`commits` (
  `commit_id` int(11) DEFAULT NULL,
  `rev` varchar(40) DEFAULT NULL,
  `committer_id` int(11) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `message` longtext,
  `repository_id` int(11) DEFAULT NULL,
  `action_type` varchar(1) DEFAULT NULL,
  `branch_id` int(11) DEFAULT NULL,
  KEY `commit_id` (`commit_id`),
  KEY `committer_id` (`committer_id`),
  KEY `date` (`date`),
  KEY `repository_id` (`repository_id`),
  KEY `action_type` (`action_type`),
  KEY `branch_id` (`branch_id`)
) ;


--
-- Table structure for table `communication_network_metrics`
--

DROP TABLE IF EXISTS `avro_test`.`communication_network_metrics`;
CREATE TABLE `avro_test`.`communication_network_metrics` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `issue_id` int(11) NOT NULL,
  `commit_id` int(11) DEFAULT NULL,
  `betweenness_sum` double DEFAULT NULL,
  `betweenness_mean` double DEFAULT NULL,
  `betweenness_median` double DEFAULT NULL,
  `betweenness_maximum` double DEFAULT NULL,
  `closeness_sum` double DEFAULT NULL,
  `closeness_mean` double DEFAULT NULL,
  `closeness_median` double DEFAULT NULL,
  `closeness_maximum` double DEFAULT NULL,
  `degree_sum` double DEFAULT NULL,
  `degree_mean` double DEFAULT NULL,
  `degree_median` double DEFAULT NULL,
  `degree_maximum` double DEFAULT NULL,
  `efficiency_sum` double DEFAULT NULL,
  `efficiency_mean` double DEFAULT NULL,
  `efficiency_median` double DEFAULT NULL,
  `efficiency_maximum` double DEFAULT NULL,
  `effective_size_sum` double DEFAULT NULL,
  `effective_size_mean` double DEFAULT NULL,
  `effective_size_median` double DEFAULT NULL,
  `effective_size_maximum` double DEFAULT NULL,
  `constraint_sum` double DEFAULT NULL,
  `constraint_mean` double DEFAULT NULL,
  `constraint_median` double DEFAULT NULL,
  `constraint_maximum` double DEFAULT NULL,
  `hierarchy_sum` double DEFAULT NULL,
  `hierarchy_mean` double DEFAULT NULL,
  `hierarchy_median` double DEFAULT NULL,
  `hierarchy_maximum` double DEFAULT NULL,
  `size` double DEFAULT NULL,
  `ties` double DEFAULT NULL,
  `density` double DEFAULT NULL,
  `diameter` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `issue_id_commit_id` (`issue_id`,`commit_id`)
);

--
-- Table structure for table `file_apriori`
--

DROP TABLE IF EXISTS `avro_test`.`file_apriori`;
CREATE TABLE `avro_test`.`file_apriori` (
  `file_id` int(11) NOT NULL,
  `file_issues` int(11) NOT NULL,
  `updated_on` datetime DEFAULT NULL,
  PRIMARY KEY (`file_id`)
) ;


--
-- Table structure for table `file_metrics`
--

DROP TABLE IF EXISTS `avro_test`.`file_metrics`;
CREATE TABLE `avro_test`.`file_metrics` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `commit_id` int(11) NOT NULL,
  `file_id` int(11) NOT NULL,
  `additions` int(11) NOT NULL,
  `deletions` int(11) NOT NULL,
  `changes` int(11) NOT NULL,
  `committers` int(11) NOT NULL,
  `commits` int(11) NOT NULL,
  `age` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `file_id` (`file_id`,`commit_id`)
) ;

--
-- Table structure for table `file_pair_apriori`
--

DROP TABLE IF EXISTS `avro_test`.`file_pair_apriori`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `avro_test`.`file_pair_apriori` (
  `file_pair_id` int(11) NOT NULL,
  `file_pair_issues` int(11) NOT NULL,
  `file1_support` double NOT NULL,
  `file2_support` double NOT NULL,
  `file1_issues` int(11) NOT NULL,
  `file2_issues` int(11) NOT NULL,
  `file_pair_support` double NOT NULL,
  `file1_confidence` double NOT NULL,
  `file2_confidence` double NOT NULL,
  `updated_on` datetime DEFAULT NULL,
  PRIMARY KEY (`file_pair_id`)
) ;

-- Table structure for table `file_pair_issue`
--

DROP TABLE IF EXISTS `avro_test`.`file_pair_issue`;
CREATE TABLE `avro_test`.`file_pair_issue` (
  `file_pair_id` int(11) NOT NULL,
  `issue_id` int(11) NOT NULL,
  PRIMARY KEY (`file_pair_id`,`issue_id`),
  KEY `file_pair_id` (`file_pair_id`),
  KEY `issue_id` (`issue_id`)
) ;

--
-- Table structure for table `file_pair_issue_commit`
--

DROP TABLE IF EXISTS `avro_test`.`file_pair_issue_commit`;
CREATE TABLE `avro_test`.`file_pair_issue_commit` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `file1_id` int(11) NOT NULL,
  `file2_id` int(11) NOT NULL,
  `issue_id` int(11) NOT NULL,
  `commit_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_key` (`file1_id`,`file2_id`,`issue_id`,`commit_id`),
  KEY `file1_id` (`file1_id`),
  KEY `file2_id` (`file2_id`),
  KEY `issue_id` (`issue_id`),
  KEY `commit_id` (`commit_id`)
);

--
-- Table structure for table `file_pairs`
--

DROP TABLE IF EXISTS `avro_test`.`file_pairs`;
CREATE TABLE `avro_test`.`file_pairs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `file1_id` int(11) DEFAULT NULL,
  `file2_id` int(11) DEFAULT NULL,
  `updated_on` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `file1_id` (`file1_id`),
  KEY `file2_id` (`file2_id`)
) ;

--
-- Table structure for table `files`
--

DROP TABLE IF EXISTS `avro_test`.`files`;
CREATE TABLE `avro_test`.`files` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `fl_id` int(11) NOT NULL,
  `file_path` varchar(4096) NOT NULL,
  `f_id` int(11) NOT NULL,
  `file_name` varchar(4096) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `f_id` (`f_id`),
  KEY `fl_id` (`fl_id`)
);

--
-- Table structure for table `files_commits`
--

DROP TABLE IF EXISTS `avro_test`.`files_commits`;
CREATE TABLE `avro_test`.`files_commits` (
  `file_id` int(11) NOT NULL,
  `commit_id` int(11) NOT NULL,
  `change_type` varchar(1) NOT NULL,
  `branch_id` int(11) NOT NULL,
  `lines_added` int(11) NOT NULL,
  `lines_removed` int(11) NOT NULL,
  PRIMARY KEY (`file_id`,`commit_id`)
) ;

DROP TABLE IF EXISTS `avro_test`.`fileset`;
CREATE TABLE `avro_test`.`fileset` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `file_id` int(11) NOT NULL,
  UNIQUE KEY `id` (`id`,`file_id`)
);

--
-- Table structure for table `fileset_sequence`
--

DROP TABLE IF EXISTS `avro_test`.`fileset_sequence`;
CREATE TABLE `avro_test`.`fileset_sequence` (
  `id` int(11) NOT NULL
) ;

--
-- Table structure for table `issue_commit_historical`
--

DROP TABLE IF EXISTS `avro_test`.`issue_commit_historical`;
CREATE TABLE `avro_test`.`issue_commit_historical` (
  `issue_id` int(11) DEFAULT NULL,
  `commit_id` int(11) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `rev` varchar(40) DEFAULT NULL,
  `num_comments` int(11) DEFAULT NULL,
  `num_commenters` int(11) DEFAULT NULL,
  `num_dev_commenters` int(11) DEFAULT NULL,
  `reopened_times` int(11) DEFAULT NULL,
  KEY `commit_id` (`commit_id`),
  KEY `date` (`date`)
) ;

--
-- Table structure for table `issues_fix_version`
--

DROP TABLE IF EXISTS `avro_test`.`issues_fix_version`;
CREATE TABLE `avro_test`.`issues_fix_version` (
  `issue_id` int(11) NOT NULL,
  `fix_version` varchar(255) NOT NULL,
  `minor_fix_version` varchar(255) NOT NULL,
  `major_fix_version` varchar(255) NOT NULL,
  UNIQUE KEY `unq_issue_fix_version` (`issue_id`,`fix_version`),
  KEY `issue_id` (`issue_id`),
  KEY `fix_version` (`fix_version`),
  KEY `minor_fix_version` (`minor_fix_version`),
  KEY `major_fix_version` (`major_fix_version`)
) ;

--
-- Table structure for table `issues_fix_version_order`
--

DROP TABLE IF EXISTS `avro_test`.`issues_fix_version_order`;
CREATE TABLE `avro_test`.`issues_fix_version_order` (
  `minor_fix_version` varchar(255) NOT NULL,
  `major_fix_version` varchar(255) NOT NULL,
  `version_order` int(11) NOT NULL,
  UNIQUE KEY `unq_major_fix_version_order` (`major_fix_version`,`version_order`),
  KEY `minor_fix_version` (`minor_fix_version`),
  KEY `major_fix_version` (`major_fix_version`),
  KEY `version_order` (`version_order`)
) ;

--
-- Table structure for table `issues_metrics`
--

DROP TABLE IF EXISTS `avro_test`.`issues_metrics`;
CREATE TABLE `avro_test`.`issues_metrics` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `issue_id` int(11) NOT NULL,
  `commit_id` int(11) DEFAULT NULL,
  `issue_key` varchar(64) NOT NULL,
  `issue_type` varchar(64) NOT NULL,
  `priority` varchar(64) NOT NULL,
  `assigned_to` varchar(64) NOT NULL,
  `submitted_by` varchar(64) NOT NULL,
  `commenters` int(11) NOT NULL,
  `dev_commenters` int(11) NOT NULL,
  `updated_on` datetime DEFAULT NULL,
  `comments_updated_on` datetime DEFAULT NULL,
  `wordiness_body` int(11) NOT NULL,
  `wordiness_comments` int(11) NOT NULL,
  `age` int(11) NOT NULL,
  `comments` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `issue_id_commit_id` (`issue_id`,`commit_id`)
);

--
-- Table structure for table `issues_scmlog`
--

DROP TABLE IF EXISTS `avro_test`.`issues_scmlog`;
CREATE TABLE `avro_test`.`issues_scmlog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `issue_id` int(11) NOT NULL,
  `scmlog_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unq_issue_scmlog` (`issue_id`,`scmlog_id`),
  KEY `issue_id` (`issue_id`),
  KEY `scmlog_id` (`scmlog_id`)
);

--
-- Table structure for table `ml_prediction`
--

DROP TABLE IF EXISTS `avro_test`.`ml_prediction`;
CREATE TABLE `avro_test`.`ml_prediction` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `commit_id` int(11) NOT NULL,
  `file_id` int(11) NOT NULL,
  `predicted_file_id` int(11) NOT NULL,
  `prediction_result` varchar(2) NOT NULL,
  `algorithm_name` varchar(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `commit_id` (`commit_id`,`file_id`,`predicted_file_id`)
) ;

-- Dump completed on 2016-09-07 14:24:23
