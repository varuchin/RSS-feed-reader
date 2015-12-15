import com.mera.varuchin.dao.RssItemDAOImpl;
import com.mera.varuchin.rss.RssItem;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;


public class Run {
    public static final String BASE_URI = "http://localhost:8081";
    //public static final String driver = "oracle.jdbc.driver.OracleDriver";


    public static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig()
                .packages("com.mera.varuchin");

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }


    public static void main(String[] args) throws IOException, SQLException {
        Locale.setDefault(Locale.ENGLISH);
        final HttpServer server = startServer();
        // installDriver();
        RssItem rssItem = new RssItem("asf", "a", "a", new Date(12, 04, 12),
                new URL("http://x-stream.github.io/"));
        RssItem rssItem1 = new RssItem("a!", "a  a a a aa aa aa aa aawrrg d d d d d tfrhdfgsr sedse a",
                "a safasf asdfas a .", new Date(12, 04, 12),
                new URL("http://x-stream.github.io/"));
        RssItem rssItem2 = new RssItem("asf", "af", "afs", new Date(12, 04, 12),
                new URL("http://x-stream.github.io/"));

        Collection<RssItem> list = new ArrayList<>();
        ArrayList<RssItem> xml = new ArrayList<>();
        list.add(rssItem);
        list.add(rssItem1);
        list.add(rssItem2);


        RssItemDAOImpl rssItemDAO = new RssItemDAOImpl();
        rssItemDAO.add(new URL("http://rtfm.merann.ru"), "Name");



        // XStream xStream = new XStream();
        //System.out.println(XmlService.buildXML(list));
//        System.out.println(list);
//        for(int i = 0; i<list.size(); i++){
//           String item = xStream.toXML(list.get(i));
//
//        }
//
//        System.out.println(xml);

        System.in.read();
        server.stop();
    }
}

