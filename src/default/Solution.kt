package default

import org.jetbrains.annotations.Mutable

/**
 * Created by alanfortlink on 5/30/17.
 */
class Solution{
    var facilities = mutableListOf<Node>()
    var cost: Int = Int.MAX_VALUE
    var problem = Problem()
    //get the nodes associated to each facility
    var facNodeMap = HashMap<Node, MutableSet<Node>>()

    //map the node to the second closest facility
    var nodeToFacility = HashMap<Node, Node>()

    constructor(problem: Problem) {
        this.problem = problem
    }

    fun getSolutionCost(): Int {
        if(cost == Int.MAX_VALUE){
            if(facilities.size == 0) return Int.MAX_VALUE

            val evaluator = Evaluator()
            this.cost = evaluator.evaluate(this, this.problem)
        }

        return cost
    }
}