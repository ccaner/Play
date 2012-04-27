package play.resultsetmock.model.provider;

import play.resultsetmock.model.query.Query;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: akpinarc
 * Date: 4/27/12
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */
public interface DataProvider {

    <T> T query(Query<T> query); // throws NotFoundException

}
