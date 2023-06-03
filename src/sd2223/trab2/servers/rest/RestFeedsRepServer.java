package sd2223.trab2.servers.rest;

import org.glassfish.jersey.server.ResourceConfig;
import sd2223.trab2.api.java.Feeds;
import sd2223.trab2.kafka.zookeeper.Zookeeper;
import sd2223.trab2.servers.Domain;
import utils.Args;

import java.util.logging.Logger;

public class RestFeedsRepServer extends AbstractRestServer {
    public static final int PORT = 6789;

    private static Logger Log = Logger.getLogger(RestFeedsRepServer.class.getName());

    RestFeedsRepServer( ) {
        super( Log, Feeds.SERVICENAME, PORT);
        try {
            Zookeeper zookeeper = new Zookeeper("kafka:2181");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize ZooKeeper client.", e);
        }
    }

    @Override
    void registerResources(ResourceConfig config) {
        config.register( new RestFeedsRepResource() );
    }

    public static void main(String[] args) throws Exception {
        Args.use( args );
        Domain.set( args[0], Long.valueOf(args[1]));
        new RestFeedsRepServer().start();
    }
}
