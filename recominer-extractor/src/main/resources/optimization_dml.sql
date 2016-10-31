-- Inserts number of file in commit
UPDATE {0}_vcs.scmlog s SET s.num_files =
(SELECT COUNT(DISTINCT(ca.file_id))
          FROM {0}_vcs.actions ca
         WHERE ca.commit_id = s.id)
 WHERE 1 = 1
 WHERE_SCMLOG;

-- Sets 1 (one) to authors that are developers (committers) too
-- if the name or user are same in both issue tracker and version control.
-- Otherwise (the author are not a developer), set 0 (zero).
UPDATE {0}_issues.people ip SET ip.is_dev = 1
WHERE EXISTS
(SELECT 1
   FROM {0}_vcs.people sp
  WHERE upper(sp.name) = upper(ip.name) OR upper(sp.name) = upper(ip.user_id));
UPDATE {0}_issues.people ip SET ip.is_dev = 0
WHERE NOT EXISTS
(SELECT 1
   FROM {0}_vcs.people sp
  WHERE upper(sp.name) = upper(ip.name) OR upper(sp.name) = upper(ip.user_id));

-- Inserts fixed date in issue
UPDATE {0}_issues.issues i SET i.fixed_on =
  (SELECT MAX(c.changed_on)
     FROM {0}_issues.changes c
    WHERE c.issue_id = i.id
      AND c.field = "Resolution"
      AND c.new_value = "Fixed")
 WHERE i.resolution = "Fixed"
 WHERE_ISSUE;

-- Inserts pre-processed count
UPDATE {0}_issues.issues i SET
i.num_comments =
(SELECT COUNT(DISTINCT(c.id))
   FROM {0}_issues.comments c
  WHERE c.issue_id = i.id),

i.num_commenters =
(SELECT COUNT(DISTINCT(c.submitted_by))
   FROM {0}_issues.comments c
  WHERE c.issue_id = i.id),

i.num_dev_commenters =
(SELECT COUNT(DISTINCT(c.submitted_by))
   FROM {0}_issues.comments c
   JOIN {0}_issues.people p ON c.submitted_by = p.id
  WHERE c.issue_id = i.id
    AND p.is_dev = 1),

i.num_watchers =
(SELECT COUNT(DISTINCT(iw.person_id))
   FROM {0}_issues.issues_watchers iw
  WHERE iw.issue_id = i.id),

i.reopened_times =
(SELECT COALESCE(COUNT(1), 0)
   FROM {0}_issues.changes c
  WHERE c.new_value = "Reopened"
    AND c.field = "Status"
    AND c.issue_id = i.id)
 WHERE 1 = 1 
 WHERE_ISSUE
;

UPDATE {0}_issues.issues i SET
i.updated_on = 
(SELECT MAX(ext.updated) 
   FROM {0}_issues.issues_ext_jira ext 
  WHERE ext.issue_id = i.id),

i.comments_updated_on = 
(SELECT MAX(comments.submitted_on) 
   FROM {0}_issues.comments comments 
  WHERE comments.issue_id = i.id)

 WHERE 1 = 1 
 WHERE_ISSUE;

-- Denormalize vcs schema
INSERT INTO {0}.commits (commit_id, rev, committer_id, date, message, repository_id, branch_id)
SELECT DISTINCT s.id, s.rev, s.committer_id, s.date, s.message, s.repository_id, a.branch_id
  FROM {0}_vcs.scmlog s
  JOIN {0}_vcs.actions a ON a.commit_id = s.id
 WHERE s.id IN (SELECT scmlog_id FROM {0}.issues_scmlog)
   AND s.id NOT IN (SELECT commit_id FROM {0}.commits)
 WHERE_SCMLOG
 ORDER BY date ASC;

-- relationship between file and commit
INSERT IGNORE INTO {0}.files_commits (file_id, file_link_id, file_path, file_name, commit_id, change_type, branch_id, lines_added, lines_removed)
SELECT DISTINCT fil.id, fill.id, fill.file_path, fil.file_name, a.commit_id, a.type, a.branch_id, filcl.added, filcl.removed
  FROM {0}_vcs.files fil
  JOIN {0}_vcs.actions a ON fil.id = a.file_id
  JOIN {0}_vcs.scmlog s ON s.id = a.commit_id
  JOIN {0}_vcs.file_links fill ON fill.file_id = fil.id 
   AND fill.commit_id =
    (SELECT MAX(afill.commit_id)
       FROM {0}_vcs.file_links afill
      WHERE afill.file_id = fil.file_id
        AND afill.commit_id <= a.commit_id)
  JOIN {0}_vcs.commits_files_lines filcl ON filcl.path = fill.file_path AND filcl.commit = a.commit_id
WHERE NOT EXISTS (SELECT 1 FROM {0}.files_commits fc WHERE fc.file_id = fil.id AND fc.commit_id = a.commit_id)
WHERE_SCMLOG
 ORDER BY a.commit_id;

-- count files for issues
UPDATE {0}_issues.issues i SET 
i.num_files =
(SELECT COUNT(DISTINCT(f.file_path))
  FROM {0}.issues_scmlog i2s
  JOIN {0}_vcs.scmlog s ON s.id = i2s.scmlog_id
  JOIN {0}.files_commits f ON f.commit_id = i2s.scmlog_id
 WHERE i2s.issue_id = i.id)
WHERE 1=1
WHERE_ISSUE;