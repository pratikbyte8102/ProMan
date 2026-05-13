package com.proman.sprint;

import com.proman.sprint.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SprintController {

    private final SprintService sprintService;

    @PostMapping("/api/projects/{projectId}/sprints")
    public ResponseEntity<SprintResponse> create(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateSprintRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sprintService.create(projectId, request));
    }

    @GetMapping("/api/projects/{projectId}/sprints")
    public ResponseEntity<List<SprintResponse>> list(@PathVariable UUID projectId) {
        return ResponseEntity.ok(sprintService.listByProject(projectId));
    }

    @GetMapping("/api/sprints/{id}")
    public ResponseEntity<SprintResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(sprintService.getById(id));
    }

    @PatchMapping("/api/sprints/{id}")
    public ResponseEntity<SprintResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSprintRequest request) {
        return ResponseEntity.ok(sprintService.update(id, request));
    }

    @DeleteMapping("/api/sprints/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        sprintService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
