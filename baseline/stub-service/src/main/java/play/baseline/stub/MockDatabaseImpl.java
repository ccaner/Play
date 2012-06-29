package play.baseline.stub;

import play.baseline.model.Pet;
import play.remotemock.annotation.Remotable;
import play.resultsetmock.annotations.Param;
import play.resultsetmock.annotations.Query;

import java.util.List;

/**
 * Default responses for sql calls, updates, queries... So that stub server starts up without errors.
 */
@Remotable(MockDatabase.class)
public class MockDatabaseImpl implements MockDatabase {

    @Override
    public Object queryPetsTable(String name, int age) {
        return null;
    }

    @Override
    public Object queryPetsGrouped(String name, int age) {
        return null;
    }

/*
    @Override
    public Object queryPetsTable(String ownerFirstName) {
        return null;
    }
*/

}
