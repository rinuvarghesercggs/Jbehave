package com.mes.soa.bpmnJbehave.steps;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jbehave.core.annotations.AfterStories;
import org.jbehave.core.annotations.BeforeStories;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.mes.soa.bpmn.IntegrationTest;

@Component
public class StepsTest {

    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private DataSource dataSource;
    
    static XSSFWorkbook workbook = null;
	static XSSFSheet sheet = null;

    private IntegrationTest testSession;
    private int result;
    String url,url2,url3;
    Response resp,resp2,resp3;
    String processID;
    
    
    String CAMUNDAIP ="";
	String URL_DEPLYOED_TO_PROCESS_ENGINE="/engine-rest/process-definition/key/netsuite-bpm";
	String URL_TRIGGER_EXECUTION_OF_NETSUITE_DAILY_PROCESS="/engine-rest/process-definition/invoice:1:d29bbf3e-a580-11e9-ab27-a20e0492d91b";
	String URL_CHECK_HISTORY_OF_PROCESS_INSTANCE="/engine-rest/history/process-instance/03d5096f-a731-11e9-ab27-a20e0492d91b";
	
	String SQL;
	
	@BeforeStories
	public void loadTestCase() throws IOException {
		try {
			System.out.println("Reading Excel file..");
			workbook = new XSSFWorkbook(getClass().getResourceAsStream("/TestCase.xlsx"));
			System.out.println("Reading First Sheet..");
			sheet = workbook.getSheetAt(0);
		} 
		catch (Exception e) {
			if (workbook != null) {
				System.out.println("Closing Excel file..");
				workbook.close();
			}
			System.out.println("Excel file not found.");
		}
	}
	
