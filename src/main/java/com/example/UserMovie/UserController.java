package com.example.UserMovie;


import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import javax.naming.ServiceUnavailableException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final WebClient reviewClient;
    private final UserRepository userRepository;


    public UserController(WebClient.Builder reviewClientBuilder, UserRepository userRepository) {
        this.reviewClient = reviewClientBuilder.baseUrl(System.getenv("REVIEW_CLIENT_URL")).build();
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());

    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) throws BadRequestException {
        return ResponseEntity.ok(userRepository.save(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }

        User existingUser = user.get();
        Flux<Review> userReviews = getReviews(existingUser.getId());

        return ResponseEntity.ok(new UserResponse(existingUser, userReviews.collectList().block()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User updatedUser) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        User user = optionalUser.get();
        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }


    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {

        userRepository.deleteById(id);
    }

    @GetMapping("/exists/{id}")
    public boolean userExists(@PathVariable Long id) {
        return userRepository.existsById(id);
    }

    private Flux<Review> getReviews(Long userId) {
        return reviewClient.get()
                .uri("reviews/" + userId + "/user")
                .retrieve()
                .bodyToFlux(Review.class)
                .retryWhen(Retry.backoff(2, Duration.of(1, ChronoUnit.SECONDS))
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                new ServiceUnavailableException("UNABLE TO CONNECT TO REVIEW SERVICE")
                        )
                );
    }
}
