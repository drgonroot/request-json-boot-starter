package com.byte102.spring;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

/**
 * RequestJsonParam解析器
 * Created by useheart on 2023/7/11
 * @author useheart
 */
public class RequestJsonParamMethodArgumentResolver extends RequestJsonMethodArgumentResolver {
    private final ObjectMapper objectMapper;

    public RequestJsonParamMethodArgumentResolver(List<HttpMessageConverter<?>> converters, List<Object> requestResponseBodyAdvice, ObjectMapper objectMapper) {
        super(converters, requestResponseBodyAdvice, objectMapper);
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestJsonParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        parameter = parameter.nestedIfOptional();
        RequestJsonParam requestJsonParam = parameter.getParameterAnnotation(RequestJsonParam.class);
        if (requestJsonParam == null) {
            return null;
        }
        boolean isRequired = (requestJsonParam.required() && !parameter.isOptional());
        String name = StringUtils.hasLength(requestJsonParam.name()) ? requestJsonParam.name() : parameter.getParameterName();
        Object arg = super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        String defaultValue = requestJsonParam.defaultValue();
        arg = this.handleValue(name, arg, defaultValue, parameter, isRequired);

        handleDataBinder(name, arg, parameter, mavContainer, webRequest, binderFactory);

        return adaptArgumentIfNecessary(arg, parameter);
    }

    private Object handleValue(String name, Object value, String defaultValue, MethodParameter parameter, boolean isRequired) throws MissingServletRequestParameterException {
        NextStep nextStep = checkValueAndReturn(name, value, defaultValue, parameter, isRequired);
        if (!nextStep.hasNext()) {
            return nextStep.getValue();
        }
        JsonNode rootNode = (JsonNode) value;
        String atName = "/" + name.replace('.', '/');
        JsonNode atNode = rootNode.at(atName);
        Object arg = null;
        if (!atNode.isMissingNode()) {
            arg = objectMapper.convertValue(atNode, parameter.getParameterType());
        }
        nextStep = checkValueAndReturn(name, arg, defaultValue, parameter, isRequired);
        return nextStep.getValue();
    }
}
