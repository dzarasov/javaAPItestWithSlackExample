import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import org.apache.commons.io.FileUtils;
import org.hamcrest.text.IsEmptyString;

import io.restassured.path.json.JsonPath;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.path.json.exception.JsonPathException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.minidev.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Test;


public class RestTests {
	public static final String SLACKAPISTRING = "https://slack.com/api/";
	public static final String SLACKMETHOD = "files.upload?";
	public static final String SLACKMETHODELETE = "files.delete?";
	public static final String SLACKTOKEN = "token=xoxp-70479966560-70496740882-71361301984-2a33b11e36&";
	public static final String SLACKFILENAMESTRING = "filename";
	public static String filenametouse = "pom.xml";
	public static String testfile = "pom.xml";
	public static final String SLACKRETURNPRETTYRESPONSE = "&pretty=1";
	public static final String SLACKFILETYPE = "file";
    public static Response response;
    public static String jsonAsString;
		
	
    @Test 
    public void verifyFileName() throws IOException {	
	    given().
	    	multiPart(SLACKFILETYPE, new File(testfile)).
	        	formParam("filename", testfile).
	            	contentType("multipart/form-data").
	    expect().
	   		body(SLACKFILETYPE+".name", equalTo(filenametouse)).
	   			when().
	   				post(SLACKAPISTRING + SLACKMETHOD + SLACKTOKEN + SLACKFILENAMESTRING + "=" + filenametouse + SLACKRETURNPRETTYRESPONSE);
	    }	
	    
    
    
    @Test 
    public void verifyID_CREATED_TIMESTAMP_Exists() throws IOException {    	
	    given().
	    	multiPart(SLACKFILETYPE, new File(testfile)).
	        	formParam("filename", testfile).
	            	contentType("multipart/form-data").
	    expect().
	   		body(SLACKFILETYPE+".id", notNullValue(), 
	   			 SLACKFILETYPE+".created", notNullValue(), 
	   			 SLACKFILETYPE+".timestamp", notNullValue()).
	   		
	   			when().
	   				post(SLACKAPISTRING + SLACKMETHOD + SLACKTOKEN + SLACKFILENAMESTRING + "=" + filenametouse + SLACKRETURNPRETTYRESPONSE);
	    }    
    
        
    @Test 
    public void verifyExtensionType() throws IOException {
	    int index = testfile.indexOf(".");
	    String fileextension = testfile.substring(index+1);
	        	
	    given().
	    	multiPart(SLACKFILETYPE, new File(testfile)).
	        	formParam("filename", testfile).
	            	contentType("multipart/form-data").
	    expect().
	   		body(SLACKFILETYPE+".filetype", equalTo(fileextension)).
	   			when().
	   				post(SLACKAPISTRING + SLACKMETHOD + SLACKTOKEN + SLACKFILENAMESTRING + "=" + filenametouse + SLACKRETURNPRETTYRESPONSE);
	    }
    

    
    @Test 
    public void verifyURL() throws IOException {        	
	    given().
	    	multiPart(SLACKFILETYPE, new File(testfile)).
	        	formParam("filename", testfile).
	            	contentType("multipart/form-data").
	    expect().
	   		body("file.url_private_download", containsString(testfile.toLowerCase())).
	   			when().
	   				post(SLACKAPISTRING + SLACKMETHOD + SLACKTOKEN + SLACKFILENAMESTRING + "=" + filenametouse + SLACKRETURNPRETTYRESPONSE);
	    }    
    	
		
	@Test
	public void deletePostedFile(){
		final Response response = 	given().
	    	multiPart(SLACKFILETYPE, new File(testfile)).
	        	formParam("filename", testfile).
	            	contentType("multipart/form-data").
	            when().
	   				post(SLACKAPISTRING + SLACKMETHOD + SLACKTOKEN + SLACKFILENAMESTRING + "=" + filenametouse + SLACKRETURNPRETTYRESPONSE);
	    
				
		JsonPath jsonPath = new JsonPath(response.asString());
		String idFromResponse = jsonPath.getString("file.id");		
        given().
        formParam("file", idFromResponse).
        		expect().
        			body("ok", equalTo(true)).
        				when().
							delete(	SLACKAPISTRING + SLACKMETHODELETE + SLACKTOKEN + SLACKFILETYPE + "=" + idFromResponse + SLACKRETURNPRETTYRESPONSE);

		
        given().
    	formParam("file", idFromResponse).
    		expect().
    			body("ok", equalTo(false)).
    			body("error", equalTo("file_deleted")).
    				when().
						delete(	SLACKAPISTRING + SLACKMETHODELETE + SLACKTOKEN + SLACKFILETYPE + "=" + idFromResponse + SLACKRETURNPRETTYRESPONSE);
	}
	
	@Test
	public void verifyId(){	
        expect().
        	body("files[0].id", equalTo("F23F7KNQJ")).
        		when().
        			get("https://slack.com/api/files.list?token=xoxp-70479966560-70496740882-71361301984-2a33b11e36&pretty=1");
	}
	
	@Test
	public void verifyName(){	
        expect().
        	body("files[0].name", equalTo("pom.xml")).
        		when().
        			get("https://slack.com/api/files.list?token=xoxp-70479966560-70496740882-71361301984-2a33b11e36&pretty=1");
	}	
	
}



