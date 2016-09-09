-- ALTER TABLE avro_vcs.file_links DROP INDEX (file_path) ;
ALTER TABLE avro_vcs.scmlog DROP INDEX rev;
ALTER TABLE avro_vcs.scmlog DROP INDEX date;

-- For Jira issue tracker only
ALTER TABLE avro_issues.issues_ext_jira DROP INDEX issue_key;
-- For Bugzilla issue tracker
ALTER TABLE avro_issues.issues DROP INDEX issue;
ALTER TABLE avro_issues.changes DROP INDEX changed_on;

-- Inserts number of file in commit
ALTER TABLE avro_vcs.scmlog DROP COLUMN num_files;

ALTER TABLE avro_issues.people DROP COLUMN is_dev;

-- Inserts pre-processed count
ALTER TABLE avro_issues.issues DROP COLUMN num_comments;
ALTER TABLE avro_issues.issues DROP COLUMN num_commenters;
ALTER TABLE avro_issues.issues DROP COLUMN num_dev_commenters ;
ALTER TABLE avro_issues.issues DROP COLUMN num_watchers;
ALTER TABLE avro_issues.issues DROP COLUMN reopened_times ;
ALTER TABLE avro_issues.issues DROP COLUMN fixed_on ;
ALTER TABLE avro_issues.issues DROP COLUMN updated_on ;

-- Denormalize vcs schema
DROP SCHEMA avro;

DROP TABLE avro_issues.issues_scmlog;
DROP TABLE avro_issues.issues_fix_version;
DROP TABLE avro_issues.issues_fix_version_order;

