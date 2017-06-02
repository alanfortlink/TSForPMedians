package default

/**
 * Created by alanfortlink on 5/30/17.
 */
class Evaluator{

    fun getCostOfExchange(solution: Solution, problem: Problem, candIn: Node, candOut: Node) : Int{


        var costIn = 0;
        var costOut = 0;

        for(node in solution.facNodeMap.get(candOut)!!){
            costOut += problem.distances[node.index][candOut.index]

            costIn += minOf( problem.distances[node.index][candIn.index],
                             problem.distances[node.index][solution.nodeToFacility.get(node)!!.index])
        }

//        println("${solution.facilities}")
//        println("testing remove ${candOut.index} and add ${candIn.index}")
//        println("old: ${costOut}, new: ${costIn}")

        return costIn - costOut
    }

    fun evaluate(solution: Solution, problem: Problem) : Int {
        var cost = 0

        for(node in problem.nodes){
            var secondBestFacility = solution.facilities.first()
            var secondBestCost = problem.distances[secondBestFacility.index][node.index]

            var bestFacility = solution.facilities.first()
            var bestCost = problem.distances[bestFacility.index][node.index]

            for(facility in solution.facilities){
                val newCost = problem.distances[facility.index][node.index]
                if(newCost < bestCost){
                    secondBestFacility = Node(bestFacility)
                    secondBestCost = bestCost

                    bestFacility = Node(facility)
                    bestCost = newCost
                } else if(newCost < secondBestCost) {
                    secondBestFacility = Node(facility)
                    secondBestCost = newCost
                }
            }

            cost += bestCost

            if(!solution.facNodeMap.containsKey(bestFacility)){
                solution.facNodeMap.put(bestFacility, mutableSetOf<Node>())
            }

            var bestList = solution.facNodeMap.get(bestFacility)
            if (bestList != null) {
                bestList.add(node)
                solution.facNodeMap.put(Node(bestFacility), bestList)
            }

            solution.nodeToFacility.put(Node(node), Node(secondBestFacility))

        }

        return cost
    }
}
