package com.proman.customfield;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CustomFieldController {

    private final CustomFieldService customFieldService;

    @PostMapping("/api/projects/{projectId}/custom-fields")
    public ResponseEntity<CustomFieldDefinition> create(
            @PathVariable UUID projectId,
            @RequestBody CreateFieldRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(customFieldService.create(projectId, request.name(), request.fieldType(),
                request.options(), request.required()));
    }

    @GetMapping("/api/projects/{projectId}/custom-fields")
    public ResponseEntity<List<CustomFieldDefinition>> list(@PathVariable UUID projectId) {
        return ResponseEntity.ok(customFieldService.listByProject(projectId));
    }

    @PatchMapping("/api/custom-fields/{id}")
    public ResponseEntity<CustomFieldDefinition> update(
            @PathVariable UUID id,
            @RequestBody UpdateFieldRequest request) {
        return ResponseEntity.ok(customFieldService.update(id, request.name(), request.fieldType(),
            request.options(), request.required()));
    }

    @DeleteMapping("/api/custom-fields/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        customFieldService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- Custom Field Values per Issue ---

    @PutMapping("/api/issues/{issueId}/custom-fields")
    public ResponseEntity<CustomFieldValueResponse> setFieldValue(
            @PathVariable UUID issueId,
            @RequestBody SetFieldValueRequest request) {
        return ResponseEntity.ok(customFieldService.setValue(issueId, request.fieldDefinitionId(), request.value()));
    }

    @GetMapping("/api/issues/{issueId}/custom-fields")
    public ResponseEntity<List<CustomFieldValueResponse>> getFieldValues(@PathVariable UUID issueId) {
        return ResponseEntity.ok(customFieldService.getValues(issueId));
    }

    record CreateFieldRequest(String name, FieldType fieldType, String options, boolean required) {}
    record UpdateFieldRequest(String name, FieldType fieldType, String options, Boolean required) {}
}
