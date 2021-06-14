import java.sql.*;

public class SQLiteJDBC {

   public static void main( String args[] ) {
      Connection c = null;
      Statement stmt = null;
      
      try {
         Class.forName("org.sqlite.JDBC");
         c = DriverManager.getConnection("jdbc:sqlite:User.db");
         System.out.println("Opened database successfully");

         stmt = c.createStatement();
      //Set auto-commit to false
         c.setAutoCommit(false);
         
         String sql1 = "DROP TABLE IF EXISTS 'USERCHECKIN' ";
         stmt.addBatch(sql1);
         
         String sql2 = "CREATE TABLE USERCHECKIN " + 
        		 		"(ID			 INTEGER 	PRIMARY KEY   	AUTOINCREMENT, " +
                        " NRIC 			 VARCHAR(9)      NOT NULL, " +
                        " NAME           TEXT    NOT NULL, " + 
                        " LOCATION       CHAR(100)     NOT NULL, " +  
                        " ONBEHALF       BOOLEAN, " + 
                        " GUARANTOR 	 TEXT, " + 
                        " CHECKINTIME	 TEXT,	"+
                        " CHECKOUTIME	 TEXT,"+
                        " CLIENTID 		 INTEGER, "+
                        " ClOSEENCOUNTER BOOLEAN DEFAULT FALSE, "+ 
                        " INFORMED		 BOOLEAN DEFAULT FALSE, " +
                        " MESSAGE      TEXT)";
         stmt.addBatch(sql2);
         
         String sql3 = "DROP TABLE IF EXISTS 'USER' ";
         stmt.addBatch(sql3);
         
         stmt.executeBatch();
       //Explicitly commit statements to apply changes
         c.commit();
       //Close Statement object and Connection
         stmt.close();
         c.close();
      } catch ( Exception e ) {
         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
         System.exit(0);
      }
      System.out.println("Table created successfully");
   }
}