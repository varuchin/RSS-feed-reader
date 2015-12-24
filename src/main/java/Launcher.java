import com.mera.varuchin.Refresher;
import com.mera.varuchin.rss.RssExecutor;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class Launcher {
    private static final String BASE_URI = "http://localhost:8081";

    private static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig()
                .packages("com.mera.varuchin");
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException, SQLException {
        Locale.setDefault(Locale.ENGLISH);
        final HttpServer server = startServer();

        RssExecutor rssExecutor = new RssExecutor();
        rssExecutor.run(new Refresher(), 0, 20, TimeUnit.SECONDS);

        System.in.read();
        server.stop();
    }
}

