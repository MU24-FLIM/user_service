package com.example.UserMovie;


import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public ResponseEntity<Mono<User>> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(genre -> ResponseEntity.ok(Mono.just(genre)))
                .orElseGet(() -> ResponseEntity.notFound().build());

    }


    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User newUser) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        User user = optionalUser.get();
        user.setUsername(newUser.getUsername());
        user.setEmail(newUser.getEmail());
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {

        userRepository.deleteById(id);
    }


    // get reviews
    @GetMapping("/{id}/reviews")
    public Mono<ResponseEntity<UserResponse>> getReviewsByTheUserId(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    return reviewClient.get()
                            .uri(("/reviews/user/" + id))
                            .retrieve()
                            .bodyToFlux(Review.class)
                            .collectList()
                            .map(reviews -> {
                                UserResponse response = new UserResponse();
                                response.setUser(user);
                                response.setReviews(reviews);
                                return ResponseEntity.ok(response);
                            });
                }).orElseGet(() -> Mono.just(ResponseEntity.notFound().build()));
    }
}
