CREATE SCHEMA IF NOT EXISTS {0} CHARACTER SET utf8 COLLATE utf8_general_ci;

CREATE TABLE IF NOT EXISTS {0}.issues_scmlog (
    id INT(11) NOT NULL AUTO_INCREMENT,
    issue_id INT(11) NOT NULL,
    scmlog_id INT(11) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY unq_issue_scmlog (issue_id,scmlog_id),
    KEY issue_id (issue_id),
    KEY scmlog_id (scmlog_id)
);

CREATE TABLE IF NOT EXISTS {0}.issues_fix_version (
    issue_id INT(11) NOT NULL,
    fix_version VARCHAR(255) NOT NULL,
    minor_fix_version VARCHAR(255) NOT NULL,
    major_fix_version VARCHAR(255) NOT NULL,
    UNIQUE KEY unq_issue_fix_version (issue_id,fix_version),
    KEY issue_id (issue_id),
    KEY fix_version (fix_version),
    KEY minor_fix_version (minor_fix_version),
    KEY major_fix_version (major_fix_version)
);

CREATE TABLE IF NOT EXISTS {0}.issues_fix_version_order(
    minor_fix_version VARCHAR(255) NOT NULL,
    major_fix_version VARCHAR(255) NOT NULL,
    version_order INT(11) NOT NULL,
    UNIQUE KEY unq_major_fix_version_order (major_fix_version,version_order),
    KEY minor_fix_version (minor_fix_version),
    KEY major_fix_version (major_fix_version),
    KEY version_order (version_order)
);

-- Stores issues' metrics and its history
CREATE TABLE IF NOT EXISTS {0}.issues_metrics(
    id INT(11) NOT NULL AUTO_INCREMENT,
    issue_id INT(11) NOT NULL,
    commit_id INT(11),
    issue_key VARCHAR(64) NOT NULL,
    issue_type VARCHAR(64) NOT NULL,
    priority VARCHAR(64) NOT NULL,
    assigned_to VARCHAR(64) NOT NULL, 
    submitted_by VARCHAR(64) NOT NULL,
    commenters INT(11) NOT NULL,
    dev_commenters INT(11) NOT NULL,
    updated_on DATETIME,
    comments_updated_on DATETIME,
    wordiness_body INT(11) NOT NULL,
    wordiness_comments INT(11) NOT NULL,
    age INT(11) NOT NULL,
    comments INT(11) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY issue_id_commit_id (issue_id, commit_id)
);

CREATE TABLE IF NOT EXISTS {0}.commit_metrics(
    id INT(11) NOT NULL AUTO_INCREMENT,
    commit_id INT(11) NOT NULL,
    committer_id INT(11) NOT NULL,
    committer_name VARCHAR(64) NOT NULL,
    revision VARCHAR(64) NOT NULL, 
    date DATETIME,
    PRIMARY KEY (id),
    UNIQUE KEY (commit_id)
);

CREATE TABLE IF NOT EXISTS {0}.file_metrics(
    id INT(11) NOT NULL AUTO_INCREMENT,
    commit_id INT(11) NOT NULL,
    file_id INT(11) NOT NULL,
    additions INT(11) NOT NULL,
    deletions INT(11) NOT NULL,
    changes INT(11) NOT NULL,
    committers INT(11) NOT NULL,
    commits INT(11) NOT NULL,
    age INT(11) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (file_id, commit_id)
);

CREATE TABLE IF NOT EXISTS {0}.communication_network_metrics(
    id INT(11) NOT NULL AUTO_INCREMENT,
    issue_id INT(11) NOT NULL, 
    commit_id INT(11),
    betweenness_sum DOUBLE,
    betweenness_mean DOUBLE,
    betweenness_median DOUBLE,
    betweenness_maximum DOUBLE,
    closeness_sum DOUBLE,
    closeness_mean DOUBLE, 
    closeness_median DOUBLE, 
    closeness_maximum DOUBLE,
    degree_sum DOUBLE, 
    degree_mean DOUBLE,
    degree_median DOUBLE,
    degree_maximum DOUBLE,
    efficiency_sum DOUBLE,
    efficiency_mean DOUBLE,
    efficiency_median DOUBLE,
    efficiency_maximum DOUBLE,
    effective_size_sum DOUBLE,
    effective_size_mean DOUBLE,
    effective_size_median DOUBLE,
    effective_size_maximum DOUBLE,
    constraint_sum DOUBLE,
    constraint_mean DOUBLE,
    constraint_median DOUBLE,
    constraint_maximum DOUBLE,
    hierarchy_sum DOUBLE,
    hierarchy_mean DOUBLE,
    hierarchy_median DOUBLE,
    hierarchy_maximum DOUBLE,
    size DOUBLE,
    ties DOUBLE,density DOUBLE,
    diameter DOUBLE,
    PRIMARY KEY (id),
    UNIQUE KEY issue_id_commit_id (issue_id, commit_id)
);

-- dataset of metrics generated
CREATE TABLE IF NOT EXISTS {0}.contextual_metrics(
    id INT(11) NOT NULL AUTO_INCREMENT,
    commit_id INT(11) NOT NULL,
    file_id INT(11) NOT NULL,
    network_metrics_id INT(11) NOT NULL, 
    issue_metrics_id INT(11) NOT NULL, 
    commit_metrics_id INT(11) NOT NULL, 
    file_metrics_id INT(11) NOT NULL, 
    PRIMARY KEY (id)
);

-- cochanges of set of files (antecedent) predicted by association rule
CREATE TABLE IF NOT EXISTS {0}.ar_prediction(
    id INT(11) NOT NULL AUTO_INCREMENT,
    commit_id INT(11) NOT NULL,
    fileset_id INT(11) NOT NULL,
    predicted_fileset_id INT(11) NOT NULL, 
    prediction_result VARCHAR(2) NOT NULL,
    rank INT(11) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (fileset_id, commit_id, predicted_fileset_id)
);

-- Sequence for grouping a set of files (table fileset)
CREATE TABLE IF NOT EXISTS {0}.fileset_sequence (id INT NOT NULL);
INSERT INTO {0}.fileset_sequence (id)
SELECT 0 FROM dual
 WHERE NOT EXISTS (SELECT * FROM {0}.fileset_sequence);

CREATE TABLE IF NOT EXISTS {0}.fileset(
    id INT(11) NOT NULL AUTO_INCREMENT,
    file_id INT(11) NOT NULL,
    UNIQUE KEY (id, file_id)
);

-- cochanges predicted by machine learning
CREATE TABLE IF NOT EXISTS {0}.ml_prediction(
    id INT(11) NOT NULL AUTO_INCREMENT,
    commit_id INT(11) NOT NULL,
    file_id INT(11) NOT NULL,
    predicted_file_id INT(11) NOT NULL,
    prediction_result VARCHAR(2) NOT NULL,
    algorithm_name VARCHAR(64) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (commit_id, file_id, predicted_file_id)
);