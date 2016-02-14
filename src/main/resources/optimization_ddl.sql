-- Inserts indexes
-- ALTER TABLE {0}_vcs.file_links ADD INDEX (file_path) ;
ALTER TABLE {0}_vcs.scmlog ADD INDEX (rev(255));
ALTER TABLE {0}_vcs.scmlog ADD INDEX (date);

-- For Jira issue tracker only
ALTER TABLE {0}_issues.issues_ext_jira ADD INDEX (issue_key(32));
-- For Bugzilla issue tracker
ALTER TABLE {0}_issues.issues ADD INDEX (issue(255));
ALTER TABLE {0}_issues.changes ADD INDEX (changed_on) ;

-- Inserts number of file in commit
ALTER TABLE {0}_vcs.scmlog ADD COLUMN num_files INT(11);

-- Sets 1 (one) to authors that are developers (committers) too
-- if the name or user are same in both issue tracker and version control.
-- Otherwise (the author are not a developer), set 0 (zero).
ALTER TABLE {0}_issues.people CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE {0}_issues.people ADD COLUMN is_dev tinyint(4) DEFAULT '0';

-- Inserts pre-processed count
ALTER TABLE {0}_issues.issues ADD COLUMN num_comments INT(11);
ALTER TABLE {0}_issues.issues ADD COLUMN num_commenters INT(11);
ALTER TABLE {0}_issues.issues ADD COLUMN num_dev_commenters INT(11);
ALTER TABLE {0}_issues.issues ADD COLUMN num_watchers INT(11);
ALTER TABLE {0}_issues.issues ADD COLUMN reopened_times INT(11);
ALTER TABLE {0}_issues.issues ADD COLUMN fixed_on DATETIME;
ALTER TABLE {0}_issues.issues ADD COLUMN updated_on DATETIME;

-- Denormalize vcs schema
CREATE SCHEMA IF NOT EXISTS {0};

CREATE TABLE IF NOT EXISTS {0}.commits (
    commit_id INT(11), -- scmlog
    rev VARCHAR(40), -- scmlog
    committer_id INT(11), -- scmlog
    date DATETIME, -- scmlog
    message LONGTEXT, -- scmlog
    repository_id INT(11), -- scmlog, files
    action_type VARCHAR(1), -- actions
    branch_id INT(11),  -- actions
    file_id INT(11), -- actions, files
    file_path VARCHAR(4096), -- file_links
    added_lines INT(11), -- commits_files_lines
    removed_lines INT(11), -- commits_files_lines
    KEY commit_id (commit_id),
    KEY committer_id (committer_id),
    KEY date (date),
    KEY repository_id (repository_id),
    KEY action_type (action_type),
    KEY branch_id (branch_id),
    KEY file_id (file_id)
);

CREATE TABLE IF NOT EXISTS {0}.file_pairs (
    id INT(11) NOT NULL AUTO_INCREMENT,
    file1_id INT(11),
    file2_id INT(11),
    file1_path VARCHAR(4096) NOT NULL,
    file2_path VARCHAR(4096) NOT NULL,
    PRIMARY KEY id (id),
    KEY file1_id (file1_id),
    KEY file2_id (file2_id)
);

CREATE TABLE IF NOT EXISTS {0}.file_pair_issue (
    file_pair_id INT(11) NOT NULL,
    issue_id INT(11) NOT NULL,
    PRIMARY KEY id (file_pair_id, issue_id)
);

CREATE TABLE IF NOT EXISTS {0}.file_pair_issue_commit (
    file_pair_id INT(11) NOT NULL,
    issue_id INT(11) NOT NULL,
    commit_id INT(11) NOT NULL,
    PRIMARY KEY id (file_pair_id, issue_id, commit_id)
);

