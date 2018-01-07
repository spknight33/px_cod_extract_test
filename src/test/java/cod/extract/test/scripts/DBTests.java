package cod.extract.test.scripts;


import cod.extract.test.BaseDatabaseClass;
import cucumber.api.CucumberOptions;


@CucumberOptions(features= {"src//test//java//features//DBTest.feature"},
glue= {"stepdefinition"}
,plugin= {"pretty","html:target/cucumber"})
public class DBTests extends BaseDatabaseClass{
	
		

}
