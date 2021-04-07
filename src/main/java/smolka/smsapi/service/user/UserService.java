package smolka.smsapi.service.user;

import smolka.smsapi.dto.UserDto;
import smolka.smsapi.exception.UserNotFoundException;
import smolka.smsapi.model.User;

public interface UserService {
    void delete(String userApiKey);

    User findUserByUserId(Long userId);

    User findUserByUserKey(String userApiKey);

    UserDto getUserInfo(String userApiKey) throws UserNotFoundException;

    void testSaveUser(String newApiKey);
}
