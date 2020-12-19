package api;

import com.google.gson.*;

import java.lang.reflect.Type;

public class EdgeJson implements  JsonSerializer<edge_data> {

    @Override
    public JsonElement serialize(edge_data edge, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject json = new JsonObject();
        json.addProperty("src",edge.getSrc());
        json.addProperty("w", edge.getWeight());
        json.addProperty("dest", edge.getDest());
        return json;
    }
}
