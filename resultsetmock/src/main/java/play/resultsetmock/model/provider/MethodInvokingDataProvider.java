package play.resultsetmock.model.provider;

import play.resultsetmock.model.provider.DataProvider;
import play.resultsetmock.model.query.Query;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 *  Class that populates data by executing a method.
 *  Query annotation is backed by an instance of this
 */
public class MethodInvokingDataProvider<T> implements DataProvider {

/*
    Object object;
    Method method;


    @Override
    public List<T> query(Query query) {
        try {
            return (List<T>) method.invoke(object, args);
        } catch (IllegalAccessException e) {

        } catch (InvocationTargetException e) {

        }
        return null;
    }

    @Override
    public boolean canAnswer(Query query) {
//        query.getIdentifier
//        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
*/

    @Override
    public <T> T query(Query<T> query) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
