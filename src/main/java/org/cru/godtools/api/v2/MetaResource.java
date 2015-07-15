package org.cru.godtools.api.v2;

import org.cru.godtools.s3.AmazonS3GodToolsConfig;
import org.jboss.logging.Logger;
import org.xml.sax.SAXException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@Path("v2/meta")
public class MetaResource
{
	private Logger log = Logger.getLogger(this.getClass());

	@GET
	public Response getAllMetaInfo() throws ParserConfigurationException, SAXException, IOException
	{
		log.info("Getting all meta info");

		return Response
				.status(Response.Status.MOVED_PERMANENTLY)
				.header("location", AmazonS3GodToolsConfig.getMetaRedirectUrl())
				.build();

	}

}
