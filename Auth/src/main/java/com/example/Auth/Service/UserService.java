package com.example.Auth.Service;

import com.example.Auth.Domain.UserModel;
import com.example.Auth.Producer.AuthProducer;
import com.example.Auth.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Data
@AllArgsConstructor
public class UserService implements BaseService<UserModel, UUID> {

    private final UserRepository userRepository;

    private final AuthProducer authProducer;

    @Override
    @Transactional //garante que a operação tenha que ser esecutada por completo
    //garante rollback
    public UserModel save(UserModel request){
        System.out.println("ENTREI NO SERVICE");

        UserModel user = userRepository.save(request);

        authProducer.sendCreateEvent(user);

        return user;
    }

    @Override
    public List<UserModel> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<UserModel> findById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public Optional<UserModel> update(UUID id, UserModel request) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(request.getName());
                    user.setEmail(request.getEmail());
                    return userRepository.save(user);
                });
    }

    @Override
    @Transactional
    public boolean delete(UUID id) {
        if (!userRepository.existsById(id)) {
            return false;
        }

        userRepository.deleteById(id);
        return true;
    }
}
