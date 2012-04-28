package play.resultsetmock.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RsColumn {

    /* Result set column label */
    String value();

    /* Result set column index */
    int index() default -1;

}
