import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.List;

@Stateless
@Path("/page")
public class RestPage {
    private final static Logger L = LoggerFactory.getLogger(RestPage.class);

    @PersistenceContext
    private EntityManager em;

    public RestPage() {
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
    @Path("/start")
    public Response start() throws Exception {
        L.info("start");
        timeTracker.iterator();
        return Response.ok("done").build();
    }

    @GET
    @Path("/result")
    @SuppressWarnings("unchecked")
    public Response result() throws Exception {
        L.info("start");
        List<Object[]> result = (List<Object[]>) em.createNativeQuery("SELECT count(u.name) AS days,u.name,avg(hours)/60 AS avg,sum(hours)/60.0 - count(u.name)*8.0  AS overworked   FROM attendance a\n" +
                "  JOIN time_user u ON u.id = a.user_id\n" +
                "GROUP BY u.name\n" +
                "ORDER BY overworked DESC ").getResultList();
        StringBuilder builder = new StringBuilder();
        builder.append("<html>");
        builder.append("<body>");
        getResultString(result, builder);
        builder.append("</html>");
        builder.append("</body>");
        return Response.ok(builder.toString()).build();
    }

    @GET
    @Path("/resultMonth")
    @SuppressWarnings("unchecked")
    public Response resultMonth(@QueryParam("month") String month) throws Exception {
        L.info("start");
        List<Object[]> result = (List<Object[]>) em.createNativeQuery("select count(u.name) as days,u.name,avg(hours)/60 as avg,sum(hours)/60.0 - count(u.name)*8.0  as overworked   from attendance a\n" +
                "  join time_user u on u.id = a.user_id\n" +
                "  WHERE to_timestamp(a.timestamp/ 1000)> timestamp '"+month+"'\n" + //'2015-12-01'
                "GROUP BY u.name\n" +
                "ORDER BY overworked DESC ").getResultList();
        StringBuilder builder = new StringBuilder();
        builder.append("<html>");
        builder.append("<body>");
        getResultString(result, builder);
        builder.append("</html>");
        builder.append("</body>");
        return Response.ok(builder.toString()).build();
    }

    private void getResultString(List<Object[]> result, StringBuilder builder) {
        for (Object[] item : result) {
            builder.append(item[0]);
            builder.append("&nbsp");
            builder.append(item[1]);
            builder.append("&nbsp");
            builder.append(item[2]);
            builder.append("&nbsp");
            builder.append(item[3]);
            builder.append("&nbsp");
            builder.append("</br>");
        }
    }

}

