package org.review.amz.design_pattern.creational.singleton;

import org.junit.Test;

public class DBConnectionTest {

    @Test
    public void runSingletonTest() {
        DBConnection dbConnection = DBConnection.getInstance("name");
        System.out.println("dbConnection name " + dbConnection.getName());

        //this proof that process are running the same instance for all objects
        DBConnection dbConnection2 = DBConnection.getInstance("name2");
        assert dbConnection2.getName() == dbConnection.getName();
    }
}
