package cod.extract.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import cucumber.api.testng.AbstractTestNGCucumberTests;

public class BaseDatabaseClass extends AbstractTestNGCucumberTests{
	
	Logger log = Logger.getLogger(BaseDatabaseClass.class.getName());
	
	// This is a base class for loading the DB connection
	private static Connection connection; 
	
	/**
	 * Accessor method for connection
	 * @return
	 */
	public static Connection getConnection()
	{
		return connection;
	}

	
	
	@BeforeClass
	public void setUp()
	{
		
		String url = System.getProperty("db.url");
		String driverClass = System.getProperty("db.driverclass");
		String user = System.getProperty("db.username");
		String password = System.getProperty("db.password");
		
		
		if(url ==null)
		{
			log.error("cannot find url property");
			Assert.fail("Test setup failure - no db url property");
		}
		if(driverClass ==null)
		{
			log.error("cannot find driver property");
			Assert.fail("Test setup failure - no driver property");
		}
		if(user ==null)
		{
			log.error("cannot find DB user property");
			Assert.fail("Test setup failure - no db user property");
		}
		if(password ==null)
		{
			log.error("cannot find DB password property");
			Assert.fail("Test setup failure - no db password property");
		}
		
		log.info("DB url: "+url);
		//url = "jdbc:postgresql://localhost:5432/postgres";
		
		log.info("using driver: "+driverClass);
		//driverClass = "org.postgresql.Driver";
		//user = "postgres";
		//password="password";
		//log.info("path :"+path);
		
		  connection = connectToDatabase(url,driverClass,user,password);
	}
	
	
	private Connection connectToDatabase(String url,String driverClass,String user,String password)
	  {
	    Connection conn = null;
	    try
	    {
	      Class.forName(driverClass);
	      
	      log.info("get DB connection");
	      conn = DriverManager.getConnection(url,user, password);
	      log.info("got DB connection!");
	    }
	    catch (ClassNotFoundException e)
	    {
	      e.printStackTrace();
	      System.exit(1);
	    }
	    catch (SQLException e)
	    {
	      e.printStackTrace();
	      System.exit(2);
	    }
	    return conn;
	  }

	
	@AfterClass
	public void tearDown()
	{
		if (connection != null)
		{
			log.info("Closing DB Connection");
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
	}
	
	
	
	
}
