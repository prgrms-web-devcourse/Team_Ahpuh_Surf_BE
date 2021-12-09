package org.ahpuh.surf.user.service;

import org.ahpuh.surf.user.dto.*;
import org.ahpuh.surf.user.entity.User;

public interface UserService {

    UserLoginResponseDto authenticate(final String email, final String password);

    User login(final String email, final String password);

    UserJoinResponseDto join(final UserJoinRequestDto joinRequest);

    UserDto findById(Long userId);

    Long update(Long userId, UserUpdateRequestDto updateDto);

    void delete(Long userId);

}
