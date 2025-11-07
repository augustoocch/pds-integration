package ar.com.uade.pds.final_project.domain.dto.request;

public record RoleAssignmentRequest (
        Long scrimId,
        Long userId,
        String newRole
) {
}
