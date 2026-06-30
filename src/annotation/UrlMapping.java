package src.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import src.utils.MethodHttp;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UrlMapping {
    String url();
    MethodHttp methodHttp();
}
