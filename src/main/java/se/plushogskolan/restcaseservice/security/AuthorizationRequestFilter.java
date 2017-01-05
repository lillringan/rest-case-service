package se.plushogskolan.restcaseservice.security;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;

public class AuthorizationRequestFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		String authentication = requestContext.getHeaderString("Authorization");

		if (!authentication.equals("Adminrights")) {
			requestContext.abortWith
						(Response.status
						(Response.Status.UNAUTHORIZED).entity("Unauthorized request")
						.build());
		}

	}

}
