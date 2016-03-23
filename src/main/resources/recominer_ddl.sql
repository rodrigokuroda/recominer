CREATE SCHEMA recominer CHARACTER SET utf8 COLLATE utf8_general_ci;

CREATE TABLE recominer.batch_configuration
(
  id INT(11) NOT NULL AUTO_INCREMENT,
  configuration_key VARCHAR(255),
  configuration_value VARCHAR(255),
  PRIMARY KEY (id)
);

CREATE TABLE recominer.issue_tracker
(
  id INT(11) NOT NULL AUTO_INCREMENT,
  mining_delay INT,
  password VARCHAR(255),
  system VARCHAR(255),
  token VARCHAR(255),
  username VARCHAR(255),
  PRIMARY KEY (id)
);

CREATE TABLE recominer.project
(
  id INT(11) NOT NULL AUTO_INCREMENT,
  issue_tracker_url VARCHAR(255),
  last_its_update DATETIME,
  last_vcs_update DATETIME,
  project_name VARCHAR(255),
  repository_path VARCHAR(255),
  version_control_url VARCHAR(255),
  issue_tracker INT(2),
  version_control INT(2),
  last_commit_date_analyzed DATETIME,
  last_issue_update_analyzed DATETIME,
  last_issue_update_analyzed_for_cochange DATETIME,
  last_apriori_update DATETIME,
  PRIMARY KEY (id),
  KEY (issue_tracker),
  KEY (version_control)
);


CREATE TABLE recominer.version_control
(
  id INT(11) NOT NULL AUTO_INCREMENT,
  password VARCHAR(255),
  username VARCHAR(255),
  PRIMARY KEY (id)
);

INSERT INTO recominer.batch_configuration (configuration_key, configuration_value) VALUES ('localGitRepositoryPath', '/home/kuroda/Git');

INSERT INTO recominer.issue_tracker (mining_delay, password, system, token, username) VALUES (5, NULL, 'JIRA', NULL, NULL);

INSERT INTO recominer.project (issue_tracker_url, last_its_update, last_vcs_update, project_name, repository_path, version_control_url, issue_tracker, version_control, last_commit_date_analyzed, last_issue_update_analyzed, last_issue_update_analyzed_for_cochange, last_apriori_update) VALUES ('https://issues.apache.org/jira/browse/AVRO', '2016-03-21 21:00:27.471', '2016-03-21 21:00:15.606', 'avro', '/home/kuroda/Git/avro', 'https://github.com/apache/avro.git', 1, NULL, '2016-03-15 08:47:31', '2016-03-17 21:42:02', '2016-03-17 16:56:38', NULL);
