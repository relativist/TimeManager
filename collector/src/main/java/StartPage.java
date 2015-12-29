import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Stateless
@Path("/page")
public class StartPage {
    private final static Logger L = LoggerFactory.getLogger(StartPage.class);

    @PersistenceContext
    private EntityManager em;

    public StartPage() {
    }

    @Inject
    TimeTracker timeTracker;

//
//    @GET
//    public Response doOnlyDownload() throws Exception {
//        L.info("connect");
//        return Response.ok("ok").build();
//    }

    @GET
    public Response start() throws Exception {
        L.info("start");
        timeTracker.iterator();
        return Response.ok("done").build();
    }

}

