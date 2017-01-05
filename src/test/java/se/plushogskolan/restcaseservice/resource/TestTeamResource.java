package se.plushogskolan.restcaseservice.resource;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;

import se.plushogskolan.restcaseservice.Application;
import se.plushogskolan.restcaseservice.config.InmemoryDBConfig;
import se.plushogskolan.restcaseservice.model.DTOTeam;
import se.plushogskolan.restcaseservice.service.TeamService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = { InmemoryDBConfig.class, TeamService.class })
public class TestTeamResource {

	@Autowired
	private TeamService service;

	@LocalServerPort
	private int randomPort;
	private static final String auth = "Authorization";
	private static final String authCode = "Adminrights";
	private static Client client;
	private WebTarget teamWebTarget;
	private URI teamInDb;
	private String baseURL;

	private DTOTeam testTeam;

	@BeforeClass
	public static void initialize() {
		client = ClientBuilder.newClient();
	}

	@Before
	public void setup() {
		String targetUrl = String.format("http://localhost:%d/", randomPort);
		baseURL = String.format("http://localhost:%d/teams", randomPort);
		String resource = "teams";
		teamWebTarget = client.target(targetUrl).path(resource);
		testTeam = new DTOTeam(1001L, "testteam", true);
		teamInDb = teamWebTarget.request().header(auth, authCode)
				.post(Entity.entity(testTeam, MediaType.APPLICATION_JSON)).getLocation();
	}

	@Test
	public void createTeamTest() {
		Response response = teamWebTarget.request().header(auth, authCode)
				.post(Entity.entity(new DTOTeam(1L, "Team1", true), MediaType.APPLICATION_JSON));
		assertEquals(CREATED, response.getStatusInfo());

	}

	@Test
	public void updateTeamTest() {
		Response response = client.target(teamInDb).request().header(auth, authCode)
				.put(Entity.entity(new DTOTeam(service.getTeam(1L).getId(), "updatedTeam", false), MediaType.APPLICATION_JSON));
		assertEquals(NO_CONTENT, response.getStatusInfo());
	}

	@Test
	public void getTeamById() {
		Response response = client.target(teamInDb).request().header(auth, authCode).get();
		assertEquals(OK, response.getStatusInfo());
	}
	
	@Test
	public void getAllTeams(){
		Response response = client.target(baseURL).request().header(auth, authCode).get();
		assertEquals(OK, response.getStatusInfo());
	}
}
