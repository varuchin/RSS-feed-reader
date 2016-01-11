import com.mera.varuchin.Refresher;
import com.mera.varuchin.modules.FeedModule;
import com.mera.varuchin.modules.HibernateModule;
import com.mera.varuchin.modules.ItemModule;
import com.mera.varuchin.rss.RssExecutor;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
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
                .packages("com.mera.varuchin").register(new FeedModule())
                .register(new ItemModule())
                .register(new HibernateModule())
                .register(MultiPartFeature.class);

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException, SQLException {
        Locale.setDefault(Locale.ENGLISH);
        final HttpServer server = startServer();

        RssExecutor rssExecutor = new RssExecutor();
        rssExecutor.run(new Refresher(), 0, 20, TimeUnit.MINUTES);

        System.in.read();
        server.stop();
    }
}

