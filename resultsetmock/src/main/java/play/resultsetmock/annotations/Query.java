package play.resultsetmock.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Query {

    /* Something to identify query, we will use string contains */
    String value();

}
