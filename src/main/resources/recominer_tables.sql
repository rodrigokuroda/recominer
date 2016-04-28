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
    issue_id INT(11) NOT NULL,
    issue_update_on DATETIME,
    issue_type VARCHAR(64) NOT NULL,
    issue_priority VARCHAR(64) NOT NULL,
    issue_assigned_to VARCHAR(64) NOT NULL, 
    issue_submitted_by VARCHAR(64) NOT NULL,
    issue_watchers INT(11) NOT NULL,
    issue_reopened INT(11) NOT NULL,
    commenters INT(11) NOT NULL,
    dev_commenters INT(11) NOT NULL,
    comments INT(11) NOT NULL,
    wordiness INT(11) NOT NULL,
    issue_age INT(11) NOT NULL,
    PRIMARY KEY (issue_id, issue_update_on)
);

CREATE TABLE IF NOT EXISTS {0}.issues_metrics(
    commit_id INT(11) NOT NULL,
    PRIMARY KEY (commit_id)
);

CREATE TABLE IF NOT EXISTS avro.communication_network_metric(
    issue_id INT(11) NOT NULL, 
    comment_updated_on DATETIME NOT NULL, 
    btwSum DOUBLE,btwAvg DOUBLE,btwMdn DOUBLE,btwMax DOUBLE,
    clsSum DOUBLE,clsAvg DOUBLE,clsMdn DOUBLE,clsMax DOUBLE,
    dgrSum DOUBLE,dgrAvg DOUBLE,dgrMdn DOUBLE,dgrMax DOUBLE,
    egoBtwSum DOUBLE,egoBtwAvg DOUBLE,egoBtwMdn DOUBLE,egoBtwMax DOUBLE,
    egoSizeSum DOUBLE,egoSizeAvg DOUBLE,egoSizeMdn DOUBLE,egoSizeMax DOUBLE,
    egoTiesSum DOUBLE,egoTiesAvg DOUBLE,egoTiesMdn DOUBLE,egoTiesMax DOUBLE,
    egoDensitySum DOUBLE,egoDensityAvg DOUBLE,egoDensityMdn DOUBLE,egoDensityMax DOUBLE,
    efficiencySum DOUBLE,efficiencyAvg DOUBLE,efficiencyMdn DOUBLE,efficiencyMax DOUBLE,
    efvSizeSum DOUBLE,efvSizeAvg DOUBLE,efvSizeMdn DOUBLE,efvSizeMax DOUBLE,
    constraintSum DOUBLE,constraintAvg DOUBLE,constraintMdn DOUBLE,constraintMax DOUBLE,
    hierarchySum DOUBLE,hierarchyAvg DOUBLE,hierarchyMdn DOUBLE,hierarchyMax DOUBLE,
    size DOUBLE,ties DOUBLE,density DOUBLE,diameter DOUBLE,
    PRIMARY KEY (issue_id, comment_updated_on)
);