package com.proman.customfield;

import com.proman.common.exception.ResourceNotFoundException;
import com.proman.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomFieldService {

    private final CustomFieldRepository fieldRepository;
    private final ProjectService projectService;

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
}
