package sd2223.trab2.mastodon.msgs;

public record MastodonAccount(String id, String username) {

    public long getId() {
        return Long.valueOf(id);
    }

    public String getUsername() {
        return username;
    }
}
