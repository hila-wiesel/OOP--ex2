package api;

import com.google.gson.*;

import java.util.Map.Entry;
import java.lang.reflect.Type;


public class GraphJson implements JsonDeserializer<directed_weighted_graph>, JsonSerializer<directed_weighted_graph> {

    @Override
    public directed_weighted_graph deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        directed_weighted_graph newGraph = new DWGraph_DS();
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonArray nodesArr = jsonObject.get("Nodes").getAsJsonArray();
        JsonArray edgesArr = jsonObject.get("Edges").getAsJsonArray();

        //creat nodes:
        for (JsonElement node_jesonElement : nodesArr ) {
            JsonObject node_jsonObject = node_jesonElement.getAsJsonObject();
            int key = node_jsonObject.get("id").getAsInt();
            String pos = node_jsonObject.get("pos").getAsString();
            String [] posArr = pos.split(",");
            double x = Double.parseDouble(posArr[0]);
            double y = Double.parseDouble(posArr[1]);
            double z = Double.parseDouble(posArr[2]);
            node_data temp = new NodeData(key, "", 0, new GeoLocation(x,y,z),0.0);
            newGraph.addNode(temp);
        }
        //creat edges:
        for (JsonElement edge_jesonElement : edgesArr ) {
            JsonObject edge_jsonObject = edge_jesonElement.getAsJsonObject();
            int src = edge_jsonObject.get("src").getAsInt();
            int dest = edge_jsonObject.get("dest").getAsInt();
            double weigh= edge_jsonObject.get("w").getAsDouble();
            newGraph.connect(src, dest, weigh);
            }
        return  newGraph;
    }


    @Override
    public JsonElement serialize(directed_weighted_graph graph, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject json = new JsonObject();
        JsonArray nodesArr = new JsonArray(graph.nodeSize());
        JsonArray edgesArr = new JsonArray(graph.edgeSize());
        NodeJson nodeJson = new NodeJson();
        EdgeJson edgeJson = new EdgeJson();

        for (node_data node : graph.getV()){
            nodesArr.add(nodeJson.serialize(node, type, jsonSerializationContext));
            for (edge_data edge : graph.getE(node.getKey())){
                    edgesArr.add(edgeJson.serialize(edge, type, jsonSerializationContext));
            }
        }
        json.add("Edges", edgesArr);
        json.add("Nodes", nodesArr);
        return json;
    }
}
