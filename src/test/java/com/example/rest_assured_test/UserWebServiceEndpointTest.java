package com.example.rest_assured_test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.response.*;
import static  io.restassured.RestAssured.given;

class UserWebServiceEndpointTest {
	
	private final String CONTEXT_PATH = "/mobile-app-ws";

	private final String EMAIL_ADDRESS = "olojedechristopher24@gmail.com";

	private final String JSON = "application/json"; 
	
	@BeforeEach
	void setUp() throws Exception {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 8080;
	}

	@Test
	void testUserLogin() {
		Map<String, String> loginDetails = new HashMap<>();
		loginDetails.put("email", EMAIL_ADDRESS);
		loginDetails.put("password", "olamide");
		
		Response response = given()
		.contentType(JSON)
		.accept(JSON)
		.body(loginDetails)
		.when()
		.post(CONTEXT_PATH + "/users/login")
		.then()
		.statusCode(200)
		.extract()
		.response();
		
		String authorizationHeader = response.header("Authorization");
		String userId = response.header("userId");
	
		assertNotNull(authorizationHeader);
		assertNotNull(userId);
	}

}