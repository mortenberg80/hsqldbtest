package no.grabas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.Test;

public class OnDuplicatKeyUpdateTest {
    @Test
    void testFoo() throws Exception {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
        } catch (Exception e) {
            System.err.println("ERROR: failed to load HSQLDB JDBC driver.");
            e.printStackTrace();
            return;
        }

        Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:mymemdb", "SA", "");

        Statement stmt = c.createStatement();

        stmt.execute("SET DATABASE SQL SYNTAX MYS TRUE");

        int result = stmt.executeUpdate(
                "CREATE TABLE foo (" +
                        " id INT PRIMARY KEY NOT NULL, " +
                        " counter INT);");

        stmt.executeUpdate("INSERT INTO foo(id, counter) VALUES (1, 1) ON DUPLICATE KEY UPDATE counter=counter+1");
        ResultSet resultSet = stmt.executeQuery("SELECT * FROM foo");

        resultSet.next();
        System.out.println("id: " + resultSet.getInt("id"));
        System.out.println("counter: " + resultSet.getInt("counter")); // this prints 1

        stmt.executeUpdate("INSERT INTO foo(id, counter) VALUES (1, 1) ON DUPLICATE KEY UPDATE counter=counter+1");

        resultSet = stmt.executeQuery("SELECT * FROM foo");

        resultSet.next();
        System.out.println("id: " + resultSet.getInt("id"));
        System.out.println("counter: " + resultSet.getInt("counter")); // this prints 2

        stmt.executeUpdate("INSERT INTO foo(id, counter) VALUES (1, 1) ON DUPLICATE KEY UPDATE counter=counter+1");

        resultSet = stmt.executeQuery("SELECT * FROM foo");

        resultSet.next();
        System.out.println("id: " + resultSet.getInt("id"));
        System.out.println("counter: " + resultSet.getInt("counter")); // This prints 2, I expected this to be 3 (!)

        // Side note - this shows that the update statement fetches 'counter' from VALUES(...):
        stmt.executeUpdate("INSERT INTO foo(id, counter) VALUES (1, 10) ON DUPLICATE KEY UPDATE counter=counter+1");
        resultSet = stmt.executeQuery("SELECT * FROM foo");

        resultSet.next();
        System.out.println("id: " + resultSet.getInt("id"));
        System.out.println("counter: " + resultSet.getInt("counter")); // this prints 11
    }
}
