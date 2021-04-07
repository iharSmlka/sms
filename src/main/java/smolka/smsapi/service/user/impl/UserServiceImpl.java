package smolka.smsapi.service.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smolka.smsapi.dto.UserDto;
import smolka.smsapi.exception.UserNotFoundException;
import smolka.smsapi.mapper.MainMapper;
import smolka.smsapi.model.User;
import smolka.smsapi.repository.UserRepository;
import smolka.smsapi.service.user.UserService;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MainMapper mainMapper;

    @Override
    @Transactional
    public void delete(String userApiKey) {
        userRepository.deleteByKey(userApiKey);
    }

    @Override
    public User findUserByUserId(Long userId) {
        return userRepository.findUserByUserId(userId);
    }

    @Override
    public User findUserByUserKey(String userApiKey) {
        return userRepository.findUserByKey(userApiKey);
    }

    @Override
    public UserDto getUserInfo(String userApiKey) throws UserNotFoundException {
        User user = userRepository.findUserByKey(userApiKey);
        if (user == null) {
            throw new UserNotFoundException("Данного юзера не существует");
        }
        return mainMapper.mapping(user, UserDto.class);
    }

    @Override
    @Transactional
    public void testSaveUser(String newApiKey) {
        if (userRepository.findUserByKey(newApiKey) != null) {
            return;
        }
        String id = UUID.randomUUID().toString();
        User user = User.builder()
                .balance(new BigDecimal(20000))
                .email(id)
                .freezeBalance(new BigDecimal(0))
                .password(id)
                .key(newApiKey)
                .username(id)
                .build();
        userRepository.save(user);
    }
}
