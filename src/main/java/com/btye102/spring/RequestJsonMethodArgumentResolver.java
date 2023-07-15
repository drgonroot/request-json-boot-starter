package com.btye102.spring;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * RequestJson解析器
 * Created by useheart on 2023/7/11
 * @author useheart
 */
public class RequestJsonMethodArgumentResolver extends AbstractMessageConverterMethodArgumentResolver {
    protected static final String REQUEST_JSON_NAME = RequestJsonMethodArgumentResolver.class.getName();
    protected static final Object NO_VALUE = new Object();
    private final ObjectMapper objectMapper;

    public RequestJsonMethodArgumentResolver(List<HttpMessageConverter<?>> converters, List<Object> requestResponseBodyAdvice, ObjectMapper objectMapper) {
        super(converters, requestResponseBodyAdvice);
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestJson.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Object arg = webRequest.getAttribute(REQUEST_JSON_NAME, RequestAttributes.SCOPE_REQUEST);
        parameter = parameter.nestedIfOptional();
        String name = parameter.getParameterName();
        if (arg != null) {
            return handleArgByRequestJson(name, arg, parameter, mavContainer, webRequest, binderFactory);
        }
        arg = readWithMessageConverters(webRequest, parameter, Map.class);
        arg = objectMapper.valueToTree(arg);
        webRequest.setAttribute(REQUEST_JSON_NAME, arg, RequestAttributes.SCOPE_REQUEST);
        return handleArgByRequestJson(name, arg, parameter, mavContainer, webRequest, binderFactory);
    }

    private Object handleArgByRequestJson(String name, Object arg, MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        if (parameter.hasParameterAnnotation(RequestJson.class)) {
            Type nestedGenericParameterType = parameter.getNestedGenericParameterType();
            if (!nestedGenericParameterType.equals(JsonNode.class)) {
                throw new MethodArgumentTypeMismatchException(null, JsonNode.class, name, parameter,
                        new Throwable("param class not equals: current class: " + nestedGenericParameterType + ",except class: " + JsonNode.class.getName()));
            }
            RequestJson requestJson = parameter.getParameterAnnotation(RequestJson.class);
            boolean isRequired = (requestJson.required() && !parameter.isOptional());
            arg = checkValueAndReturn(name, arg, ValueConstants.DEFAULT_NONE, parameter, isRequired).getValue();
            handleDataBinder(name, arg, parameter, mavContainer, webRequest, binderFactory);
            return adaptArgumentIfNecessary(arg, parameter);
        }
        return arg;
    }

    protected void handleDataBinder(String name, Object value, MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        if (binderFactory != null) {
            WebDataBinder binder = binderFactory.createBinder(webRequest, value, name);
            if (value != null) {
                validateIfApplicable(binder, parameter);
                if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter)) {
                    throw new MethodArgumentNotValidException(parameter, binder.getBindingResult());
                }
            }
            if (mavContainer != null) {
                mavContainer.addAttribute(BindingResult.MODEL_KEY_PREFIX + name, binder.getBindingResult());
            }
        }
    }

    protected NextStep checkValueAndReturn(String name, Object value, String defaultValue, MethodParameter parameter, boolean isRequired) throws MissingServletRequestParameterException {
        if (value == null || value == NO_VALUE || (value instanceof JsonNode && (((JsonNode) value).isNull() || ((JsonNode) value).isMissingNode()))) {
            if (isRequired) { // 要求必须要有值
                throw new MissingServletRequestParameterException(name, parameter.getNestedParameterType().getSimpleName());
            }
            if (!defaultValue.equals(ValueConstants.DEFAULT_NONE)) { // 无值时存在默认值则使用
                return new NextStep(false, defaultValue);
            }
            return new NextStep(false, null);
        }
        return new NextStep(true, value);
    }

    protected static class NextStep {
        private final boolean hasNext; // 是否有下一步
        private final Object value;

        public NextStep(boolean hasNext, Object value) {
            this.hasNext = hasNext;
            this.value = value;
        }

        public boolean hasNext() {
            return hasNext;
        }

        public Object getValue() {
            return value;
        }
    }
}
