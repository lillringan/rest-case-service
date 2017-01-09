package se.plushogskolan.restcaseservice.resource;

import static javax.ws.rs.core.Response.Status.CREATED;
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
import se.plushogskolan.restcaseservice.model.DTOWorkItem;
import se.plushogskolan.casemanagement.model.WorkItem.Status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = { InmemoryDBConfig.class})
public class TestWorkItemResource {

	@LocalServerPort
	private int randomPort;
	private static final String auth = "Authorization";
	private static final String authCode = "Adminrights";
	private static Client client;
	private WebTarget wiWebTarget;
	private URI wiInDb;
	private String baseURL;

	private DTOWorkItem testwi;

	@BeforeClass
	public static void initialize() {
		client = ClientBuilder.newClient();
	}

	@Before
	public void setup() {
		String targetUrl = String.format("http://localhost:%d/", randomPort);
		baseURL = String.format("http://localhost:%d/workitems", randomPort);
		String resource = "workitems";
		wiWebTarget = client.target(targetUrl).path(resource);
		testwi = new DTOWorkItem(1001L, "testwi", Status.DONE);
		wiInDb = wiWebTarget.request().header(auth, authCode)
				.post(Entity.entity(testwi, MediaType.APPLICATION_JSON)).getLocation();
	}
	
	@Test
	public void createWorkItemTest() {
		Response response = wiWebTarget.request().header(auth, authCode)
				.post(Entity.entity(new DTOWorkItem(1L, "WorkItem1", Status.DONE), MediaType.APPLICATION_JSON));
		assertEquals(CREATED, response.getStatusInfo());
	}
	
	@Test
	public void getTeamById() {
		Response response = client.target(wiInDb).request().header(auth, authCode).get();
		assertEquals(OK, response.getStatusInfo());
	}
	
	@Test
	public void getAllTeams(){
		Response response = client.target(baseURL).request().header(auth, authCode).get();
		assertEquals(OK, response.getStatusInfo());
	}
}
