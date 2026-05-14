package com.proman.customfield;

import com.proman.common.exception.BusinessRuleException;
import com.proman.common.exception.ResourceNotFoundException;
import com.proman.issue.Issue;
import com.proman.issue.IssueService;
import com.proman.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomFieldService {

    private final CustomFieldRepository fieldRepository;
    private final CustomFieldValueRepository valueRepository;
    private final ProjectService projectService;
    private final @Lazy IssueService issueService;

    @Transactional
    public CustomFieldDefinition create(UUID projectId, String name, FieldType fieldType,
                                         String options, boolean required) {
        var project = projectService.findProject(projectId);
        var field = CustomFieldDefinition.builder()
            .project(project)
            .name(name)
            .fieldType(fieldType)
            .options(options)
            .required(required)
            .build();
        return fieldRepository.save(field);
    }

    @Transactional(readOnly = true)
    public List<CustomFieldDefinition> listByProject(UUID projectId) {
        return fieldRepository.findByProjectId(projectId);
    }

    @Transactional
    public CustomFieldDefinition update(UUID fieldId, String name, FieldType fieldType,
                                         String options, Boolean required) {
        var field = fieldRepository.findById(fieldId)
            .orElseThrow(() -> new ResourceNotFoundException("CustomFieldDefinition", fieldId));
        if (name != null) field.setName(name);
        if (fieldType != null) field.setFieldType(fieldType);
        if (options != null) field.setOptions(options);
        if (required != null) field.setRequired(required);
        return fieldRepository.save(field);
    }

    @Transactional
    public void delete(UUID fieldId) {
        if (!fieldRepository.existsById(fieldId)) {
            throw new ResourceNotFoundException("CustomFieldDefinition", fieldId);
        }
        fieldRepository.deleteById(fieldId);
    }

    // --- Custom Field Values ---

    @Transactional
    public CustomFieldValueResponse setValue(UUID issueId, UUID fieldDefinitionId, String value) {
        Issue issue = issueService.findIssue(issueId);
        CustomFieldDefinition field = fieldRepository.findById(fieldDefinitionId)
            .orElseThrow(() -> new ResourceNotFoundException("CustomFieldDefinition", fieldDefinitionId));

        if (!field.getProject().getId().equals(issue.getProject().getId())) {
            throw new BusinessRuleException("Custom field does not belong to this issue's project");
        }

        // Validate SELECT field value against options
        if (field.getFieldType() == FieldType.DROPDOWN && field.getOptions() != null) {
            List<String> validOptions = List.of(field.getOptions().split(","));
            if (!validOptions.contains(value)) {
                throw new BusinessRuleException("Invalid value for SELECT field. Valid options: " + field.getOptions());
            }
        }

        // Find existing or create new
        CustomFieldValue cfv = valueRepository.findByIssueIdAndFieldDefinitionId(issueId, fieldDefinitionId)
            .orElseGet(() -> CustomFieldValue.builder()
                .issue(issue)
                .fieldDefinition(field)
                .build());

        cfv.setValue(value);
        cfv = valueRepository.save(cfv);
        return toValueResponse(cfv);
    }

    @Transactional(readOnly = true)
    public List<CustomFieldValueResponse> getValues(UUID issueId) {
        return valueRepository.findByIssueId(issueId).stream()
            .map(this::toValueResponse)
            .toList();
    }

    private CustomFieldValueResponse toValueResponse(CustomFieldValue v) {
        return new CustomFieldValueResponse(
            v.getId(),
            v.getIssue().getId(),
            v.getFieldDefinition().getId(),
            v.getFieldDefinition().getName(),
            v.getFieldDefinition().getFieldType(),
            v.getValue(),
            v.getCreatedAt(),
            v.getUpdatedAt()
        );
    }
}
