package com.meiyou.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Deprecated
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})  
@Retention(RetentionPolicy.CLASS)                                  
public @interface Print {                                     
}