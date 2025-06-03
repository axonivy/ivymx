package com.axonivy.jmx.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.axonivy.jmx.MInclude;
import com.axonivy.jmx.MOperation;

/**
 * Contains methods that are commonly used.
 * @author rwei
 * @since 01.07.2013
 */
class MInternalUtils {
  static String getAttributeName(Method method, String annotatedName) {
    if (StringUtils.isEmpty(annotatedName)) {
      annotatedName = StringUtils.uncapitalize(getAttributeNameFromMethod(method));
    }
    return annotatedName;
  }

  static String getAttributeName(Field field, String annotatedName) {
    if (StringUtils.isEmpty(annotatedName)) {
      annotatedName = field.getName();
    }
    return annotatedName;
  }

  static String getAttributeNameFromMethod(Method method) {
    String implementationAttributeName = method.getName();
    if (implementationAttributeName.startsWith("get")) {
      implementationAttributeName = StringUtils.substringAfter(implementationAttributeName, "get");
    } else if (implementationAttributeName.startsWith("is")) {
      implementationAttributeName = StringUtils.substringAfter(implementationAttributeName, "is");
    } else {
      throw new IllegalArgumentException("Name of getter method for an attribute must start with get...() or is...() but is '" + implementationAttributeName + "'");
    }
    return implementationAttributeName;
  }

  static String getOperationName(Method method, MOperation operation) {
    if (StringUtils.isEmpty(operation.name())) {
      return method.getName();
    }
    return operation.name();
  }

  static String getDescription(String description, String name) {
    if (StringUtils.isEmpty(description)) {
      return name;
    }
    return description;
  }

  static Class<?> getIncludedType(Class<?> declaredType, MInclude include) {
    if (include.type() == Void.class) {
      return declaredType;
    }
    Class<?> type = include.type();
    if (!declaredType.isAssignableFrom(type)) {
      throw new IllegalArgumentException("The type given on @MInclude annotation must be a subtype of the annotated field type or return type of the annotation method. Given type: '" + type + "'. Field or method return type: '" + declaredType + "'");
    }
    return type;
  }

  public static Type getManagedTyped(Type declaredType, Class<?> annotatedType) {
    if (annotatedType == Void.class) {
      return declaredType;
    }
    final Class<?> type = annotatedType;
    if (declaredType instanceof Class) {
      if (!isAssignable(declaredType, type)) {
        throw new IllegalArgumentException("The type given on an annotation must be a subtype of the annotated field type or return type of the annotation method. Given type: '" + type + "'. Field or method return type: '" + declaredType + "'");
      }
      return type;
    } else if (declaredType instanceof ParameterizedType) {
      final ParameterizedType parameterizedDeclaredType = (ParameterizedType) declaredType;
      final Type declaredRawType = parameterizedDeclaredType.getRawType();
      if (!isListClass(declaredRawType)) {
        throw new IllegalArgumentException("The parameter type on annotations is not supported for generics classes expect java.util.List and subclasses of it.");
      }
      if (!hasOneGenericParameter(parameterizedDeclaredType)) {
        throw new IllegalArgumentException("The parameter type on annotations is not supported for generics classes with no or more than one generic parameter.");
      }
      declaredType = ((ParameterizedType) declaredType).getActualTypeArguments()[0];
      if (!isAssignable(declaredType, type)) {
        throw new IllegalArgumentException("The type given on an annotation must be a subtype of the annotated field type or return type of the annotation method. Given type: '" + type + "'. Field or method return type: '" + declaredType + "'");
      }
      return new ParameterizedType(){

        @Override
        public Type[] getActualTypeArguments() {
          return new Type[] {type};
        }

        @Override
        public Type getRawType() {
          return declaredRawType;
        }

        @Override
        public Type getOwnerType() {
          return parameterizedDeclaredType.getOwnerType();
        }
      };
    }
    throw new IllegalArgumentException("Not allowed to declare type attribute on an annotation if annotated field type or return type of the annotation method is '" + declaredType + "'");
  }

  static Method[] getNonSyntheticDeclaredMethods(Class<?> type) {
    return Stream.of(type.getDeclaredMethods())
        .filter(method -> !method.isSynthetic())
        .toArray(Method[]::new);
  }

  private static boolean isAssignable(Type toType, Class<?> fromType) {
    return toType instanceof Class && ((Class<?>) toType).isAssignableFrom(fromType);
  }

  private static boolean hasOneGenericParameter(ParameterizedType type) {
    return type.getActualTypeArguments() != null && type.getActualTypeArguments().length == 1;
  }

  private static boolean isListClass(Type type) {
    return type instanceof Class<?> && List.class.isAssignableFrom((Class<?>) type);
  }
}
