package sd2223.trab2.servers.rest;

import org.glassfish.jersey.server.ResourceConfig;
import sd2223.trab2.api.java.Feeds;
import sd2223.trab2.servers.Domain;
import utils.Args;

import java.util.logging.Logger;

public class RestFeedsProxyServer extends AbstractRestServer {
    public static final int PORT = 5678;

    private static Logger Log = Logger.getLogger(RestFeedsProxyServer.class.getName());

    RestFeedsProxyServer() {
        super( Log, Feeds.SERVICENAME, PORT);
    }

    @Override
    void registerResources(ResourceConfig config) {
        config.register( RestFeedsProxyResource.class );
    }

    public static void main(String[] args) throws Exception {
        Args.use( args );
        Domain.set( args[0], Long.valueOf(args[1]));
        new RestFeedsProxyServer().start();
    }
}
