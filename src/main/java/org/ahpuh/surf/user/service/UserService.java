package org.ahpuh.surf.user.service;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.common.exception.ApplicationException;
import org.ahpuh.surf.common.exception.s3.UploadFailException;
import org.ahpuh.surf.common.exception.user.DuplicatedEmailException;
import org.ahpuh.surf.common.exception.user.UserNotFoundException;
import org.ahpuh.surf.jwt.JwtAuthentication;
import org.ahpuh.surf.jwt.JwtAuthenticationToken;
import org.ahpuh.surf.s3.service.S3Service;
import org.ahpuh.surf.user.domain.User;
import org.ahpuh.surf.user.domain.UserConverter;
import org.ahpuh.surf.user.domain.UserRepository;
import org.ahpuh.surf.user.dto.request.UserJoinRequestDto;
import org.ahpuh.surf.user.dto.request.UserUpdateRequestDto;
import org.ahpuh.surf.user.dto.response.UserFindInfoResponseDto;
import org.ahpuh.surf.user.dto.response.UserLoginResponseDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;


@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
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
                .orElseThrow(UserNotFoundException::new);
        user.checkPassword(passwordEncoder, password);
        return user;
    }

    @Transactional
    public Long join(final UserJoinRequestDto joinRequest) {
        if (userRepository.existsByEmail(joinRequest.getEmail())) {
            throw new DuplicatedEmailException();
        }
        return userRepository.save(userConverter.toEntity(joinRequest))
                .getUserId();
    }

    public UserFindInfoResponseDto getUserInfo(final Long userId) {
        final User user = getUser(userId);
        return userConverter.toUserFindInfoResponseDto(user, user.getFollowingCount(), user.getFollowerCount());
    }

    @Transactional
    public void update(final Long userId, final UserUpdateRequestDto updateDto, final MultipartFile profilePhoto) {
        final User userEntity = getUser(userId);
        final Optional<String> profilePhotoUrl = profileImageUpload(profilePhoto);

        userEntity.update(passwordEncoder, updateDto, profilePhotoUrl);
    }

    @Transactional
    public void delete(final Long userId) {
        final User userEntity = getUser(userId);
        userRepository.delete(userEntity);
    }

    private User getUser(final Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    private Optional<String> profileImageUpload(final MultipartFile profilePhoto) {
        if (Objects.isNull(profilePhoto)) {
            return Optional.empty();
        } else if (profilePhoto.isEmpty()) {
            return Optional.empty();
        }

        try {
            return s3Service.uploadUserImage(profilePhoto);
        } catch (final ApplicationException e) {
            e.printStackTrace();
            throw e;
        } catch (final Exception e) {
            e.printStackTrace();
            throw new UploadFailException();
        }
    }
}
