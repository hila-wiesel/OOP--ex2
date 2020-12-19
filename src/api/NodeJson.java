package api;

import com.google.gson.*;

import java.lang.reflect.Type;

public class NodeJson implements JsonDeserializer<node_data>, JsonSerializer<node_data> {

    @Override
    public node_data deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        int id = jsonObject.get("key").getAsInt();
        String pos = jsonObject.get("pos").getAsString();
        String [] posArr = pos.split(",");
        double x = Double.parseDouble(posArr[0]);
        double y = Double.parseDouble(posArr[1]);
        double z = Double.parseDouble(posArr[2]);
        geo_location g = new GeoLocation(x,y,z);
        node_data newNode = new NodeData(id, "", 0, g, 0.0);

        return newNode;
    }

    @Override
    public JsonElement serialize(node_data node, Type type, JsonSerializationContext jsonSerializationContext) {

        JsonObject json = new JsonObject();
        json.addProperty("id", node.getKey());
        json.addProperty("pos",node.getLocation().toString());

        return json;
    }
}
