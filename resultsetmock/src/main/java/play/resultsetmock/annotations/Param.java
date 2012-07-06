package play.resultsetmock.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {

    /* Something to identify query, we will use SQL.contains */
    String value() default "";

}
