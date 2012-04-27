package play.resultsetmock.model;

import play.resultsetmock.model.provider.DataProvider;
import play.resultsetmock.model.query.Query;

import java.sql.ResultSet;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: akpinarc
 * Date: 4/27/12
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResultSetPopulator {
    
    List<DataProvider> providerRegistry;
    
    public ResultSet getResultSet(Query query) {
        List result = null;
        for (DataProvider dataProvider : providerRegistry) {
            if (dataProvider.canAnswer(query)) {
                result = dataProvider.query(query);
            }
        }
        return wrap(result);
    }
    
    ResultSet wrap(List result) {
        return null;
    }
    
    
}
