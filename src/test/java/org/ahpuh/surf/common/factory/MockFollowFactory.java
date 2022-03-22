package org.ahpuh.surf.common.factory;

import org.ahpuh.surf.follow.domain.Follow;
import org.ahpuh.surf.follow.dto.request.FollowRequestDto;
import org.ahpuh.surf.follow.dto.response.FollowResponseDto;
import org.ahpuh.surf.follow.dto.response.FollowUserResponseDto;
import org.ahpuh.surf.user.domain.User;

import java.util.List;

public class MockFollowFactory {

    public static Follow createMockFollow(final User source, final User target) {
        return Follow.builder()
                .source(source)
                .target(target)
                .build();
    }

    public static FollowRequestDto createMockFollowRequestDto() {
        return new FollowRequestDto(1L);
    }

    public static FollowRequestDto createMockFollowRequestDtoWithTargetId(final Long targetId) {
        return new FollowRequestDto(targetId);
    }

    public static FollowResponseDto createMockFollowResponseDto() {
        return new FollowResponseDto(1L);
    }

    public static List<FollowUserResponseDto> createMockFollowUserResponseDtos() {
        final FollowUserResponseDto followUserResponseDto = FollowUserResponseDto.builder()
                .userId(1L)
                .userName("userName")
                .profilePhotoUrl("url")
                .build();
        return List.of(followUserResponseDto, followUserResponseDto);
    }
}
