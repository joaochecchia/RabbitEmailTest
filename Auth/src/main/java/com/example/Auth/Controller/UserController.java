package com.example.Auth.Controller;

import com.example.Auth.DTO.UserDto;
import com.example.Auth.Domain.UserModel;
import com.example.Auth.Service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController implements BaseController<UserModel, UserDto, UUID> {

    private final UserService userService;

    @Override
    @PostMapping("/save")
    public ResponseEntity<UserModel> save(@RequestBody @Valid UserDto userDto){

        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDto, userModel);
        UserModel user = userService.save(userModel);

        return ResponseEntity.ok(user);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UserModel> findById(@PathVariable UUID id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    @GetMapping
    public ResponseEntity<List<UserModel>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<UserModel> update(@PathVariable UUID id, @RequestBody @Valid UserDto userDto) {
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDto, userModel);

        return userService.update(id, userModel)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!userService.delete(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
