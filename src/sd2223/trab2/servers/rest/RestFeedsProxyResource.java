package sd2223.trab2.servers.rest;

import sd2223.trab2.api.java.Feeds;
import sd2223.trab2.mastodon.Mastodon;

public class RestFeedsProxyResource extends RestFeedsResource<Feeds> {
    public RestFeedsProxyResource() {
        super(Mastodon.getInstance());
    }
}
