package lv.bootcamp.shelter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lv.bootcamp.shelter.dto.AnimalCreateRequest;
import lv.bootcamp.shelter.dto.AnimalResponse;
import lv.bootcamp.shelter.service.AnimalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for shelter animal endpoints.
 * Returns JSON — does not render HTML pages.
 */
@RequiredArgsConstructor
@Tag(name = "Animals", description = "Manage animals in the shelter")
@RestController
@RequestMapping("/api/animals")
public class AnimalApiController {

    private final AnimalService animalService;

    @Operation(summary = "List all animals", description = "Returns a list of all animals current and adopted")
    @ApiResponse(responseCode = "200", description = "List of animals")
    @GetMapping
    public List<AnimalResponse> findAll() {
        return animalService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalResponse> findById(@PathVariable Long id) {
        return animalService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Lists adopted animals. Restricted to ROLE_ADMIN — see SecurityConfig.
     * Read-only, so it's a good endpoint for testing role-based JWT authorization:
     * calling it repeatedly (e.g. with/without a token, or with a ROLE_USER token)
     * has no side effects, unlike {@code POST /api/animals}.
     */
    @Operation(summary = "List all animals that were adopted", description = "Returns a list of all animals that were adopted")
    @ApiResponse(responseCode = "200", description = "List of animals")
    @ApiResponse(
            responseCode = "403",
            description = "Need to have ROLE_ADMIN",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Forbidden",
                            summary = "Insufficient permissions",
                            value = """
                                                        {
                                                            "timestamp": "2026-07-09T22:55:31.392+00:00",
                                                            "status": 403,
                                                            "error": "Forbidden",
                                                            "path": "/api/animals/adopted"
                                                         }
                                    """
                    )
            )
    )
    @GetMapping("/adopted")
    public List<AnimalResponse> findAdopted() {
        return animalService.findAdopted();
    }

    /**
     * Creates a new animal. Restricted to ROLE_ADMIN — see SecurityConfig.
     */
    @Operation(summary = "Create new animal", description = "Add a new animal to shelter")
    @ApiResponse(responseCode = "201", description = "Animal was created")
    @ApiResponse(responseCode = "403", description = "Need to have ROLE_ADMIN")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnimalResponse create(@RequestBody @Valid AnimalCreateRequest request) {
        return animalService.create(request);
    }

    /**
     * Adopts an animal as the currently logged-in user. Restricted to ROLE_USER
     * (not ROLE_ADMIN) — see SecurityConfig.
     */
    @Operation(summary = "Adopt animal", description = "Marks the chosen animal as adopted by current user")
    @ApiResponse(responseCode = "200", description = "Animal was adopted")
    @ApiResponse(responseCode = "403", description = "Need to have ROLE_USER")
    @ApiResponse(responseCode = "409", description = "Animal has already been adopted")
    @PostMapping("/{id}/adopt")
    public ResponseEntity<AnimalResponse> adopt(@PathVariable Long id, Authentication authentication) {
        return animalService.adopt(id, authentication.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleAlreadyAdopted(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
