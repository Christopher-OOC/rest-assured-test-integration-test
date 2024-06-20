package com.example.rest_assured_test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.restassured.RestAssured;
import io.restassured.response.*;
import static io.restassured.RestAssured.given;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserWebServiceEndpointTest {

	private final String CONTEXT_PATH = "/mobile-app-ws";

	private final String EMAIL_ADDRESS = "olojedechristopher@gmail.com";

	private final String JSON = "application/json";

	private static String authorizationHeader;

	private static String userId;

	@BeforeEach
	void setUp() throws Exception {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 8080;
	}

	@Order(value = 1)
	@Test
	void testUserLogin() {
		Map<String, String> loginDetails = new HashMap<>();
		loginDetails.put("email", EMAIL_ADDRESS);
		loginDetails.put("password", "olamide");

		Response response = given().contentType(JSON).accept(JSON).body(loginDetails).when()
				.post(CONTEXT_PATH + "/users/login").then().statusCode(200).extract().response();

		authorizationHeader = response.header("Authorization");
		userId = response.header("userId");

		assertNotNull(authorizationHeader);
		assertNotNull(userId);

		System.out.println("First");
	}

	@Order(value = 2)
	@Test
	void testGetUser() {

		Response response = given().pathParam("id", userId).header("Authorization", authorizationHeader).accept(JSON)
				.contentType(JSON).when().get(CONTEXT_PATH + "/users/{id}").then().statusCode(200).contentType(JSON)
				.extract().response();

		String userPublicId = response.jsonPath().getString("userId");
		String userEmail = response.jsonPath().getString("email");
		String firstName = response.jsonPath().getString("firstName");
		String lastName = response.jsonPath().getString("lastName");
		List<Map<String, String>> addresses = response.jsonPath().getList("addresses");
		String addressId = addresses.get(0).get("addressId");

		assertNotNull(userPublicId);
		assertNotNull(userEmail);
		assertNotNull(firstName);
		assertNotNull(lastName);
		assertEquals(EMAIL_ADDRESS, userEmail);
		assertTrue(addresses.size() == 2);
		assertTrue(addressId.length() == 30);

		System.out.println("Second");
	}

	@Order(value=3)
	@Test
	void testUpdateUser() {

		Map<String, String> userDetails = new HashMap<>();
		userDetails.put("firstName", "Olojede");
		userDetails.put("lastName", "olamide");

		Response response = given().accept(JSON).contentType(JSON).header("Authorization", authorizationHeader)
				.pathParam("id", userId).body(userDetails).when().put(CONTEXT_PATH + "/users/{id}").then()
				.statusCode(200).contentType(JSON).extract().response();

		String firstName = response.jsonPath().getString("firstName");
		String lastName = response.jsonPath().getString("lastName");
		
		List<Map<String, String>> storedAddresses = response.jsonPath().getList("addresses");
		
		assertEquals("Olojede", firstName);
		assertEquals("olamide", lastName);
		assertNotNull(storedAddresses);
	}
	
	@Order(value=4)
	@Test
	void testDeleteUser() {
		Response response = given().accept(JSON).header("Authorization", authorizationHeader)
				.pathParam("id", userId)
				.when()
				.delete(CONTEXT_PATH + "/users/{id}")
				.then()
				.statusCode(200)
				.contentType(JSON)
				.extract()
				.response();
				
		String operationResult = response.jsonPath().getString("operationResult");
		assertEquals("SUCCESS", operationResult);
	}

}
