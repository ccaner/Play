package play.resultsetmock.jdbc.iki;

import play.resultsetmock.jdbc.iki.data.TabularDataProvider;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: akpinarc
 * Date: 6/29/12
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResultSetDataProviderFactory {
    
    public static TabularDataProvider createDataProvider(Object result) {
        if (result instanceof List) {

        } else if (result instanceof Map) {

        }
        throw new IllegalArgumentException();
    }
}
