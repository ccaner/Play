package play.baseline.stub;

import play.resultsetmock.annotations.Param;
import play.resultsetmock.annotations.Query;

/**
 * With this interface we map sql queries, updates, calls to Java methods.
 */
public interface MockDatabase {

    @Query("select * from pets where name = ? and age = ?")
    public Object queryPetsTable(
            @Param("name") String name,
            @Param("age") int age);

/*
    @Query("select * from pets where owner_firstname = ?")
    public Object queryPetsTable(
            @Param("owner_firstname") String ownerFirstName);
*/

    @Query("{ call load_pets_grouped(?, ?) }")
    public Object queryPetsGrouped(
            @Param String name,
            @Param int age);

    @Query("{ call count_by_age(?, ?) }")
    public Object countPetsByAge(
            @Param int age,
            @Param int[] count);

}
