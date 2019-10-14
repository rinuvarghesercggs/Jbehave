package com.mes.soa.bpmnJbehave.steps;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
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
    Response resp;
    String processID;
    String processCode="FL03";
    String SQL;
    
    String CAMUNDAIP ="http://192.168.7.147:8080";
	String URL_DEPLYOED_TO_PROCESS_ENGINE_START="/engine-rest/process-definition/key/prepare-batch/start";
	String URL_DEPLYOED_TO_PROCESS_ENGINE_PREPARE="/engine-rest/process-definition/key/prepare-batch";

	
	@BeforeStories
	public void loadTestCase() throws IOException 
	{
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
	@Given("that fulfilment process prepare-batch has been deployed into the process engine")
    public void givenXValue1() 
	{
		resp = RestAssured.get(CAMUNDAIP);
	 }
	
    @When("a user triggers execution of the prepare-batch daily process processurl")
    public void deployprocessengine1() 
    {
    	url = CAMUNDAIP + URL_DEPLYOED_TO_PROCESS_ENGINE_PREPARE;
    	resp = RestAssured.get(url);
     }
    
    @Then("the process should successfully complete $value")
    public void thenXshouldBe1(@Named("value") int value) 
    {
    	int statuscode = resp.getStatusCode();
    	value = statuscode;
    }
    
    
    /*-------------SECOND SCENERIO------------*/
    @Given("that  process batch-start-new has been deployed into the process engine")
    public void givenXValue2() 
	{
		resp=null;
	}
	
    @When("a user triggers execution of the batch-start-new daily process processurl")
    public void deployprocessengine2() 
    {
    	resp=null;
    	url = CAMUNDAIP + URL_DEPLYOED_TO_PROCESS_ENGINE_START;
    	RequestSpecification request = RestAssured.given();
		JSONObject requestParams = new JSONObject();
	    request.header("Content-Type", "application/json");
	    request.body(requestParams.toString());
	    resp=request.post(url);
    	writeFile();
    }
    
    @Then("the process-start-new should successfully complete $value")
    public void thenXshouldBe2(@Named("value") int value) 
    {
    	int statuscode = resp.getStatusCode();
    	value = statuscode;
    }
    
    
    /*-------------THIRD SCENERIO------------*/
    @Given("that  process validate-new has been deployed into the process engine")
    public void givenXValue3() 
	{
		System.out.println("--Validating New Status of a batch--");
	}
	
    @When("a user triggers execution of the validate-new daily process processurl")
    public void deployprocessengine3() 
    {
    
    	
    }
    
    @Then("the validate-new should successfully complete $value")
    public void thenXshouldBe3(@Named("value") int value) 
    {
    	int statuscode = resp.getStatusCode();
    	String pstatus=null;
		String expectedStatus = null;
		try {
			
			
			String batchID=getBatchCode("NEW");
	    	
	    	SQL="SELECT \n" + 
					"	bpc.batch_process_cd,\n" + 
					"	b.batch_id,\n" + 
					"	batch_status_cd as pStatus,\n" + 
					"	status.created_on\n" + 
					"FROM\n" + 
					"	mes.ref_batch_status_code status\n" + 
					"JOIN\n" + 
					"	mes.batch as b on status.batch_sid = b.batch_sid\n" + 
					"JOIN\n" + 
					"	mes.ref_batch_process_code  bpc on b.batch_process_sid=bpc.batch_process_sid   \n" + 
					"LEFT JOIN\n" + 
					"	mes.ref_batch_source_code bsc on bsc.batch_source_sid = b.batch_source_sid\n" + 
					"WHERE\n" + 
					"	bpc.batch_process_cd='"+processCode+"' AND b.batch_id IN("+batchID+")  AND  batch_status_cd ='NEW' "
							+ "order by status.created_on desc ";
			
			expectedStatus="PICKED";
		    Connection con=dataSource.getConnection();
		    PreparedStatement ps=con.prepareStatement(SQL);
		    ResultSet rs=ps.executeQuery();
		    	while(rs.next())
		    	{
		    		if(rs.getString("pStatus").equals(expectedStatus))
		    		{
		    			System.out.println("");
						System.out.println("######## Validation Test Result for Process Code : "+processCode+"--- Batch Code : "+rs.getString("batch_id")+" --- Expected Status After Test : "+expectedStatus+" --- Actual Status : "+rs.getString("pStatus")+" --- Test Status : SUCCESS ########");
						System.out.println("");
		    		}
		    		else
		    		{
		    			System.out.println("");
						System.out.println("######## Validation Test Result for Process Code : "+processCode+"--- Batch Code : "+rs.getString("batch_id")+" --- Expected Status After Test : "+expectedStatus+" --- Actual Status : "+rs.getString("pStatus")+" --- Test Status : FAILED ########");
						System.out.println("");
		    		}
		    	}
		    	
		}catch(Exception e) {e.printStackTrace();}
    	value = statuscode;
    }
    
    /*-------------FOURTH SCENERIO------------*/
    @Given("that  process validate-rerun has been deployed into the process engine")
    public void givenXValue4() 
	{
		resp=null;
	}
	
    @When("a user triggers execution of the validate-rerun daily process processurl")
    public void deployprocessengine4() 
    {
    	resp=null;
    	url = CAMUNDAIP + URL_DEPLYOED_TO_PROCESS_ENGINE_START;
    	RequestSpecification request = RestAssured.given();
		JSONObject requestParams = new JSONObject();
	    request.header("Content-Type", "application/json");
	    request.body(requestParams.toString());
	    resp=request.post(url);
    
	    SQL="select batch_status_cd as pStatus from mes.ref_batch_status_code status\n" + 
				"join mes.batch as b on status.batch_sid = b.batch_sid\n" + 
				"join mes.ref_batch_process_code  bpc on b.batch_process_sid=bpc.batch_process_sid     \n" + 
				"left join mes.ref_batch_source_code bsc on bsc.batch_source_sid = b.batch_source_sid\n" + 
				"where bpc.batch_process_cd='"+processCode+"' order by status.created_on desc LIMIT 1";
    }
    
    @Then("the validate-rerun should successfully complete $value")
    public void thenXshouldBe4(@Named("value") int value) 
    {
    	int statuscode = resp.getStatusCode();
    	String pstatus=null;
		String expectedStatus = null;
		try {
			
			
			String batchID=getBatchCode("RE-RUN");
	    	
	    	SQL="SELECT \n" + 
					"	bpc.batch_process_cd,\n" + 
					"	b.batch_id,\n" + 
					"	batch_status_cd as pStatus,\n" + 
					"	status.created_on\n" + 
					"FROM\n" + 
					"	mes.ref_batch_status_code status\n" + 
					"JOIN\n" + 
					"	mes.batch as b on status.batch_sid = b.batch_sid\n" + 
					"JOIN\n" + 
					"	mes.ref_batch_process_code  bpc on b.batch_process_sid=bpc.batch_process_sid   \n" + 
					"LEFT JOIN\n" + 
					"	mes.ref_batch_source_code bsc on bsc.batch_source_sid = b.batch_source_sid\n" + 
					"WHERE\n" + 
					"	bpc.batch_process_cd='"+processCode+"' AND b.batch_id IN("+batchID+")  AND  batch_status_cd ='RE-RUN' "
							+ "order by status.created_on desc ";
			
			expectedStatus="PICKED";
		    Connection con=dataSource.getConnection();
		    PreparedStatement ps=con.prepareStatement(SQL);
		    ResultSet rs=ps.executeQuery();
		    	while(rs.next())
		    	{
		    		if(rs.getString("pStatus").equals(expectedStatus))
		    		{
		    			System.out.println("");
						System.out.println("######## Validation Test Result for Process Code : "+processCode+"--- Batch Code : "+rs.getString("batch_id")+" --- Expected Status After Test : "+expectedStatus+" --- Actual Status : "+rs.getString("pStatus")+" --- Test Status : SUCCESS ########");
						System.out.println("");
		    		}
		    		else
		    		{
		    			System.out.println("");
						System.out.println("######## Validation Test Result for Process Code : "+processCode+"--- Batch Code : "+rs.getString("batch_id")+" --- Expected Status After Test : "+expectedStatus+" --- Actual Status : "+rs.getString("pStatus")+" --- Test Status : FAILED ########");
						System.out.println("");
		    		}
		    	}
		    	
		}catch(Exception e) {e.printStackTrace();}
		value = statuscode;
    }

    
    private void writeFile()
    {
    	
    
    	List<String> temp=new ArrayList<>();
    	 
    	try {
    		
    		 XSSFWorkbook workbook = new XSSFWorkbook();
		     XSSFSheet sheet = workbook.createSheet("BatchData");
			
			SQL="SELECT \n" + 
					"	bpc.batch_process_cd,\n" + 
					"	b.batch_id,\n" + 
					"	batch_status_cd as pStatus,\n" + 
					"	status.created_on\n" + 
					"FROM\n" + 
					"	mes.ref_batch_status_code status\n" + 
					"JOIN\n" + 
					"	mes.batch as b on status.batch_sid = b.batch_sid\n" + 
					"JOIN\n" + 
					"	mes.ref_batch_process_code  bpc on b.batch_process_sid=bpc.batch_process_sid   \n" + 
					"LEFT JOIN\n" + 
					"	mes.ref_batch_source_code bsc on bsc.batch_source_sid = b.batch_source_sid\n" + 
					"WHERE\n" + 
					"	bpc.batch_process_cd='"+processCode+"' order by status.created_on desc ";
	    	
	    		int i=1;
	    		//Header file

    			Row row = sheet.createRow(0);
    			Cell cell=row.createCell(0);
    			cell.setCellValue("PROCESS_CODE");
    			cell=row.createCell(1);
    			cell.setCellValue("BATCH_ID");
    			cell=row.createCell(2);
    			cell.setCellValue("STATUS");
    			cell=row.createCell(3);
    			cell.setCellValue("CREATED_ON");
    			
   
	    		
	    		Connection con=dataSource.getConnection();
	    		PreparedStatement ps=con.prepareStatement(SQL);
	    		ResultSet rs=ps.executeQuery();
	    		while(rs.next())
	    		{
	    			if(rs.getString("pStatus").equals("PICKED"))
	    			{
	    				temp.add(rs.getString(1));
	    				temp.add(rs.getString(2));
	    				temp.add(rs.getString(3));
	    				temp.add(rs.getString(4));
	    			}
	    			else
	    			{
		    			row = sheet.createRow(i);
		    			cell=row.createCell(0);
		    			cell.setCellValue(rs.getString(1));
		    			cell=row.createCell(1);
		    			cell.setCellValue(rs.getString(2));
		    			cell=row.createCell(2);
		    			cell.setCellValue(rs.getString(3));
		    			cell=row.createCell(3);
		    			cell.setCellValue(rs.getString(4));
		    			i++;
	    			}
	    		}
	    		
	    		if(temp.size()>0)
	    		{	int k=0;
		    		for(int j=0;j<(temp.size()/4);j++)
			          {
		    			row = sheet.createRow(i+j);
		    			for(int jj=0;jj<4;jj++)
		    			{
			    			cell=row.createCell(jj);
			    			cell.setCellValue(temp.get(jj+k).toString());
			    			
		    			}
		    			k=k+4;
			          }
	    		}
	    		
	    		
				File f=new File("");
	    		String path=f.getAbsolutePath()+"/src/test/resources/TestCase.xlsx";
	    		
	    		FileOutputStream out = new FileOutputStream(new File(path));
	    		workbook.write(out);
	            out.close();
	
			
		} 
		catch (Exception e) 
    	{
			if (workbook != null) {
				System.out.println("Closing Excel file..");
			}
			System.out.println("Excel file not found.");
		}
    	finally 
    	{
    		try {
    		workbook.close();
    		}catch(Exception e) {e.printStackTrace();}
    	}
    }
    
    private String getBatchCode(String initialStatus)
    {
    	String batchIds=null;
    	 for (Row row : sheet) 
    	 {
    	        for (Cell cell : row) 
    	        {
    	            if (cell.getCellType() ==CellType.STRING) 
    	            {
    	                if (cell.getRichStringCellValue().getString().trim().equals(initialStatus)) 
    	                {
    	                	if(batchIds!=null)
    	                	{
        	                    batchIds=batchIds+",'"+row.getCell(1, MissingCellPolicy.RETURN_BLANK_AS_NULL).getStringCellValue()+"'";
    	                	}
    	                	else
    	                	{
        	                    batchIds="'"+row.getCell(1, MissingCellPolicy.RETURN_BLANK_AS_NULL).getStringCellValue()+"'";

    	                	}
    	                }
    	            }
    	        }
    	  }
    	return batchIds;
    }
    
    private String[] getTestCase(int rowNum, String processCode,String expectedStatus) 
    {
        String[] response = new String[2];
        Row row = sheet.getRow(rowNum);
         if(row != null) {
             String processCd = row.getCell(0, MissingCellPolicy.RETURN_BLANK_AS_NULL).getStringCellValue();
             if(processCd == null) {
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
