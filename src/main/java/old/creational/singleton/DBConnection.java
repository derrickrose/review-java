package old.creational.singleton;

import lombok.Getter;


public class DBConnection {

    private static DBConnection dbConnection;
    @Getter
    private String name;

    private DBConnection(String name) {
        this.name = name;
    }

    public static DBConnection getInstance(String name) {
        if (dbConnection == null) {
            dbConnection = new DBConnection(name);
        }
        return dbConnection;
    }

}
