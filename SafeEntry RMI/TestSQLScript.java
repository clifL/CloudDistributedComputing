import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestSQLScript {
	public static void main( String args[] ) {
	Connection c = null;
    Statement stmt = null;
      
    try {
    	String covidin= "2021-06-10 00:01:03";
    	String covidout= "2021-06-10 00:47:14";
		String nric= "S9732250C";
		String name = "CSM";
		String location = "Ang Mo Kio";
		boolean boolValue= false;
		String guarantor = "Sergeant Nick";
		boolean onBehalf= true;
		int flag = (boolValue)? 1 : 0;
		int clientId= 1;
		String date="2021-06-09 22:44:24";
		String dateout="2021-06-09 23:51:08";
         Class.forName("org.sqlite.JDBC");
         c = DriverManager.getConnection("jdbc:sqlite:User.db");
         System.out.println("Opened database successfully");
         stmt = c.createStatement();
         c.setAutoCommit(false);
         //Update user's NRIC that have Null values for Check out time to the current time
//         String sql1 = "UPDATE USERCHECKIN SET CHECKOUTIME = datetime('now') WHERE NRIC= "+"\""+nric+"\"" + " AND CHECKOUTIME IS NULL";
//         stmt.addBatch(sql1);
//         String sql2="UPDATE USERCHECKIN SET INFORMED = TRUE WHERE NRIC= "+"\""+nric+"\"" + " AND LOCATION = "+"\""+location+"\"";
//         stmt.addBatch(sql2);
//         //Insertion if checking in alone
//         if (!onBehalf) {
//        	 String sql3= "INSERT INTO USERCHECKIN (NRIC,NAME,LOCATION,ONBEHALF,GUARANTOR,CHECKINTIME,CHECKOUTIME,CLIENTID) VALUES("+"\""+nric+"\"" +","+ "\""+name+"\"" +","+ "\""+location+"\"" +","+ "\""+flag+"\"" + "," + "NULL" + "," + "datetime('now')" + "," + "NULL" + "," +  clientId + ")"; 
//        	 stmt.addBatch(sql3);
//         }
//         //Insertion if checking in with guarantor
//         if (onBehalf) {
//        	 String sql3= "INSERT INTO USERCHECKIN (NRIC,NAME,LOCATION,ONBEHALF,GUARANTOR,CHECKINTIME,CHECKOUTIME,CLIENTID) VALUES("+"\""+nric+"\"" +","+ "\""+name+"\"" +","+ "\""+location+"\"" +","+ "\""+flag+"\"" + "," + "\""+guarantor+"\"" + "," + "datetime('now')" + "," + "NULL" + "," +  clientId + ")";  	 
//        	 stmt.addBatch(sql3);
////        	 String sql3 = "INSERT INTO USER (NRIC,NAME) VALUES("+"\""+nric+"\"" +","+ "\""+name+"\")";
////        	 stmt.addBatch(sql3);
//         }
//         String select= "SELECT * FROM  USERCHECKIN WHERE NRIC = "+"\""+nric+"\"";
//         ResultSet rs = stmt.executeQuery(select);
//         while (rs.next()) {
//             String locationn = rs.getString("LOCATION");
//             String checkintime = rs.getString("CHECKINTIME");
//             String checkoutime= rs.getString("CHECKOUTIME");
//             System.out.println("You have visited:" + locationn + "Check in time: "+ checkintime + "Check out time: "+ checkoutime );
//         }
         String checkInTimeStamp = "2021-06-12T00:10";
         String checkOutTimeStamp ="2021-06-12T00:11";
         String select=  "SELECT DISTINCT NRIC FROM USERCHECKIN WHERE LOCATION = " + "\""+location+"\"" + " AND CHECKINTIME BETWEEN '" + checkInTimeStamp + "' AND '" + checkOutTimeStamp + "'" + " UNION" + " SELECT DISTINCT NRIC FROM USERCHECKIN WHERE LOCATION = " + "\""+location+"\"" + " AND CHECKOUTIME <=  '"+ checkOutTimeStamp +"'";
         ResultSet rs=stmt.executeQuery(select);
         System.out.println("before while");
         while (rs.next()) {
    	 String nricResult= rs.getString("NRIC");
//         String locationn = rs.getString("LOCATION");
//         String checkintime = rs.getString("CHECKINTIME");
//         String checkoutime= rs.getString("CHECKOUTIME");
//         System.out.println("You have visited:" + locationn + "Check in time: "+ checkintime + "Check out time: "+ checkoutime );
    	   System.out.println("inside while");
    	 	System.out.println("List of NRIC: " + nricResult );
       }
       
       String sql2= "UPDATE USERCHECKIN SET CLOSEENCOUNTER = TRUE WHERE NRIC= "+"\""+nric+"\"" + " AND LOCATION = "+"\""+location+"\"";
       stmt.executeUpdate(sql2);
       
       
//       stmt.executeBatch();
         //Explicitly commit statements to apply changes
         c.commit();
         //Close Statement object and Connection
         stmt.close();
         c.close();
      }
     catch (Exception e) {
    	  System.err.println( e.getClass().getName() + ": " + e.getMessage() );
          System.exit(0);
      }

	System.out.println("Okay done!");}
}

