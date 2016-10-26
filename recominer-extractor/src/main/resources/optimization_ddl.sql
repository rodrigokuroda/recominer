-- Inserts indexes
-- ALTER TABLE {0}_vcs.file_links ADD INDEX (file_path) ;
-- ALTER TABLE {0}_vcs.scmlog ADD INDEX (rev(255));
-- ALTER TABLE {0}_vcs.scmlog ADD INDEX (date);

-- For Jira issue tracker only
ALTER TABLE {0}_issues.issues_ext_jira ADD INDEX (issue_key(32));
-- For Bugzilla issue tracker
-- ALTER TABLE {0}_issues.issues ADD INDEX (issue(255));
-- ALTER TABLE {0}_issues.changes ADD INDEX (changed_on) ;

-- Inserts number of file in commit and issue
ALTER TABLE {0}_vcs.scmlog ADD COLUMN num_files INT(11);
ALTER TABLE {0}_issues.issues ADD COLUMN num_files INT(11);

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
ALTER TABLE {0}_issues.issues ADD COLUMN comments_updated_on DATETIME;

-- Workaround for Bicho
ALTER TABLE {0}_issues.issues_ext_jira CHANGE COLUMN environment environment MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL;
ALTER TABLE {0}_issues.comments CHANGE COLUMN text text MEDIUMTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ;

-- Insert id for file_links
ALTER TABLE {0}_vcs.commits_files_lines ADD COLUMN file_link_id INT(11);

-- Denormalize vcs schema
CREATE SCHEMA IF NOT EXISTS {0} CHARACTER SET utf8 COLLATE utf8_general_ci;

CREATE TABLE IF NOT EXISTS {0}.commits (
    commit_id INT(11), -- scmlog
    rev VARCHAR(40), -- scmlog
    committer_id INT(11), -- scmlog
    date DATETIME, -- scmlog
    message LONGTEXT, -- scmlog
    repository_id INT(11), -- scmlog, files
    branch_id INT(11), -- actions
    UNIQUE KEY commit_id (commit_id),
    KEY committer_id (committer_id),
    KEY date (date),
    KEY repository_id (repository_id),
    KEY branch_id (branch_id)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

CREATE TABLE IF NOT EXISTS {0}.files (
    id INT(11) NOT NULL AUTO_INCREMENT,
    fl_id INT(11) NOT NULL,
    file_path VARCHAR(4096) NOT NULL,
    f_id INT(11) NOT NULL,
    file_name VARCHAR(4096) NOT NULL,
    PRIMARY KEY id (id),
    KEY f_id (f_id),
    KEY fl_id (fl_id)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

CREATE TABLE IF NOT EXISTS {0}.files_commits (
    file_id INT(11) NOT NULL,
    commit_id INT(11) NOT NULL,
    change_type VARCHAR(1) NOT NULL,
    branch_id INT(11) NOT NULL,
    lines_added INT(11) NOT NULL,
    lines_removed INT(11) NOT NULL,
    PRIMARY KEY file_commit_id (file_id, commit_id)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

CREATE TABLE IF NOT EXISTS {0}.file_pair_issue_commit (
    id INT(11) NOT NULL AUTO_INCREMENT,
    file1_id INT(11) NOT NULL,
    file2_id INT(11) NOT NULL,
    issue_id INT(11) NOT NULL,
    commit_id INT(11) NOT NULL,
    PRIMARY KEY id (id),
    UNIQUE KEY unique_key (file1_id, file2_id, issue_id, commit_id),
    KEY file1_id (file1_id),
    KEY file2_id (file2_id),
    KEY issue_id (issue_id),
    KEY commit_id (commit_id)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

CREATE TABLE IF NOT EXISTS {0}.file_apriori (
    file_id INTEGER NOT NULL,
    file_issues INTEGER NOT NULL,
    updated_on DATETIME,
    PRIMARY KEY id (file_id)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

CREATE TABLE IF NOT EXISTS {0}.file_pair_apriori (
    file_pair_id INTEGER NOT NULL,
    file_pair_issues INTEGER NOT NULL,
    file1_support DOUBLE NOT NULL,
    file2_support DOUBLE NOT NULL,
    file1_issues INTEGER NOT NULL,
    file2_issues INTEGER NOT NULL,
    file_pair_support DOUBLE NOT NULL,
    file1_confidence DOUBLE NOT NULL,
    file2_confidence DOUBLE NOT NULL,
    updated_on DATETIME,
    PRIMARY KEY id (file_pair_id)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

CREATE TABLE IF NOT EXISTS {0}.issue_commit_historical (
    issue_id INT(11), -- issue
    commit_id INT(11), -- scmlog
    date DATETIME, -- scmlog
    rev VARCHAR(40), -- scmlog
    num_comments INT(11), 
    num_commenters INT(11), 
    num_dev_commenters INT(11), 
    reopened_times INT(11), 
    KEY commit_id (commit_id),
    KEY date (date)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;