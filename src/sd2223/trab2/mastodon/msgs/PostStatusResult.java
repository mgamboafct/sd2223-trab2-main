package sd2223.trab2.mastodon.msgs;

import sd2223.trab2.api.Message;
import sd2223.trab2.servers.Domain;

import java.time.OffsetDateTime;

public record PostStatusResult(String id, String content, String created_at, MastodonAccount account) {
	
	public long getId() {
		return Long.valueOf(id);
	}
	
	public long getCreationTime() {
		OffsetDateTime dateTime = OffsetDateTime.parse(created_at);

		return dateTime.toInstant().toEpochMilli();
	}
	
	public String getText() {
		return content.replaceAll("<p>|</p>", "");
	}
	
	public Message toMessage() {
		var m = new Message( getId(), account.getUsername(), Domain.get(), getText());
		m.setCreationTime( getCreationTime() );
		return m;
	}
}