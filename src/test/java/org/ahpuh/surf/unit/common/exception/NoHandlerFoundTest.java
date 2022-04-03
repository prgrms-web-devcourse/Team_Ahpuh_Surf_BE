package org.ahpuh.surf.unit.common.exception;

import org.ahpuh.surf.jwt.JwtAuthenticationToken;
import org.ahpuh.surf.unit.ControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.ResultActions;

import static org.ahpuh.surf.common.factory.MockJwtFactory.createJwtToken;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NoHandlerFoundTest extends ControllerTest {

    @BeforeEach
    void setUp() {
        final JwtAuthenticationToken authentication = createJwtToken(1L, "testEmail@naver.com");
        final SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        given(securityContext.getAuthentication())
                .willReturn(authentication);
    }

    @DisplayName("잘못된 URI path가 입력되면 400 응답을 보낸다.")
    @Test
    void noHandlerFoundException_400() throws Exception {
        // When
        final ResultActions perform = mockMvc.perform(get("/api/v1/invalid")
                .header(HttpHeaders.AUTHORIZATION, "TestToken"));

        // Then
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorMessage").value("No handler found for GET /api/v1/invalid"))
                .andDo(print());
    }
}
