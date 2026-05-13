CREATE INDEX idx_issues_project_status ON issues(project_id, status_id);
CREATE INDEX idx_issues_project_sprint ON issues(project_id, sprint_id);
CREATE INDEX idx_issues_parent ON issues(parent_id);
CREATE INDEX idx_issues_search_vector ON issues USING GIN(search_vector);
CREATE INDEX idx_activity_log_project_created ON activity_log(project_id, created_at DESC);
CREATE INDEX idx_notifications_user_read_created ON notifications(user_id, read, created_at DESC);
CREATE INDEX idx_comments_issue_created ON comments(issue_id, created_at);
