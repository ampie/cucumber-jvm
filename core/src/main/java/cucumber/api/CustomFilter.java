package cucumber.api;

import gherkin.formatter.Filter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CustomFilter {
    Class<? extends Filter> filterClass();
    String[] filterParams() default {};
    Class<?> parameterType() default String.class;
}
