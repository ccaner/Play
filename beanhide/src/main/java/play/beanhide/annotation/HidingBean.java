package play.beanhide.annotation;

@java.lang.annotation.Inherited
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({java.lang.annotation.ElementType.TYPE})
public @interface HidingBean {

    java.lang.String value();

    java.lang.String hidesBean() default "";

    java.lang.Class<?> hidesClass() default Class.class;

}
