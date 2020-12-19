package gameClient;

import Server.Game_Server_Ex2;
import api.*;

import javax.swing.*;
import java.util.*;
import java.util.List;

public class Ex2 implements Runnable{
	private static MyFrame _win;
	private static Arena _ar;
	private ArrayList<Integer> gameDetails;
	private int numOf_agents;
	private int numOf_pokemons;


	public void set_ar(Arena _ar) {
		this._ar = _ar;
	}

	public static void main(String[] a) {
		Thread client = new Thread(new Ex2());
		client.start();


	}
	
	@Override
	public void run() {
		String idNum= JOptionPane.showInputDialog("Please enter id: ");
		String level= JOptionPane.showInputDialog("Please enter level: ");
		int level_number = Integer.parseInt(level);
		game_service game = Game_Server_Ex2.getServer(level_number);
		int id = Integer.parseInt(idNum);
		game.login(id);
		init(game);

		// add agents:
		int j=0;
		for (Pokemon pokemon : _ar.getPokemons().keySet()){
			if(j >= numOf_agents) { break; }
			int src = pokemon.getSrc_id();
			game.addAgent(src);
			//System.out.println("add agent in "+src);        //debug
			++j;
		}
		for(; j<numOf_agents; ++j){    // numOf_agents > numOf_pokemons
			int randomNum = 0 + (int)(Math.random() * (_ar.getGraph().nodeSize()-1));
			Iterator<node_data> iter = _ar.getGraph().getV().iterator();
			int i=0;
			node_data startNode=null;
			while(i<randomNum && iter.hasNext()){
				startNode = iter.next();
				++i;
			}
			game.addAgent(startNode.getKey());
		}
		HashMap<Integer, Agent> agents = Arena.load_agents(game.getAgents(), _ar.getGraph());
		_ar.setAgents(agents);

		// start the game:
		game.startGame();
		while (game.isRunning()) {
			_win.repaint();
			int timeToSleep = (int)(1000*moveAgants(game));
			if(timeToSleep<10)
				timeToSleep=10;
			if(9<timeToSleep && timeToSleep<50)
				timeToSleep=45;
			if(49<timeToSleep && timeToSleep<115)
				timeToSleep=90;
			if (timeToSleep>114)
				timeToSleep=115;
			try{
				Thread.sleep(timeToSleep);
			}
			catch(Exception e) {e.printStackTrace();
			}

		}


		gameDetails = Arena.read_gameDetails(game.toString());
		System.out.println("grade: " + gameDetails.get(3) + " , moves: " + gameDetails.get(2));
		System.exit(0);
	}


	/**
	 * the chosen strategy:
	 * Moves each of the agents along the edge.
	 * if the agent is already on a pokemon src node - he will set to eat him.
	 * if the agent is already on a pokemon dest node - he will set to eat him.
	 * Other wise, for each agent  find the closest "profitable" pokemon , the  cheapest one-  that (time to reach him) / (his value) is the lowest
	 */
	private double moveAgants(game_service game) {
		game.move();

		Arena.update_agents(game.getAgents(), _ar.getGraph(), _ar.getAgents());
		HashMap<Integer, Agent> agents =  _ar.getAgents();
		TreeMap<Pokemon, Double> pokemons = Arena.load_pokemon(game.getPokemons(), _ar.getGraph());
		_ar.setPokemons(pokemons);
		directed_weighted_graph graph = _ar.getGraph();
		dw_graph_algorithms algo = new DWGraph_Algo();
		algo.init(graph);

		double min_shortestDis=Double.MAX_VALUE;
		for (Agent agent : agents.values()){
			Pokemon chosenPokemon = profitable_pokemon(agent, pokemons, algo);
			double dist = algo.shortestPathDist(agent.getSrc_id(), chosenPokemon.getSrc_id()) - agent.getDistance_fromSrc() + chosenPokemon.getDistance_fromSrc();
			dist =dist/agent.getSpeed();
			if(dist<min_shortestDis) {
				min_shortestDis = dist;
			}
			List<node_data> path = algo.shortestPath(agent.getSrc_id(), chosenPokemon.getSrc_id());
			//printPath(path);
			int NextNode;
			if (path.size() == 1){	//the chosen agent is on the pokemone src node
				NextNode = chosenPokemon.getDest_id();
			}
			else {
				NextNode = path.get(1).getKey();
			}
			game.chooseNextEdge(agent.getId(), NextNode);
			agent.setNextNode(NextNode);
		}
		if (min_shortestDis==Double.MAX_VALUE){
			min_shortestDis=1;
		}
		return min_shortestDis;

	}

	/** helper function - find the most profitable pokemon for the given agent
	 * by check each of the pokemos */
	public static Pokemon profitable_pokemon (Agent agent, TreeMap<Pokemon,Double> pokemos, dw_graph_algorithms algo){
		double dist=0;
		double minCoast = Double.MAX_VALUE;
		Pokemon chosenPokemon =null;
		for (Pokemon pokemon : pokemos.keySet()) {
			if (pokemon.getAgentOnTheWay() == true){
				continue;
			}
			if (agent.getSrc_id()==pokemon.getDest_id()){
				chosenPokemon = pokemon;
				break;
			}
			if (agent.getSrc_id()==pokemon.getSrc_id()){
				chosenPokemon = pokemon;
				break;
			}
			dist = algo.shortestPathDist(agent.getSrc_id(), pokemon.getSrc_id());
			dist = dist - agent.getDistance_fromSrc() + pokemon.getDistance_fromSrc();
			dist = dist / agent.getSpeed();
/*			if (dist%1 > 0.001){	//the agent is too fast, so he will skip on the chosen pokemon
				continue;
			}*/
			if (dist/pokemon.getValue() < minCoast){
				minCoast = dist/pokemon.getValue();
				chosenPokemon = pokemon;
			}
		}
		chosenPokemon.setAgentOnTheWay(true);
		return chosenPokemon;
	}


