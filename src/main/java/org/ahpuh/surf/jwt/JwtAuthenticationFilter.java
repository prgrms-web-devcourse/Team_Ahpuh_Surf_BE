package org.ahpuh.surf.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final String headerKey;

    private final Jwt jwt;

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
        /**
         * HTTP 요청 헤더에 JWT 토큰이 있는지 확인
         * JWT 토큰이 있다면, 주어진 토큰 디코딩
         * userId, email, roles 데이터 추출
         * JwtAuthenticationToken 생성해서 SecurityContext에 넣는다.
         **/
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            final String token = getToken(request);
            if (token != null) {
                try {
                    final Claims claims = verify(token);
                    log.debug("Jwt parse result: {}", claims);

                    final Long userId = claims.userId;
                    final String email = claims.email;
                    final List<GrantedAuthority> authorities = getAuthorities(claims);

                    if (userId != null && isNotEmpty(email) && authorities.size() > 0) {
                        final JwtAuthenticationToken authentication =
                                new JwtAuthenticationToken(new JwtAuthentication(token, userId, email), null, authorities);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch (final Exception e) {
                    log.warn("Jwt processing failed: {}", e.getMessage());
                }
            }
        } else {
            log.debug("SecurityContextHolder not populated with security token, as it already contained: '{}'",
                    SecurityContextHolder.getContext().getAuthentication());
        }

        chain.doFilter(request, response);
    }

    private String getToken(final HttpServletRequest request) {
        final String token = request.getHeader(headerKey);
        if (isNotEmpty(token)) {
            log.debug("Jwt authorization api detected: {}", token);
            try {
                return URLDecoder.decode(token, "UTF-8");
            } catch (final UnsupportedEncodingException e) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }

    private Claims verify(final String token) {
        return jwt.verify(token);
    }

    private List<GrantedAuthority> getAuthorities(final Claims claims) {
        final String[] roles = claims.roles;
        return roles == null || roles.length == 0
                ? emptyList()
                : Arrays.stream(roles).map(SimpleGrantedAuthority::new).collect(toList());
    }

}
