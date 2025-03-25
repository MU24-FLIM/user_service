package com.example.UserMovie;


import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUser());

    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id){

            return ResponseEntity.ok(userService.getUser(id).orElse(null));

    }

    @PostMapping
    public void createNewUser(@RequestBody User user){
        userService.createUser(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) throws Exception{

        User username = userService.updateUser(user);

        if(user != null){
            return ResponseEntity.accepted().body(user);
        } else{
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
    }
}
