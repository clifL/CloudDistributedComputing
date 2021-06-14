/*
	  Code: History.java
	  Date: 29th May 2021

	  Usage: This class is used as blueprint for returning back the SafeEntry history to the users.
      It will be wrapped into an arraylist of history objects and parse to the callback function.
*/


import java.io.Serializable;
import java.time.LocalDateTime;

public class History implements Serializable{
    public String location;
    public LocalDateTime checkInTimeStamp;
    public LocalDateTime checkOutTimeStamp;
    
}
