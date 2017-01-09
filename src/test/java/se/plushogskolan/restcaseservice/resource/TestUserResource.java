package se.plushogskolan.restcaseservice.resource;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import se.plushogskolan.restcaseservice.Application;
import se.plushogskolan.restcaseservice.config.InmemoryDBConfig;
import se.plushogskolan.restcaseservice.model.DTOUser;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = { InmemoryDBConfig.class})
public class TestUserResource {

	@LocalServerPort
	private int randomPort;
	private static final String auth = "Authorization";
	private static final String authCode = "Adminrights";
	private static Client client;
	private WebTarget userWebTarget;
	private URI userInDb;
	private String baseURL;

	private DTOUser testUser;
	private DTOUser updateUser;

	@BeforeClass
	public static void initialize() {
		client = ClientBuilder.newClient();
	}

	@Before
	public void setup() {
		String targetUrl = String.format("http://localhost:%d/", randomPort);
		baseURL = String.format("http://localhost:%d/users", randomPort);
		String resource = "users";
		testUser = DTOUser.builder().setId(1001L).setFirstName("test").setLastName("testson").setIsActive(true)
				.build("test1234567");
		updateUser = DTOUser.builder().setId(1001L).setFirstName("testing").setLastName("testsoning").setIsActive(true)
				.build("testing1234");
		userWebTarget = client.target(targetUrl).path(resource);
		
	}
	
	@Test
	public void createUserTest() {
		Response response = userWebTarget.request().header(auth, authCode)
				.post(Entity.entity(DTOUser.builder().setId(1L).setFirstName("testing").setLastName("testsoning").setIsActive(true)
						.build("testing123456"), MediaType.APPLICATION_JSON));
		assertEquals(CREATED, response.getStatusInfo());

	}
	
	@Test
	public void updateUserTest() {
		userInDb = userWebTarget.request().header(auth, authCode)
				.post(Entity.entity(testUser, MediaType.APPLICATION_JSON)).getLocation();
		Response response = client.target(userInDb).request().header(auth, authCode)
				.put(Entity.entity(updateUser, MediaType.APPLICATION_JSON));
		assertEquals(NO_CONTENT, response.getStatusInfo());
	}
	
	@Test
	public void getUserById() {
		userInDb = userWebTarget.request().header(auth, authCode)
				.post(Entity.entity(testUser, MediaType.APPLICATION_JSON)).getLocation();
		Response response = client.target(userInDb).request().header(auth, authCode).get();
		assertEquals(OK, response.getStatusInfo());
	}
	
	@Test
	public void getAllUsers(){
		Response response = client.target(baseURL).request().header(auth, authCode).get();
		assertEquals(OK, response.getStatusInfo());
	}
}
