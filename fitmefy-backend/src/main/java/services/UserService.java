package services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import entities.User;
import repository.UserRepository;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UUID setUserInfoInDB(String name, String email) {
        String role = "dedicated_user";
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setRole(role);
        user = userRepository.save(user);
        return user.getId();
    }
}
