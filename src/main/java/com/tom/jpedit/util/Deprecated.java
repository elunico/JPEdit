package com.tom.jpedit.util;

public @interface Deprecated {
  String forPackage() default "";

  String value() default "";

  String since() default "";

  boolean forRemoval() default false;

  String replaceWith() default "";

}
