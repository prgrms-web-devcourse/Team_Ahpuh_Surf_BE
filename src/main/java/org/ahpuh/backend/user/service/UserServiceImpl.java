package org.ahpuh.backend.user.service;

import lombok.RequiredArgsConstructor;
import org.ahpuh.backend.user.converter.UserConverter;
import org.ahpuh.backend.user.dto.UserJoinRequestDto;
import org.ahpuh.backend.user.entity.User;
import org.ahpuh.backend.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final UserConverter userConverter;

    public User login(final String email, final String password) {
        checkArgument(isNotEmpty(email), "email must be provided.");
        checkArgument(isNotEmpty(password), "password must be provided.");

        final User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Could not found user for " + email));
        user.checkPassword(passwordEncoder, password);
        return user;
    }

    public Long join(final UserJoinRequestDto dto) {
        checkArgument(isNotEmpty(dto.getEmail()), "email must be provided.");
        checkArgument(isNotEmpty(dto.getUserName()), "userName must be provided.");
        checkArgument(isNotEmpty(dto.getPassword()), "password must be provided.");

        return userRepository.save(userConverter.toEntity(dto)).getUserId();
    }

}
