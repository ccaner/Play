package play.baseline.stub;

import play.resultsetmock.annotations.Param;
import play.resultsetmock.annotations.Query;

/**
 * With this interface we map sql queries, updates, calls to Java methods.
 */
public interface MockDatabase {

    @Query("select * from pets where name = ? and age = ?")
    public QueryResult queryPetsTable(
            @Param("name") String name,
            @Param("age") int age);

/*
    @Query("select * from pets where owner_firstname = ?")
    public QueryResult queryPetsTable(
            @Param("owner_firstname") String ownerFirstName);
*/


}
