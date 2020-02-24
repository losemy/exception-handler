package com.github.losemy.exceptionhandler.handler;

import org.springframework.core.ExceptionDepthComparator;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author lose
 * @date 2019-12-02
 **/
public class BizHandlerMethodResolver {

    /**
     * A filter for selecting {@code @ExceptionHandler} methods.
     */
    public static final ReflectionUtils.MethodFilter EXCEPTION_HANDLER_METHODS = method ->
            AnnotatedElementUtils.hasAnnotation(method, BizHandler.class);


    private final Map<Class<? extends Throwable>, Method> mappedMethods = new HashMap<>(16);

    private final Map<Class<? extends Throwable>, Method> exceptionLookupCache = new ConcurrentReferenceHashMap<>(16);


    public BizHandlerMethodResolver(Class<?> handlerType) {
        for (Method method : MethodIntrospector.selectMethods(handlerType, EXCEPTION_HANDLER_METHODS)) {
            for (Class<? extends Throwable> exceptionType : detectExceptionMappings(method)) {
                addExceptionMapping(exceptionType, method);
            }
        }
    }


    private void addExceptionMapping(Class<? extends Throwable> exceptionType, Method method) {
        Method oldMethod = this.mappedMethods.put(exceptionType, method);
        if (oldMethod != null && !oldMethod.equals(method)) {
            throw new IllegalStateException("Ambiguous @ExceptionHandler method mapped for [" +
                    exceptionType + "]: {" + oldMethod + ", " + method + "}");
        }
    }


    /**
     * Find a {@link Method} to handle the given exception.
     * Use {@link ExceptionDepthComparator} if more than one match is found.
     * @param exception the exception
     * @return a Method to handle the exception, or {@code null} if none found
     */
    @Nullable
    public Method resolveMethod(Exception exception) {
        return resolveMethodByThrowable(exception);
    }

    /**
     * Find a {@link Method} to handle the given Throwable.
     * Use {@link ExceptionDepthComparator} if more than one match is found.
     * @param exception the exception
     * @return a Method to handle the exception, or {@code null} if none found
     * @since 5.0
     */
    @Nullable
    public Method resolveMethodByThrowable(Throwable exception) {
        Method method = resolveMethodByExceptionType(exception.getClass());
        if (method == null) {
            Throwable cause = exception.getCause();
            if (cause != null) {
                method = resolveMethodByExceptionType(cause.getClass());
            }
        }
        return method;
    }


    /**
     * Return the {@link Method} mapped to the given exception type, or {@code null} if none.
     */
    @Nullable
    private Method getMappedMethod(Class<? extends Throwable> exceptionType) {
        List<Class<? extends Throwable>> matches = new ArrayList<>();
        for (Class<? extends Throwable> mappedException : this.mappedMethods.keySet()) {
            if (mappedException.isAssignableFrom(exceptionType)) {
                matches.add(mappedException);
            }
        }
        if (!matches.isEmpty()) {
            matches.sort(new ExceptionDepthComparator(exceptionType));
            return this.mappedMethods.get(matches.get(0));
        }
        else {
            return null;
        }
    }

    /**
     * Find a {@link Method} to handle the given exception type. This can be
     * useful if an {@link Exception} instance is not available (e.g. for tools).
     * @param exceptionType the exception type
     * @return a Method to handle the exception, or {@code null} if none found
     */
    @Nullable
    public Method resolveMethodByExceptionType(Class<? extends Throwable> exceptionType) {
        Method method = this.exceptionLookupCache.get(exceptionType);
        if (method == null) {
            method = getMappedMethod(exceptionType);
            this.exceptionLookupCache.put(exceptionType, method);
        }
        return method;
    }


    /**
     * Extract exception mappings from the {@code @ExceptionHandler} annotation first,
     * and then as a fallback from the method signature itself.
     */
    @SuppressWarnings("unchecked")
    private List<Class<? extends Throwable>> detectExceptionMappings(Method method) {
        List<Class<? extends Throwable>> result = new ArrayList<>();
        detectAnnotationExceptionMappings(method, result);
        if (result.isEmpty()) {
            for (Class<?> paramType : method.getParameterTypes()) {
                if (Throwable.class.isAssignableFrom(paramType)) {
                    result.add((Class<? extends Throwable>) paramType);
                }
            }
        }
        if (result.isEmpty()) {
            throw new IllegalStateException("No exception types mapped to " + method);
        }
        return result;
    }

    private void detectAnnotationExceptionMappings(Method method, List<Class<? extends Throwable>> result) {
        BizHandler ann = AnnotatedElementUtils.findMergedAnnotation(method, BizHandler.class);
        Assert.state(ann != null, "No ExceptionHandler annotation");
        result.addAll(Arrays.asList(ann.value()));
    }
}
