package com.isucorp.acmecompanytest;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

@Target({TYPE, METHOD, FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Info
{
    String value();
}
