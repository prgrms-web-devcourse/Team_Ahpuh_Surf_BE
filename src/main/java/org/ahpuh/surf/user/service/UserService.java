package org.ahpuh.surf.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ahpuh.surf.jwt.JwtAuthentication;
import org.ahpuh.surf.jwt.JwtAuthenticationToken;
import org.ahpuh.surf.s3.S3Service;
import org.ahpuh.surf.user.domain.User;
import org.ahpuh.surf.user.domain.UserConverter;
import org.ahpuh.surf.user.domain.UserRepository;
import org.ahpuh.surf.user.domain.follow.FollowRepository;
import org.ahpuh.surf.user.dto.request.UserJoinRequestDto;
import org.ahpuh.surf.user.dto.request.UserUpdateRequestDto;
import org.ahpuh.surf.user.dto.response.UserFindInfoResponseDto;
import org.ahpuh.surf.user.dto.response.UserJoinResponseDto;
import org.ahpuh.surf.user.dto.response.UserLoginResponseDto;
import org.ahpuh.surf.user.dto.response.UserUpdateResponseDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.ahpuh.surf.common.exception.EntityExceptionHandler.UserNotFound;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final S3Service s3Service;
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
                .orElseThrow(() -> UserNotFound(email));
        user.checkPassword(passwordEncoder, password);
        return user;
    }

    @Transactional
    public UserJoinResponseDto join(final UserJoinRequestDto joinRequest) {
        if (userRepository.existsByEmail(joinRequest.getEmail())) {
            throw new IllegalArgumentException(String.format("Email is duplicated. email=%s", joinRequest.getEmail()));
        }
        final Long userId = userRepository.save(userConverter.toEntity(joinRequest))
                .getUserId();
        return new UserJoinResponseDto(userId);
    }

    public UserFindInfoResponseDto findUser(final Long userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFound(userId));
        final long followingCount = followRepository.countByUser(user);
        final long followerCount = followRepository.countByFollowedUser(user);
        return userConverter.toUserFindInfoResponseDto(user, followingCount, followerCount);
    }

    @Transactional
    public UserUpdateResponseDto update(final Long userId, final UserUpdateRequestDto updateDto, final MultipartFile profilePhoto) {
        String profilePhotoUrl = null;
        if (profilePhoto != null) {
            try {
                profilePhotoUrl = s3Service.uploadUserImage(profilePhoto);
            } catch (final IOException e) {
                log.info("파일이 존재하지 않습니다.");
                e.printStackTrace();
            }
        }
        final User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFound(userId));
        userEntity.update(passwordEncoder, updateDto, profilePhotoUrl);
        return new UserUpdateResponseDto(userId);
    }

    @Transactional
    public void delete(final Long userId) {
        final User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFound(userId));
        userRepository.delete(userEntity);
    }
}
