package gameClient;
import api.*;
import api.game_service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gameClient.util.Point3D;
import gameClient.util.Range;
import gameClient.util.Range2D;
import gameClient.util.Range2Range;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;

 /** This class represents a multi Agents Arena which move on a graph - grabs Pokemons and avoid the Zombies.*/
public class Arena {
    public static final double EPS1 = 0.001, EPS2=EPS1*EPS1, EPS=EPS2;
    private directed_weighted_graph _graph;
    private HashMap<Integer,Agent> _agents;     // <id, agent>
    private TreeMap<Pokemon, Double> _pokemons;  // <pokemon, value>
    private game_service game;
    private List<String> _info;
    private static Point3D MIN = new Point3D(0, 100,0);
    private static Point3D MAX = new Point3D(0, 100,0);

    public Arena(game_service game) {
        this.game= game;
        _info = new ArrayList<String>();
    }

    public game_service getGame() {
        return game;
    }

    public TreeMap<Pokemon, Double> getPokemons() {return _pokemons;}
    public void setPokemons(TreeMap<Pokemon, Double> pokemons) {
        this._pokemons = pokemons;
    }

    public HashMap<Integer,Agent> getAgents() {return _agents;}
    public void setAgents(HashMap<Integer, Agent> agents) {
        this._agents = agents;
    }

    public directed_weighted_graph getGraph() {
        return _graph;
    }
    public void setGraph(directed_weighted_graph g) {this._graph =g;}       //init();}

    public List<String> get_info() {
        return _info;
    }



    // reading json string and load:

    /**loading data from the server about all the existed agents, and make collection from it*/
    public static HashMap<Integer, Agent> load_agents(String jsonString, directed_weighted_graph graph ) {
        HashMap<Integer, Agent> agents = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray agents_arr = jsonObject.getJSONArray("Agents");
            for (int i = 0; i < agents_arr.length(); i++) {
                JSONObject pp = agents_arr.getJSONObject(i);
                JSONObject agent_jsonObject = pp.getJSONObject("Agent");
                int id = agent_jsonObject.getInt("id");
                double value = agent_jsonObject.getDouble("value");
                int src = agent_jsonObject.getInt("src");
                int dest = agent_jsonObject.getInt("dest");
                double speed = agent_jsonObject.getDouble("speed");
                String posString = agent_jsonObject.getString("pos");
                Point3D pos = new Point3D(posString);
                if(dest==-1){   //this agent has got to his dest. now the new src is dest
                    for (node_data n : graph.getV()){
                        if (n.getLocation().equals(pos)){
                            src = n.getKey();
                            break;
                        }
                    }
                }
                //create new agent and set all details about him:
                Agent newAgent = new Agent(id, value, src, dest, speed, pos);
                double distance_fromSrc = pos.distance(graph.getNode(src).getLocation());
                newAgent.setDistance_fromSrc(distance_fromSrc);
                agents.put(newAgent.getId(), newAgent);
            }
        } catch (JSONException e) {
            e.printStackTrace();}

