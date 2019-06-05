package me.arjunn_.type;

import org.json.JSONObject;

import java.util.UUID;

/**
 *  An object used to represent a player present on a server.
 *  Includes the player's uuid, username, server, and highest rank.
 *
 * @author Arjun
 * @since JDK 11
 * @version 1.0
 *
 */
public class vPlayer {

    private final UUID uuid;
    private final String username;
    private String server;
    private String rank;

    public vPlayer(UUID uuid, String username, String server, String rank) {

        this.uuid = uuid;
        this.username = username;
        this.server = server;
        this.rank = rank;

    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getUsername() {
        return this.username;
    }

    public String getServer() {
        return this.server;
    }

    public String getRank() {
        return this.rank;
    }

    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        result.put("uuid", this.uuid.toString())
                .put("username", this.username)
                .put("rank", this.rank)
                .put("server", this.server);

        return result;
    }

    public static vPlayer fromJSON(JSONObject jsonObject) {

        if (!jsonObject.has("uuid") || !jsonObject.has("username") || !jsonObject.has("rank") || !jsonObject.has("server")) {
            return null;
        }

        return new vPlayer(UUID.fromString(jsonObject.getString("uuid")), jsonObject.getString("username"), jsonObject.getString("rank"), jsonObject.getString("server"));
    }

}
