CREATE TABLE IF NOT EXISTS {0}_issues.issues_scmlog (
    id int(11) NOT NULL AUTO_INCREMENT,
    issue_id int(11) NOT NULL,
    scmlog_id int(11) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY unq_issue_scmlog (issue_id,scmlog_id),
    KEY issue_id (issue_id),
    KEY scmlog_id (scmlog_id)
);

CREATE TABLE IF NOT EXISTS {0}_issues.issues_fix_version (
    issue_id int(11) NOT NULL,
    fix_version varchar(255) NOT NULL,
    minor_fix_version varchar(255) NOT NULL,
    major_fix_version varchar(255) NOT NULL,
    UNIQUE KEY unq_issue_fix_version (issue_id,fix_version),
    KEY issue_id (issue_id),
    KEY fix_version (fix_version),
    KEY minor_fix_version (minor_fix_version),
    KEY major_fix_version (major_fix_version)
);

CREATE TABLE IF NOT EXISTS {0}_issues.issues_fix_version_order(
    minor_fix_version varchar(255) NOT NULL,
    major_fix_version varchar(255) NOT NULL,
    version_order int(11) NOT NULL,
    UNIQUE KEY unq_major_fix_version_order (major_fix_version,version_order),
    KEY minor_fix_version (minor_fix_version),
    KEY major_fix_version (major_fix_version),
    KEY version_order (version_order)
);