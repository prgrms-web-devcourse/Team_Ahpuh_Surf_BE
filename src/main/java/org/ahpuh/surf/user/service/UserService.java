package org.ahpuh.surf.user.service;

import org.ahpuh.surf.user.dto.UserJoinRequestDto;
import org.ahpuh.surf.user.dto.UserJoinResponseDto;
import org.ahpuh.surf.user.dto.UserLoginResponseDto;
import org.ahpuh.surf.user.entity.User;

public interface UserService {

    UserLoginResponseDto authenticate(final String email, final String password);

    User login(final String email, final String password);

    UserJoinResponseDto join(final UserJoinRequestDto joinRequest);

}
