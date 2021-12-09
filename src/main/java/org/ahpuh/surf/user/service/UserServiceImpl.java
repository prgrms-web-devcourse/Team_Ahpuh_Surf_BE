package org.ahpuh.surf.user.service;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.jwt.JwtAuthentication;
import org.ahpuh.surf.jwt.JwtAuthenticationToken;
import org.ahpuh.surf.user.converter.UserConverter;
import org.ahpuh.surf.user.dto.*;
import org.ahpuh.surf.user.entity.User;
import org.ahpuh.surf.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.ahpuh.surf.common.exception.EntityExceptionHandler.UserNotFound;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final UserConverter userConverter;

    public UserLoginResponseDto authenticate(final String email, final String password) {
        final JwtAuthenticationToken authToken = new JwtAuthenticationToken(email, password);
        final Authentication resultToken = authenticationManager.authenticate(authToken);
        final JwtAuthentication authentication = (JwtAuthentication) resultToken.getPrincipal();
        final User user = (User) resultToken.getDetails();
        return new UserLoginResponseDto(authentication.token, user.getUserId());
    }

    public User login(final String email, final String password) {
        final User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Could not found user for " + email));
        user.checkPassword(passwordEncoder, password);
        return user;
    }

    @Transactional
    public UserJoinResponseDto join(final UserJoinRequestDto joinRequest) {
        final User newUser = userRepository.save(userConverter.toEntity(joinRequest));
        return new UserJoinResponseDto(newUser.getEmail(), joinRequest.getPassword());
    }

    @Override
    public UserDto findById(final Long userId) {
        final UserDto userDto = userRepository.findById(userId)
                .map(userConverter::toUserDto)
                .orElseThrow(() -> UserNotFound(userId));
        return userDto;
    }

    @Override
    public Long update(final Long userId, final UserUpdateRequestDto updateDto) {
        final User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFound(userId));
        userEntity.update(updateDto);
        return userEntity.getUserId();
    }

    @Override
    public void delete(final Long userId) {
        final User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFound(userId));
        userEntity.delete();
    }

}
