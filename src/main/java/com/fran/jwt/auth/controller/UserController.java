package com.fran.jwt.auth.controller;

import com.fran.jwt.auth.dto.PasswordRequest;
import com.fran.jwt.auth.dto.UserRequest;
import com.fran.jwt.auth.dto.UserResponse;
import com.fran.jwt.auth.entity.Role;
import com.fran.jwt.auth.service.UserService;
import com.fran.jwt.auth.utils.AuthenticatedUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Operations related to user management")
public class UserController {

    private final UserService userService;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Operation(summary = "Update user", description = "Updates a user's data by their username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{username}")
    public ResponseEntity<Void> updateUser(
            @Parameter(description = "User's username") @PathVariable String username,
            @RequestBody UserRequest user) {
        userService.updateUser(username, user);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update role", description = "Updates a user's role by their ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateRole(
            @Parameter(description = "User's ID") @PathVariable Long id,
            @RequestBody Role role) {
        userService.updateRole(id, role);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete user by ID", description = "Deletes a user by their ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserById(@Parameter(description = "User's ID") @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete user by username", description = "Deletes a user by their username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/by-username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserByUsername(@Parameter(description = "User's username") @PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update password", description = "Updates a user's password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @ApiResponse(responseCode = "400", description = "Incorrect old password"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{username}/password")
    public ResponseEntity<Void> updatePassword(
            @Parameter(description = "User's username") @PathVariable String username,
            @RequestBody PasswordRequest password) {
        userService.updatePassword(username, password);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get authenticated user info", description = "Retrieves information about the currently authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User information retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getAuthenticatedUserInfo() {
        String username = authenticatedUserProvider.getAuthenticatedUser().getUsername();
        return ResponseEntity.ok(userService.getUserInfoByUsername(username));
    }
}