	/**
	 * another strategy:
	 * Moves each of the agents along the edge,
	 * for each pokemon (start with the one who has the highest value) find the closest "free" (on a node) agent,
	 * and set for him the next destionaion (next node) on his path to this pokemon.
	 */
	private double moveAgants2(game_service game) {
		Arena.update_agents(game.getAgents(), _ar.getGraph(), _ar.getAgents());
		HashMap<Integer, Agent> agents =  _ar.getAgents();
		TreeMap<Pokemon, Double> pokemons = Arena.load_pokemon(game.getPokemons(), _ar.getGraph());
		_ar.setPokemons(pokemons);
		directed_weighted_graph graph = _ar.getGraph();
		dw_graph_algorithms algo = new DWGraph_Algo();
		algo.init(graph);

		double min_shortestDis=Double.MAX_VALUE;
		int k=0;

		for (Pokemon pokemon : pokemons.keySet()) {
			if(k >= numOf_agents) {
				if(min_shortestDis==Double.MAX_VALUE){
					return 0.5;
				}
				break;
			}
			Agent closest_agent = find_closest_agent(pokemon, algo, agents);
			if (closest_agent.getId() == 0 && closest_agent.getSrc_id()==16 && closest_agent.getNextNode()==-1){
				System.out.println("aa");
			}
			if(closest_agent == null) {
				if(min_shortestDis==Double.MAX_VALUE){
					return 0.5;
				}
				break;
			}
			double dist = algo.shortestPathDist(closest_agent.getSrc_id(), pokemon.getSrc_id()) - closest_agent.getDistance_fromSrc() + pokemon.getDistance_fromSrc();
			dist =dist/closest_agent.getSpeed();
			if(dist<min_shortestDis) {
				min_shortestDis = dist;
			}
			if (closest_agent.getNextNode()==pokemon.getSrc_id() ||
					(closest_agent.getSrc_id() == pokemon.getSrc_id() && closest_agent.getNextNode()==pokemon.getDest_id())){		//he is already on the way to the pokemon
				continue;
			}
			int id_agent = closest_agent.getId();
			List<node_data> path = algo.shortestPath(agents.get(id_agent).getSrc_id(), pokemon.getSrc_id());   //change dest to src in pokemone
			printPath(path);
			int NextNode;
			if (path.size() == 1){	//the chosen agent is on the pokemone src node
				NextNode = pokemon.getDest_id();
			}
			else {
				NextNode = path.get(1).getKey();
			}
			game.chooseNextEdge(id_agent, NextNode);
			closest_agent.setNextNode(NextNode);
			++k;

			//debug
			System.out.println("move agent: " + id_agent +", speed: "+closest_agent.getSpeed()+", val: " + closest_agent.getValue()+ ", from " + agents.get(id_agent).getSrc_id() + " to " + NextNode);
		}
/*		while (k<numOf_agents){
			/// do nothing?
			k++;
		}*/
		return min_shortestDis;
	}

	public static Agent find_closest_agent ( Pokemon pokemon, dw_graph_algorithms algo, HashMap<Integer, Agent> agents){
		double shortestDis = Double.MAX_VALUE;
		double temp_dist;
		Agent closed_agent = null;
		for (int i=0; i< agents.size(); ++i){
			Agent agent = agents.get(i);
			//if( agent.getNextNode() != -1) { continue; }     //this agent not on node...
			temp_dist = algo.shortestPathDist(agent.getSrc_id(), pokemon.getSrc_id());
			temp_dist = temp_dist - agent.getDistance_fromSrc() + pokemon.getDistance_fromSrc();
			temp_dist = temp_dist/agent.getSpeed();
			//temp_dist =  temp_dist - agent.getDistance_fromSrc() + pokemon.getDistance_fromDest();
			// agent i on src.. and we dont need the dist from pok to dest couse its the same fro all agent, and now we finf the wat to *src* so, its nor right also..
			if (temp_dist<shortestDis){
				shortestDis=temp_dist;
				closed_agent=agent;
			}
		}
		//System.out.println("\nfind_closest_agent, " +closed_agent );
		return closed_agent;
	}


	/**Load from the server all the details game-
	 * the graph that this level run on.
	 * num Of pokemons and their locations
	 * num Of agents
	 Setting frame details for Visualization of tha game.
	 */
	private void init(game_service game) {
		directed_weighted_graph graph = Arena.load_graph(game.getGraph());
		TreeMap<Pokemon, Double> pokemons = Arena.load_pokemon(game.getPokemons(), graph);
		gameDetails = Arena.read_gameDetails(game.toString());
		numOf_pokemons = gameDetails.get(0);
		numOf_agents=  gameDetails.get(1);

		_ar = new Arena(game);
		_ar.setGraph(graph);
		_ar.setPokemons(pokemons);
		this.set_ar(_ar);

		_win = new MyFrame("Pokemon Game  -  (c) Hila Wiesel", _ar);
		_win.setSize(1000, 700);
		_win.update(_ar);
		_win.show();
	}


	//helper function for *debug*
	private void printPath(List<node_data> path) {
		if (path == null) {
			System.out.println("Path doesnt exist\n ");
			return;
		}
		System.out.println("Path between " + path.get(0).getKey()+ " to " + path.get(path.size()-1).getKey() + ": ");
		for (int i = 0; i < path.size() - 1; ++i) {
			System.out.print(path.get(i).getKey() + ", ");
		}
		System.out.print(path.get(path.size()-1).getKey()+"\n");
	}
}