	@AfterStories
	public void closeTestCase() {
		try {
			if (workbook != null) {
				System.out.println("Closing Excel file..");
				workbook.close();
			}
		} 
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	/*-------------FIRST SCENERIO------------*/
	//URL_DEPLYOED_TO_PROCESS_ENGINE
	@Given("that fulfilment process prepare-batch has been deployed into the process engine $value")
    public void givenXValue1(String url) {
		CAMUNDAIP=url;
		resp = RestAssured.get(CAMUNDAIP);
		//System.out.println(CAMUNDAIP);
	 }
	
    @When("a user triggers execution of the prepare-batch daily process processurl $processurl")
    public void deployprocessengine1(String processurl ) {
    	processurl = URL_DEPLYOED_TO_PROCESS_ENGINE;
    	//System.out.println(URL_DEPLYOED_TO_PROCESS_ENGINE);
    	url = CAMUNDAIP + URL_DEPLYOED_TO_PROCESS_ENGINE;
    	resp = RestAssured.get(url);
     }
    
    @Then("the process should successfully complete $value")
    public void thenXshouldBe1(@Named("value") int value) {
    	//System.out.println(url);
    	
    	int statuscode = resp.getStatusCode();
    	value = statuscode;
    }
    
    
    /*-------------SECOND SCENERIO------------*/
    //Connect start process
	@Given("that  process batch-start-new has been deployed into the process engine $value")
    public void givenXValue2(String url) {
		CAMUNDAIP=url;
		resp = RestAssured.get(CAMUNDAIP);
		//System.out.println(CAMUNDAIP);
	 }
	
    @When("a user triggers execution of the batch-start-new daily process processurl $processurl")
    public void deployprocessengine2(String processurl ) {
    	processurl = URL_DEPLYOED_TO_PROCESS_ENGINE;
    	//System.out.println(URL_DEPLYOED_TO_PROCESS_ENGINE);
    	url = CAMUNDAIP + URL_DEPLYOED_TO_PROCESS_ENGINE;
    	resp = RestAssured.get(url);
     }
    
    @Then("the process-start-new should successfully complete $value")
    public void thenXshouldBe2(@Named("value") int value) {
    	//System.out.println(url);
    	int statuscode = resp.getStatusCode();
    	value = statuscode;
    }
    
    /*-------------THIRD SCENERIO------------*/
    //Connect start process
	@Given("that  process validate-new has been deployed into the process engine $value")
    public void givenXValue3(String url) {
		CAMUNDAIP=url;
		resp = RestAssured.get(CAMUNDAIP);
		//System.out.println(CAMUNDAIP);
	 }
	
    @When("a user triggers execution of the validate-new daily process processurl $processurl")
    public void deployprocessengine3(String processurl ) {
    	processurl = URL_DEPLYOED_TO_PROCESS_ENGINE;
    	//System.out.println(URL_DEPLYOED_TO_PROCESS_ENGINE);
    	url = CAMUNDAIP + URL_DEPLYOED_TO_PROCESS_ENGINE;
    	resp = RestAssured.get(url);
     }
    
    @Then("the validate-new should successfully complete $value")
    public void thenXshouldBe3(@Named("value") int value) {
    	//System.out.println(url);
    	String pstatus=null;
    	String processCODE = null;
    	String expectedStatus = null;
    	
    	String[] response = getTestCase(1,processCODE,expectedStatus);
    	processCODE = response[0];
        //expectedStatus = response[1];
    	expectedStatus = "PICKED";

    	int statuscode = resp.getStatusCode();
    	String str=resp.asString();
    	JSONObject obj=new JSONObject();
    	JSONParser parser= new JSONParser();
    	try {
    		obj=(JSONObject) parser.parse(str);
    	}catch(Exception e) {e.printStackTrace();}
    	//processID=(String)obj.get("deploymentId");
    	
    	
    	SQL="select batch_status_cd as pStatus from mes.ref_batch_status_code status\n" + 
				"join mes.batch as b on status.batch_sid = b.batch_sid\n" + 
				"join mes.ref_batch_process_code  bpc on b.batch_process_sid=bpc.batch_process_sid     \n" + 
				"left join mes.ref_batch_source_code bsc on bsc.batch_source_sid = b.batch_source_sid\n" + 
				"where bpc.batch_process_cd='"+processCODE+"' order by status.created_on desc LIMIT 1";
    	try {
    		Connection con=dataSource.getConnection();
    		PreparedStatement ps=con.prepareStatement(SQL);
    		ResultSet rs=ps.executeQuery();
    		if(rs.next())
    		{
    			pstatus=rs.getString("pStatus");
    		}
    		
    		if(pstatus.equals(expectedStatus))
			{
				System.out.println("########---Process Code : "+processCODE+"--- Expected Status After Test : "+expectedStatus+" --- Actual Status : "+pstatus+" --- Test Status : SUCCESS ---########");
			}
			else
			{
				System.out.println("########---Process Code : "+processCODE+"--- Expected Status After Test : "+expectedStatus+" --- Actual Status : "+pstatus+" --- Test Status : FAILED --- ########");
			}
    	
    	}catch(Exception e) {e.printStackTrace();}
    	
    	value = statuscode;
    	Assert.assertEquals(resp.getStatusCode(),statuscode);
    }
    
    

    /*-------------FOURTH SCENERIO------------*/
    //Connect start process
	@Given("that  process validate-rerun has been deployed into the process engine $value")
    public void givenXValue4(String url) {
		CAMUNDAIP=url;
		resp = RestAssured.get(CAMUNDAIP);
		//System.out.println(CAMUNDAIP);
	 }
	
    @When("a user triggers execution of the validate-rerun daily process processurl $processurl")
    public void deployprocessengine4(String processurl ) {
    	processurl = URL_DEPLYOED_TO_PROCESS_ENGINE;
    	//System.out.println(URL_DEPLYOED_TO_PROCESS_ENGINE);
    	url = CAMUNDAIP + URL_DEPLYOED_TO_PROCESS_ENGINE;
    	resp = RestAssured.get(url);
     }
    
    @Then("the validate-rerun should successfully complete $value")
    public void thenXshouldBe4(@Named("value") int value) {
    	//System.out.println(url);
    	
    	String pstatus = null;
    	String processCODE = null;
    	String expectedStatus = null;
    	
    	String[] response = getTestCase(2,processCODE,expectedStatus);
    	processCODE = response[0];
        //expectedStatus = response[1];
    	expectedStatus = "PICKED";
    	
    	int statuscode = resp.getStatusCode();
    	String str=resp.asString();
    	JSONObject obj=new JSONObject();
    	JSONParser parser= new JSONParser();
    	try {
    		obj=(JSONObject) parser.parse(str);
    	}catch(Exception e) {e.printStackTrace();}
    	//processID=(String)obj.get("deploymentId");
    	
    	
    	SQL="select batch_status_cd as pStatus from mes.ref_batch_status_code status\n" + 
				"join mes.batch as b on status.batch_sid = b.batch_sid\n" + 
				"join mes.ref_batch_process_code  bpc on b.batch_process_sid=bpc.batch_process_sid     \n" + 
				"left join mes.ref_batch_source_code bsc on bsc.batch_source_sid = b.batch_source_sid\n" + 
				"where bpc.batch_process_cd='"+processCODE+"' order by status.created_on desc LIMIT 1";
    	try {
    		Connection con=dataSource.getConnection();
    		PreparedStatement ps=con.prepareStatement(SQL);
    		ResultSet rs=ps.executeQuery();
    		if(rs.next())
    		{
    			pstatus=rs.getString("pStatus");
    		}
    		
    		if(pstatus.equals(expectedStatus))
			{
				System.out.println("########---Process Code : "+processCODE+"--- Expected Status After Test : "+expectedStatus+" --- Actual Status : "+pstatus+" --- Test Status : SUCCESS ---########");
			}
			else
			{
				System.out.println("########---Process Code : "+processCODE+"--- Expected Status After Test : "+expectedStatus+" --- Actual Status : "+pstatus+" --- Test Status : FAILED --- ########");
			}
    	
    	}catch(Exception e) {e.printStackTrace();}
    	
    	value = statuscode;
    	Assert.assertEquals(resp.getStatusCode(),statuscode);
    }
    
    
    /*-------------FIFTH SCENERIO------------*/
    //Connect start process
	@Given("that  process validate-picked has been deployed into the process engine $value")
    public void givenXValue5(String url) {
		CAMUNDAIP=url;
		resp = RestAssured.get(CAMUNDAIP);
		//System.out.println(CAMUNDAIP);
	 }
	
    @When("a user triggers execution of the validate-picked daily process processurl $processurl")
    public void deployprocessengine5(String processurl ) {
    	processurl = URL_DEPLYOED_TO_PROCESS_ENGINE;
    	//System.out.println(URL_DEPLYOED_TO_PROCESS_ENGINE);
    	url = CAMUNDAIP + URL_DEPLYOED_TO_PROCESS_ENGINE;
    	resp = RestAssured.get(url);
     }
    
    @Then("the validate-picked should successfully complete $value")
    public void thenXshouldBe5(@Named("value") int value) {
    	//System.out.println(url);
    	
    	String pstatus = null;
    	String processCODE = null;
    	String expectedStatus = null;
    	
    	String[] response = getTestCase(3,processCODE,expectedStatus);
    	processCODE = response[0];
       // expectedStatus = response[1];
    	expectedStatus = "PICKED";

    	
    	int statuscode = resp.getStatusCode();
    	String str=resp.asString();
    	JSONObject obj=new JSONObject();
    	JSONParser parser= new JSONParser();
    	try {
    		obj=(JSONObject) parser.parse(str);
    	}catch(Exception e) {e.printStackTrace();}
    	//processID=(String)obj.get("deploymentId");
    	
    	
    	SQL="select batch_status_cd as pStatus from mes.ref_batch_status_code status\n" + 
				"join mes.batch as b on status.batch_sid = b.batch_sid\n" + 
				"join mes.ref_batch_process_code  bpc on b.batch_process_sid=bpc.batch_process_sid     \n" + 
				"left join mes.ref_batch_source_code bsc on bsc.batch_source_sid = b.batch_source_sid\n" + 
				"where bpc.batch_process_cd='"+processCODE+"' order by status.created_on desc LIMIT 1";
    	try {
    		Connection con=dataSource.getConnection();
    		PreparedStatement ps=con.prepareStatement(SQL);
    		ResultSet rs=ps.executeQuery();
    		if(rs.next())
    		{
    			pstatus=rs.getString("pStatus");
    		}
    		
    		if(pstatus.equals(expectedStatus))
			{
				System.out.println("########---Process Code : "+processCODE+"--- Expected Status After Test : "+expectedStatus+" --- Actual Status : "+pstatus+" --- Test Status : SUCCESS ---########");
			}
			else
			{
				System.out.println("########---Process Code : "+processCODE+"--- Expected Status After Test: "+expectedStatus+" --- Actual Status : "+pstatus+" --- Test Status : FAILED --- ########");
			}
    	
    	}catch(Exception e) {e.printStackTrace();}
    	
    	value = statuscode;
    	Assert.assertEquals(resp.getStatusCode(),statuscode);
    }
    
    /*-------------SIXTH SCENERIO------------*/
    //Connect start process
	@Given("that  process instance-picked has been deployed into the process engine $value")
    public void givenXValue6(String url) {
		CAMUNDAIP=url;
		resp = RestAssured.get(CAMUNDAIP);
		//System.out.println(CAMUNDAIP);
	 }
	
    @When("a user triggers execution of the instance-picked daily process processurl $processurl")
    public void deployprocessengine6(String processurl ) {
    	processurl = URL_DEPLYOED_TO_PROCESS_ENGINE;
    	//System.out.println(URL_DEPLYOED_TO_PROCESS_ENGINE);
    	url = CAMUNDAIP + URL_DEPLYOED_TO_PROCESS_ENGINE;
    	resp = RestAssured.get(url);
     }
    
    @Then("the instance-picked should successfully complete $value")
    public void thenXshouldBe6(@Named("value") int value) {
    	//System.out.println(url);
    	
    	String pstatus=null;
    	String expectedStatus="PICKED";
    	String processCODE="1450";
    	
    	String[] response = getTestCase(4,processCODE,expectedStatus);
    	processCODE = response[0];
        expectedStatus = response[1];
    	
    	int statuscode = resp.getStatusCode();
    	String str=resp.asString();
    	JSONObject obj=new JSONObject();
    	JSONParser parser= new JSONParser();
    	try {
    		obj=(JSONObject) parser.parse(str);
    	}catch(Exception e) {e.printStackTrace();}
    	//processID=(String)obj.get("deploymentId");

    	SQL="select count(*) from mes.ref_batch_execution rbe\n" + 
    			"join mes.batch as b on rbe.batch_sid = b.batch_sid\n" + 
    			"join mes.ref_batch_process_code  bpc on b.batch_process_sid=bpc.batch_process_sid     \n" + 
    			"where bpc.batch_process_cd='"+processCODE+"'";
    	
    	try {
    		int k=0;
    		Connection con=dataSource.getConnection();
    		PreparedStatement ps=con.prepareStatement(SQL);
    		ResultSet rs=ps.executeQuery();
    		if(rs.next())
    		{
    			k=rs.getInt(1);
    		}
    		
    		if(k>0)
			{
				System.out.println(" --- Test Status : SUCCESS ---########");
			}
			else
			{
				System.out.println(" --- Test Status : FAILED --- ########");
			}
    	
    	}catch(Exception e) {e.printStackTrace();}
    	
    	value = statuscode;
    	Assert.assertEquals(resp.getStatusCode(),statuscode);
    }
    
    
     //-------------SEVENTH SCENERIO------------
     
    //Connect process status change
	@Given("that  process batch-status has been deployed into the process engine $value")
    public void givenXValue7(String url) {
		CAMUNDAIP=url+"/"+processID;
		resp = RestAssured.get(CAMUNDAIP);
		//System.out.println(CAMUNDAIP);
	 }
	
    @When("a user triggers execution of the batch-status daily process processurl $processurl")
    public void deployprocessengine7(String processurl ) {
    	processurl = URL_DEPLYOED_TO_PROCESS_ENGINE;
    	//System.out.println(URL_DEPLYOED_TO_PROCESS_ENGINE);
    	url = CAMUNDAIP + URL_DEPLYOED_TO_PROCESS_ENGINE;
    	resp = RestAssured.get(url);
     }
    
    @Then("the batch-status should successfully complete $value")
    public void thenXshouldBe7(@Named("value") int value) {
    	//System.out.println(url);
    	
    	int statuscode = resp.getStatusCode();
    	value = statuscode;
    }
    
    private String[] getTestCase(int rowNum, String processCode,String expectedStatus) {
        String[] response = new String[2];
        Row row = sheet.getRow(rowNum);
         if(row != null) {
             int processCd = (int)row.getCell(0, MissingCellPolicy.RETURN_BLANK_AS_NULL).getNumericCellValue();
             if(processCd == 0) {
                 System.out.println("Invalid 'Process Code'");
                 return null;
             }
             response[0] = processCd+"";
             expectedStatus = row.getCell(1, MissingCellPolicy.RETURN_BLANK_AS_NULL).getStringCellValue();
             if(expectedStatus == null || expectedStatus.isEmpty()) {
                 System.out.println("Invalid 'Excepcted Status'");
                 return null;
             }
             response[1] = expectedStatus;
             //System.out.println("Excepcted Status = " + expectedStatus);
         }
         else {
             System.out.println("Test case not found in row "+rowNum);
             return null;
         }
         return response;
    }
}
