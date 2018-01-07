package stepdefinition;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testng.Assert;

import cod.extract.test.BaseDatabaseClass;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class DBTest {
	
	Logger log = Logger.getLogger(DBTest.class.getName());
	
	
	Map<String, Integer> typeMap = new HashMap<String, Integer>();

	public DBTest()
	{
		// load the look up for recordtypes
		typeMap.put("AT", 37);
		typeMap.put("PCO", 46);
		typeMap.put("Contractor", 8);
		typeMap.put("Pharmacy-YP", 1337);
		typeMap.put("Pharmacy-Grouping", 1338);
	}
	
	@Given("^An initial extract_load run$")
	public void an_initial_extract_load_run() throws Throwable {
	    // no ation
	}
	
	@Given("^the database is empty$")
	public void the_database_is_empty() throws Throwable {
	   // no action
	}
	
	@When("^extract_load run is completed$")
	public void extract_load_run_is_completed() throws Throwable {
	   // no action
	}
	
	@Then("^the database contains \"([^\"]*)\" record types$")
	public void the_database_contains_record_types(String recordType) throws Throwable {
		
	
		log.info("contains record type "+ recordType);
		
		// lookup 
		int betType = (int) typeMap.get(recordType);
		
		if (betType ==0)
		{
			Assert.fail("unknown recordType "+recordType);
		}
		
		String check="Select count(*) from organisation org where org.cip_bet_type = "+betType;
		
		Connection connection = BaseDatabaseClass.getConnection();
		Statement st=connection.createStatement();
		ResultSet rs= st.executeQuery(check);
		
		rs.next();
		int count = rs.getInt(1);
		rs.close();
		
		log.info("total records on DB:"+count);
		
		Assert.assertTrue(count>0, "Expected organisation records of type "+betType+ " but none found");
		
		
	   
	}
	
	
	
	@Then("^the database contains at least (\\d+) \"([^\"]*)\" records$")
	public void the_database_contains_at_least_records(int expectedRecordCount, String dbTable) throws Throwable {
	   
		log.info("contains at least "+ expectedRecordCount + "records for table: "+dbTable);
		
		String check="Select count(*) from " + dbTable;
		
		Connection connection = BaseDatabaseClass.getConnection();
		Statement st=connection.createStatement();
		ResultSet rs= st.executeQuery(check);
		
		rs.next();
		int count = rs.getInt(1);
		rs.close();
		
		log.info("total records on DB: "+count);
		
		Assert.assertTrue(count>=expectedRecordCount, "Expected records in table "+dbTable+ " to be more than "+expectedRecordCount+" but was "+count);
		
	}
	
	@Then("^there are more \"([^\"]*)\" records than \"([^\"]*)\"  records$")
	public void there_are_more_records_than_records(String dbTable1, String dbTable2) throws Throwable {
	    
		log.info("more records in table "+ dbTable1 + "than records for table"+dbTable2);
		
		
		String check1="Select count(*) from " + dbTable1;
		String check2="Select count(*) from " + dbTable2;
		
		Connection connection = BaseDatabaseClass.getConnection();
		Statement st=connection.createStatement();
		ResultSet rs1= st.executeQuery(check1);
		
		rs1.next();
		int count1 = rs1.getInt(1);
		rs1.close();
		
		ResultSet rs2= st.executeQuery(check2);
		
		rs2.next();
		int count2 = rs2.getInt(1);
		rs2.close();
		
		log.info("total records on DB table1:"+count1);
		log.info("total records on DB table2:"+count2);
		
		Assert.assertTrue(count1>count2, "Expected more records in table "+dbTable1+ " than in table " + dbTable2);
		
	}
	
	

}
