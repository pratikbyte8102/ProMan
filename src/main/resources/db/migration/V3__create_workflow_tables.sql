CREATE TABLE workflow_statuses (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(20) NOT NULL,
    position INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (project_id, name)
);

CREATE TABLE workflow_transitions (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    from_status_id UUID NOT NULL REFERENCES workflow_statuses(id) ON DELETE CASCADE,
    to_status_id UUID NOT NULL REFERENCES workflow_statuses(id) ON DELETE CASCADE,
    auto_assign_role VARCHAR(50),
    required_fields TEXT[],
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (project_id, from_status_id, to_status_id)
);
