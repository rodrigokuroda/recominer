-- MySQL dump 10.13  Distrib 5.6.28, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: recominer
-- ------------------------------------------------------
-- Server version	5.6.28-0ubuntu0.15.04.1-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `association_rule_log`
--

DROP TABLE IF EXISTS `recominer`.`association_rule_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recominer`.`association_rule_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL,
  `type` varchar(64) NOT NULL,
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `last_commit_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `start_end_date` (`start_date`,`end_date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `association_rule_log`
--

LOCK TABLES `recominer`.`association_rule_log` WRITE;
/*!40000 ALTER TABLE `association_rule_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `association_rule_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `calculator_log`
--

DROP TABLE IF EXISTS `recominer`.`calculator_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recominer`.`calculator_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL,
  `metric` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `last_commit_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `start_end_date` (`start_date`,`end_date`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `calculator_log`
--

LOCK TABLES `recominer`.`calculator_log` WRITE;
/*!40000 ALTER TABLE `calculator_log` DISABLE KEYS */;
INSERT INTO `recominer`.`calculator_log` VALUES (1,1,'AllMetrics','2016-08-11 12:25:24','2016-08-11 12:26:16',NULL);
/*!40000 ALTER TABLE `calculator_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `classificator_log`
--

DROP TABLE IF EXISTS `recominer`.`classificator_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recominer`.`classificator_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL,
  `type` varchar(64) NOT NULL,
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `last_commit_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `start_end_date` (`start_date`,`end_date`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `classificator_log`
--

LOCK TABLES `recominer`.`classificator_log` WRITE;
/*!40000 ALTER TABLE `classificator_log` DISABLE KEYS */;
INSERT INTO `recominer`.`classificator_log` VALUES (1,1,'RandomForest','2016-09-05 22:47:06','2016-09-05 22:47:12',NULL);
/*!40000 ALTER TABLE `classificator_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `configuration`
--

DROP TABLE IF EXISTS `recominer`.`configuration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recominer`.`configuration` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `key` varchar(255) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `configuration`
--

LOCK TABLES `recominer`.`configuration` WRITE;
/*!40000 ALTER TABLE `configuration` DISABLE KEYS */;
INSERT INTO `recominer`.`configuration` VALUES (1,'localGitRepositoryPath','/home/kuroda/Git'),(2,'max_files_per_commit','20');
/*!40000 ALTER TABLE `configuration` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dataset_log`
--

DROP TABLE IF EXISTS `recominer`.`dataset_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recominer`.`dataset_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL,
  `type` varchar(64) NOT NULL,
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `last_commit_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `start_end_date` (`start_date`,`end_date`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dataset_log`
--

LOCK TABLES `recominer`.`dataset_log` WRITE;
/*!40000 ALTER TABLE `dataset_log` DISABLE KEYS */;
INSERT INTO `recominer`.`dataset_log` VALUES (1,1,'All','2016-08-24 17:05:34','2016-08-24 17:06:49',NULL);
/*!40000 ALTER TABLE `dataset_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `extractor`
--

DROP TABLE IF EXISTS `recominer`.`extractor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recominer`.`extractor` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bicho_process_end_date` datetime DEFAULT NULL,
  `bicho_process_return_code` int(11) DEFAULT NULL,
  `bicho_process_start_date` datetime DEFAULT NULL,
  `cvsanaly_process_end_date` datetime DEFAULT NULL,
  `cvsanaly_process_return_code` int(11) DEFAULT NULL,
  `cvsanaly_process_start_date` datetime DEFAULT NULL,
  `git_process_end_date` datetime DEFAULT NULL,
  `git_process_return_code` int(11) DEFAULT NULL,
  `git_process_start_date` datetime DEFAULT NULL,
  `project` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_bs3cv88dcffo586ltw90qs0k6` (`project`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `extractor`
--

LOCK TABLES `recominer`.`extractor` WRITE;
/*!40000 ALTER TABLE `extractor` DISABLE KEYS */;
/*!40000 ALTER TABLE `extractor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `extractor_log`
--

DROP TABLE IF EXISTS `recominer`.`extractor_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recominer`.`extractor_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) DEFAULT NULL,
  `git_process_start_date` datetime DEFAULT NULL,
  `git_process_end_date` datetime DEFAULT NULL,
  `git_process_return_code` int(11) DEFAULT NULL,
  `cvsanaly_process_start_date` datetime DEFAULT NULL,
  `cvsanaly_process_end_date` datetime DEFAULT NULL,
  `cvsanaly_process_return_code` int(11) DEFAULT NULL,
  `bicho_process_start_date` datetime DEFAULT NULL,
  `bicho_process_end_date` datetime DEFAULT NULL,
  `bicho_process_return_code` int(11) DEFAULT NULL,
  `association_process_start_date` datetime DEFAULT NULL,
  `association_process_end_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=134 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `extractor_log`
--

LOCK TABLES `recominer`.`extractor_log` WRITE;
/*!40000 ALTER TABLE `extractor_log` DISABLE KEYS */;
INSERT INTO `recominer`.`extractor_log` VALUES (1,1,'2016-07-24 23:43:57','2016-07-24 23:43:57',128,'2016-07-24 23:43:57','2016-07-24 23:44:07',0,'2016-07-24 23:44:07','2016-07-24 23:44:19',0,NULL,NULL);
/*!40000 ALTER TABLE `extractor_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `issue_tracker`
--

DROP TABLE IF EXISTS `recominer`.`issue_tracker`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recominer`.`issue_tracker` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `extraction_delay` int(11) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `system` varchar(255) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `issue_tracker`
--

LOCK TABLES `recominer`.`issue_tracker` WRITE;
/*!40000 ALTER TABLE `issue_tracker` DISABLE KEYS */;
INSERT INTO `recominer`.`issue_tracker` VALUES (1,15,'','JIRA','','');
/*!40000 ALTER TABLE `issue_tracker` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `recominer`.`project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recominer`.`project` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `issue_tracker_url` varchar(255) DEFAULT NULL,
  `last_its_update` datetime DEFAULT NULL,
  `last_vcs_update` datetime DEFAULT NULL,
  `project_name` varchar(255) DEFAULT NULL,
  `schema_prefix` varchar(255) DEFAULT NULL,
  `repository_path` varchar(255) DEFAULT NULL,
  `version_control_url` varchar(255) DEFAULT NULL,
  `issue_tracker` int(2) DEFAULT NULL,
  `version_control` int(2) DEFAULT NULL,
  `last_commit_date_analyzed` datetime DEFAULT NULL,
  `last_issue_update_analyzed` datetime DEFAULT NULL,
  `last_issue_update_analyzed_for_cochange` datetime DEFAULT NULL,
  `last_apriori_update` datetime DEFAULT NULL,
  `last_issue_update_analyzed_for_version` datetime DEFAULT NULL,
  `last_metrics_calculation` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `issue_tracker` (`issue_tracker`),
  KEY `version_control` (`version_control`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project`
--

LOCK TABLES `recominer`.`project` WRITE;
/*!40000 ALTER TABLE `project` DISABLE KEYS */;
INSERT INTO `recominer`.`project` VALUES (1,'https://issues.apache.org/jira/browse/AVRO','2016-07-13 00:37:14','2016-05-01 22:51:14','avro','avro_test','/home/kuroda/Git/avro','https://github.com/apache/avro.git',1,NULL,'2016-03-15 08:47:31','2016-03-28 18:25:04','2016-03-17 16:56:38','2016-03-31 20:21:15',NULL,NULL);
/*!40000 ALTER TABLE `project` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `version_control`
--

DROP TABLE IF EXISTS `recominer`.`version_control`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recominer`.`version_control` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `version_control`
--

LOCK TABLES `recominer`.`version_control` WRITE;
/*!40000 ALTER TABLE `version_control` DISABLE KEYS */;
/*!40000 ALTER TABLE `version_control` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-09-07 14:25:18
