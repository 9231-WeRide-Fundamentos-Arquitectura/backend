package org.example.backendweride.platform.profile.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.backendweride.platform.profile.domain.model.queries.GetProfileByUserIdQuery;
import org.example.backendweride.platform.profile.domain.services.ProfileCommandService;
import org.example.backendweride.platform.profile.domain.services.ProfileQueryService;
import org.example.backendweride.platform.profile.interfaces.rest.resources.ProfileResource;
import org.example.backendweride.platform.profile.interfaces.rest.resources.UpdateUserResource;
import org.example.backendweride.platform.profile.interfaces.rest.transform.ProfileResourceFromAggregateAssembler;
import org.example.backendweride.platform.profile.interfaces.rest.transform.UpdateProfileCommandFromResourceAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Profiles Controller
 *
 * @summary Handles HTTP requests related to user profiles.
 */
@RestController
@RequestMapping(value = "/api/v1/profiles", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Profiles", description = "Obtain and Update User Profiles")
public class ProfilesController {

    private final ProfileQueryService profileQueryService;
    private final ProfileCommandService profileCommandService;

    public ProfilesController(ProfileQueryService profileQueryService, ProfileCommandService profileCommandService) {
        this.profileQueryService = profileQueryService;
        this.profileCommandService = profileCommandService;
    }

    /**
     * Get profile by Account ID.
     *
     * @param userId The ID of the account associated with the profile.
     * @return ResponseEntity containing the user resource or a not found status.
     */
    @GetMapping("/{userId}")
    @Operation(summary = "Get profile by User ID", description = "Retrieve user profile details using the associated account ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ProfileResource> getProfileByAccountId(@PathVariable Long userId) {
        var query = new GetProfileByUserIdQuery(userId);
        var profile = profileQueryService.handle(query);
        if (profile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var resource = ProfileResourceFromAggregateAssembler.toResource(profile.get());
        return ResponseEntity.ok(resource);
    }

    /**
     * Update profile by User ID.
     *
     * @param userId   The ID of the user whose user to update.
     * @param resource The updated user details.
     * @return ResponseEntity containing the updated user resource or a not found status.
     */
    @PutMapping("/{userId}")
    @Operation(summary = "Update profile by User ID", description = "Update user profile details using the user ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ProfileResource> updateProfile(@PathVariable Long userId, @RequestBody UpdateUserResource resource) {
        var command = UpdateProfileCommandFromResourceAssembler.toCommand(userId, resource);
        var profile = profileCommandService.handle(command);
        if (profile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var res = ProfileResourceFromAggregateAssembler.toResource(profile.get());
        return ResponseEntity.ok(res);
    }
}