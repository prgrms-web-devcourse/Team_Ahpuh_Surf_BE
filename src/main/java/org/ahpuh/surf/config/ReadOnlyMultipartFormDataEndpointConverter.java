package org.ahpuh.surf.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import java.lang.reflect.Type;

public class ReadOnlyMultipartFormDataEndpointConverter extends MappingJackson2HttpMessageConverter {

    public ReadOnlyMultipartFormDataEndpointConverter(final ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public boolean canRead(final Type type, final Class<?> contextClass, final MediaType mediaType) {
        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return false;
        }
        final HandlerMethod handlerMethod = (HandlerMethod) requestAttributes
                .getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        if (handlerMethod == null) {
            return false;
        }
        final RequestMapping requestMapping = handlerMethod.getMethodAnnotation(RequestMapping.class);
        if (requestMapping == null) {
            return false;
        }
        if (requestMapping.consumes().length != 1
                || !MediaType.MULTIPART_FORM_DATA_VALUE.equals(requestMapping.consumes()[0])) {
            return false;
        }
        return super.canRead(type, contextClass, mediaType);
    }

    @Override
    protected boolean canWrite(final MediaType mediaType) {
        return false;
    }
}
