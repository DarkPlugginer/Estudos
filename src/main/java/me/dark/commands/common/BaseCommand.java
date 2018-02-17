/*
 * Copyright (c) 16/2/2018. Projeto desenvolvido por Miguel Lukas.
 * Não remova este quote.
 */

package me.dark.commands.common;

import me.dark.commands.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BaseCommand {

    String[] aliases();

    Permission permission();

    String usage() default "";

    String desc();

    boolean player() default true;

    boolean console() default true;

    int min() default 0;

    int max() default -1;

    boolean hidden() default false;
}
