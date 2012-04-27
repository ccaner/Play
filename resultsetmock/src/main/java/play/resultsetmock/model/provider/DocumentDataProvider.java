package play.resultsetmock.model.provider;

import org.w3c.dom.Document;
import play.resultsetmock.model.query.Query;

/**
 * Created by IntelliJ IDEA.
 * User: akpinarc
 * Date: 4/27/12
 * Time: 3:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class DocumentDataProvider implements DataProvider {

    Document db;


    @Override
    public <T> T query(Query<T> query) {
        return null;
    }

}
