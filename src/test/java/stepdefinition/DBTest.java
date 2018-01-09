package stepdefinition;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testng.Assert;

import cod.extract.test.base.BaseDatabaseClass;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class DBTest {

	Logger log = Logger.getLogger(DBTest.class.getName());

	// cip_bet_type mappings for cucumber
	Map<String, Integer> typeMap = new HashMap<String, Integer>();

	public DBTest() {
		// load the look up for record types
		typeMap.put("AT", 37);
		typeMap.put("PCO", 46);
		typeMap.put("Contractor", 8);
		typeMap.put("Pharmacy-YP", 1337);
		typeMap.put("Pharmacy-Grouping", 1338);
		typeMap.put("Division", 31);
	}

	@Given("^An initial extract_load run$")
	public void an_initial_extract_load_run() throws Throwable {
		// no action - pre condition
	}

	@Given("^the database is empty$")
	public void the_database_is_empty() throws Throwable {
		// no action - pre condition
	}

	@When("^extract_load run is completed$")
	public void extract_load_run_is_completed() throws Throwable {
		// no action - pre condition
	}

	@Then("^the database contains \"([^\"]*)\" record type$")
	public void the_database_contains_record_type(String recordType) throws Throwable {

		log.info("Check DB contains record type: " + recordType);

		// lookup cucumber type for bet_type_id
		int betType = (int) typeMap.get(recordType);

		if (betType == 0) {
			Assert.fail("Unknown recordType: " + recordType);
		}

		String query = "Select count(*) from organisation org where org.cip_bet_type = " + betType;

		log.info("run query :"+query);
		Connection connection = BaseDatabaseClass.getConnection();
		Statement st = connection.createStatement();
		ResultSet rs = st.executeQuery(query);

		rs.next();
		int count = rs.getInt(1);
		rs.close();

		log.info("Total " + recordType+ " records on DB:" + count);

		Assert.assertTrue(count > 0, "Expected organisation records of type " + betType + ", but none found");
	}

	@Then("^the database contains at least (\\d+) \"([^\"]*)\" records$")
	public void the_database_contains_at_least_records(int expectedRecordCount, String dbTable) throws Throwable {

		log.info("Check DB contains at least " + expectedRecordCount + " records for table: " + dbTable);

		String query = "Select count(*) from " + dbTable;

		log.info("run query :"+query);
		Connection connection = BaseDatabaseClass.getConnection();
		Statement st = connection.createStatement();
		ResultSet rs = st.executeQuery(query);

		rs.next();
		int count = rs.getInt(1);
		rs.close();

		log.info("Total records for table "+ dbTable +" on DB: " + count);

		Assert.assertTrue(count >= expectedRecordCount, "Expected records in table " + dbTable + " to be more than "
				+ expectedRecordCount + ", but was " + count);

	}

	@Then("^there are more \"([^\"]*)\" records than \"([^\"]*)\"  records$")
	public void there_are_more_records_than_records(String dbTable1, String dbTable2) throws Throwable {

		log.info("Check there are more records in table " + dbTable1 + " than records for table" + dbTable2);

		String check1 = "Select count(*) from " + dbTable1;
		String check2 = "Select count(*) from " + dbTable2;

		Connection connection = BaseDatabaseClass.getConnection();
		Statement st = connection.createStatement();
		ResultSet rs1 = st.executeQuery(check1);

		rs1.next();
		int count1 = rs1.getInt(1);
		rs1.close();

		ResultSet rs2 = st.executeQuery(check2);

		rs2.next();
		int count2 = rs2.getInt(1);
		rs2.close();

		log.info("Total records on DB table " + dbTable1 + ": " + count1);
		log.info("Total records on DB table " + dbTable2 + ": " + count2);

		Assert.assertTrue(count1 > count2, "Expected more records in table " + dbTable1 + " than in table " + dbTable2);

	}

	@Then("^the database contains only expected record types$")
	public void the_database_contains_only_expected_record_types() throws Throwable {

		// use the lookup map to get all valid cip_bet_types
		Collection<Integer> typeList = typeMap.values();
		String query = "Select distinct cip_bet_type from organisation where cip_bet_type not in ";
		query = query + sqlFormattedList(typeList);

		log.info("run query :"+query);
		Connection connection = BaseDatabaseClass.getConnection();
		Statement st = connection.createStatement();
		ResultSet rs = st.executeQuery(query);

		if (rs.isBeforeFirst()) {
			// fail as we have record types we are not expecting
			String badTypes = buildListFromResultset(rs,1);
			log.error("Unexpected record type(s) in organisation: " + badTypes);
			Assert.fail("Unexpected cip_bet_type(s) in organisation table : " + badTypes);
		}

		rs.close();

	}

	/**
	 * Builds a SQL IN clause from the list
	 * 
	 * @param typeList
	 * @return
	 */
	private String sqlFormattedList(Collection<Integer> typeList) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (Integer i : typeList) {
			sb.append(i + ",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(")");
		return sb.toString();
	}

	@Then("^there are no duplicate organisation records$")
	public void there_are_no_duplicate_database_organisation_records() throws Throwable {

		String query = "select alt_code from organisation ou where (select count(*) from organisation inr "
				+ "where inr.alt_code = ou.alt_code) > 1 ";

		log.info("No Duplicate organisation record check: " + query);
		Connection connection = BaseDatabaseClass.getConnection();
		Statement st = connection.createStatement();
		ResultSet rs = st.executeQuery(query);

		if (rs.isBeforeFirst()) {
			// fail as there are duplicate records - we should have an empty resultset
			String duplicateCodes = buildListFromResultset(rs,1);
			log.error("Duplicate records in organisation table: " + duplicateCodes);
			Assert.fail("Duplicate records in organisation table: "+duplicateCodes);
		}

		rs.close();

	}
	
	/**
	 * build a comma delimited list from resultset column
	 * @param rs
	 * @param position - used for the get of the result set - ie column
	 * @return
	 * @throws Throwable
	 */
	private String buildListFromResultset(ResultSet rs,int position) throws Throwable{
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		String i;
		while(rs.next())
		{
			// getstring will work for int columns as well
			i = rs.getString(position);
			sb.append(i + ",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("]");
		return sb.toString();
	}
	
	
	@Then("^the database contains \"([^\"]*)\" of \"([^\"]*)\"$")
	public void the_database_contains_of(String altcode, String type) throws Throwable {
	    log.info("Check database contains altcode : "+altcode+ ", type: "+ type);
	    
	    
	    String query = "Select * from organisation where alt_code = '" + altcode + "' and cip_bet_type = " + type;

		log.info("run query :"+query);
		Connection connection = BaseDatabaseClass.getConnection();
		Statement st = connection.createStatement();
		ResultSet rs = st.executeQuery(query);

		if (!rs.isBeforeFirst()) {
			// We must have results  - so empty resultset cause fail
			
			Assert.fail("Organisation record for altcode: "+altcode +" and type: " + type + " not found");
		}

	    
	    
	}

	@Then("^the database contains a relation record between \"([^\"]*)\" and \"([^\"]*)\"$")
	public void the_database_contains_a_relation_record_between_and(String parentCode, String childCode) throws Throwable {
	    log.info("Check database relation contains parent: "+parentCode + ", child: "+ childCode);
	    
	    String query = "Select * from organisation_relation where alt_code_parent = '" + parentCode + "' and alt_code_child = '" + childCode +"'";

		log.info("run query :"+query);
		Connection connection = BaseDatabaseClass.getConnection();
		Statement st = connection.createStatement();
		ResultSet rs = st.executeQuery(query);

		if (!rs.isBeforeFirst()) {
			// We must have results  - so empty resultset cause fail
			
			Assert.fail("Organisation_relation record for parent_alt_code: "+parentCode +" and child_alt_Code: " + childCode + " not found");
		}
	    
	}
	
	@Then("^the database contains exactly (\\d+) \"([^\"]*)\" records$")
	public void the_database_contains_exactly_records(int expectedRecordCount, String dbTable) throws Throwable {
		log.info("Check DB contains exactly " + expectedRecordCount + " records for table: " + dbTable);

		String query = "Select count(*) from " + dbTable;

		log.info("run query :"+query);
		Connection connection = BaseDatabaseClass.getConnection();
		Statement st = connection.createStatement();
		ResultSet rs = st.executeQuery(query);

		rs.next();
		int count = rs.getInt(1);
		rs.close();

		log.info("Total records for table "+ dbTable +" on DB: " + count);

		Assert.assertTrue(count == expectedRecordCount, "Expected records in table " + dbTable + " to be " +
				+ expectedRecordCount + ", but was " + count);
	}

}
