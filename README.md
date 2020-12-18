# Pokemon game on directed weighted graph
### Object oriented programming - ex2
<img src= "images/p1.jpg" width=750 hight=100>



#### This project is a development of game strategy in pokemon game.

## Game rules:
In this game, there is changeable numbur of pokemos, and changeable numbur of agents. The goal of the agents is to eat as much as possible pokemons, and by that to raise the grade of the current level.
Each pokemon has value. Eating "fat" pokemon (with hight value) raise the grade more significant.
Each agent has speed, that raise by eating pokemon, acording to the eaten pokemon's value.

## Strategy:
During development two strategies were examined:

*  Go over the agemts - for each agent  find the closest "profitable" pokemon , the  cheapest one-  that (time to reach him) / (his value) is the lowes.
   But - if the agent is already on a pokemon dest node - he will set to eat him first.
         Otherwise, if the agent is already on a pokemon src node - he will set to eat him.
       
*  Go over the pokemons, start with the one who has the highest value, - for each pokemon find the closest "free" (on a node) agent, and send him towards this pokemon.

##### --> After lots of testing for each of the strategy, the chosen strategy is strategy is the first one.

## Practical:
For play the game, I creat few classes:
* [Arena](https://github.com/hila-wiesel/OOP--ex2/wiki/Arena)
* [Agent](https://github.com/hila-wiesel/OOP--ex2/wiki/Agent)
* [Pokemon](https://github.com/hila-wiesel/OOP--ex2/wiki/Pokemon)
* [Ex2_Client](https://github.com/hila-wiesel/OOP--ex2/wiki/Ex2_Client)


## Game arena structure:
The game running on directed weighted graph.
For this I implemented 3 interfaces:
* noda_data - implemented by class [NodeData](https://github.com/hila-wiesel/OOP--ex2/wiki/Node-Data)
* directed_weighted_graph - implemented by class [DWGraph_DS](https://github.com/hila-wiesel/OOP--ex2/wiki/DW%D6%B9Graph_DS)
* dw_graph_algorithms - implemented by class [DWGraph_Algo](https://github.com/hila-wiesel/OOP--ex2/wiki/DWGraph_Algo)








(c) Hila Wiesel