        return agents;
    }

    /**    loading data from the server about all the existed agents, and update the fields that was changed in each of the existing agents.*/
     public static void update_agents (String jsonString, directed_weighted_graph graph, HashMap<Integer, Agent> agents){
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray agents_arr = jsonObject.getJSONArray("Agents");
            for (int i = 0; i < agents_arr.length(); i++) {
                JSONObject pp = agents_arr.getJSONObject(i);
                JSONObject agent_jsonObject = pp.getJSONObject("Agent");
                int id = agent_jsonObject.getInt("id");
                double value = agent_jsonObject.getDouble("value");
                int src = agent_jsonObject.getInt("src");
                int dest = agent_jsonObject.getInt("dest");
                double speed = agent_jsonObject.getDouble("speed");
                String posString = agent_jsonObject.getString("pos");
                Point3D pos = new Point3D(posString);
                if(dest==-1){   //this agent has got to his dest. now the new src is dest
                    for (node_data n : graph.getV()){
                        if (n.getLocation().equals(pos)){
                            src = n.getKey();
                            break;
                        }
                    }
                }
                //find the agent and set all details about him:
                Agent temp_agent = agents.get(id);
                temp_agent.setValue(value);
                temp_agent.setSrc_id(src);
                temp_agent.setNextNode(dest);
                temp_agent.setSpeed(speed);
                temp_agent.setPos(pos);
                double distance_fromSrc = pos.distance(graph.getNode(src).getLocation());
                temp_agent.setDistance_fromSrc(distance_fromSrc);
            }
        } catch (JSONException e) {
            e.printStackTrace();}
    }

    /** loading data that was given from the server, and makes pokemon objects from him*/
    public static TreeMap<Pokemon,Double> load_pokemon(String jsonString, directed_weighted_graph graph){
        TreeMap<Pokemon,Double> pokemons = new TreeMap<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray pokemons_arr = jsonObject.getJSONArray("Pokemons");
            int len =pokemons_arr.length();
            for(int i=0; i<len; i++) {
                JSONObject pp = pokemons_arr.getJSONObject(i);
                JSONObject pokemon_jsonObject = pp.getJSONObject("Pokemon");
                int type = pokemon_jsonObject.getInt("type");
                double value = pokemon_jsonObject.getDouble("value");
                String posString = pokemon_jsonObject.getString("pos");
                Point3D pos = new Point3D(posString);
                //create new pokemon and set all details about him:
                Pokemon newPokemon = new Pokemon(value, type, pos );
                pokemons.put(newPokemon, value);
                edge_data e = newPokemon.findEdge(graph);
                newPokemon.setEdge(e);  //new
                newPokemon.setSrc_id(e.getSrc());
                newPokemon.setDest_id(e.getDest());
                double distance_fromSrc = pos.distance(graph.getNode(e.getSrc()).getLocation());
                newPokemon.setDistance_fromSrc(distance_fromSrc);
                //System.out.println("newPokemon= "+newPokemon);
            }
        }
        catch (JSONException e) {e.printStackTrace();}
        return pokemons;
    }

    /** loading data that was given from the server, and make new graph from it */
    public static directed_weighted_graph load_graph (String jsonString){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(directed_weighted_graph.class, new GraphJson());
        Gson gson = builder.create();
        directed_weighted_graph newGraph  = gson.fromJson(jsonString, directed_weighted_graph.class);
        return newGraph;
    }

    /** reading the game details, like number of pokemons and agents in this level*/
    public static ArrayList<Integer> read_gameDetails (String jsonString){
        ArrayList<Integer> ans = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONObject inner_json = json.getJSONObject("GameServer");
            ans.add(inner_json.getInt("pokemons"));
            ans.add(inner_json.getInt("agents"));
            ans.add(inner_json.getInt("moves"));
            ans.add(inner_json.getInt("grade"));
        }
        catch (JSONException e) {e.printStackTrace();}
        return  ans;
    }


    private static Range2D GraphRange(directed_weighted_graph g) {
        Iterator<node_data> itr = g.getV().iterator();
        double x0=0,x1=0,y0=0,y1=0;
        boolean first = true;
        while(itr.hasNext()) {
            geo_location p = itr.next().getLocation();
            if(first) {
                x0=p.x(); x1=x0;
                y0=p.y(); y1=y0;
                first = false;
            }
            else {
                if(p.x()<x0) {x0=p.x();}
                if(p.x()>x1) {x1=p.x();}
                if(p.y()<y0) {y0=p.y();}
                if(p.y()>y1) {y1=p.y();}
            }
        }
        Range xr = new Range(x0,x1);
        Range yr = new Range(y0,y1);
        return new Range2D(xr,yr);
    }
    public static Range2Range w2f(directed_weighted_graph g, Range2D frame) {
        Range2D world = GraphRange(g);
        Range2Range ans = new Range2Range(world, frame);
        return ans;
    }

}
