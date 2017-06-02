package default

import java.util.*
import kotlin.collections.HashMap

/**
 * Created by alanfortlink on 5/30/17.
 */

fun nextInt(): Int{
    return (Math.random() * 100000000000).toInt();
}

class TS{

    enum class SearchMethod{
        FIRST_IMPROVING, BEST_IMPROVING
    };

    var evaluator = Evaluator()
    var problem = Problem()
    var bestSolution = Solution(Problem())
    var tabu = mutableListOf<Node>()
    var searchMethod = default.TS.SearchMethod.FIRST_IMPROVING
    var tenure = 0

    fun solve(problem: Problem, numOfIterations: Int, timeLimit: Int, tenure: Int, searchMethod: default.TS.SearchMethod, intensifyFrequency: Int) : Solution {
        this.searchMethod = searchMethod
        this.tenure = tenure
        this.problem = problem
        this.bestSolution = getBasicSolution()

        var intensify = 1
        var limit = numOfIterations / intensifyFrequency

        var timeBegin = System.currentTimeMillis()
        var timeSpent:Long = 0

        for (i in 1..numOfIterations) {
            var bestNeighbor = getBestNeighbor(bestSolution);

            intensify = (intensify + 1) % limit

            if (intensify == 0) {
                bestNeighbor = getBestNeighbor(bestNeighbor)
            }

            if (bestNeighbor != null && bestNeighbor.getSolutionCost() < bestSolution.getSolutionCost()) {
                bestSolution = bestNeighbor
            }

            timeSpent = System.currentTimeMillis() - timeBegin

            if (timeSpent > timeLimit) {
                break
            }

        }

        println("${bestSolution.getSolutionCost()} ${timeSpent/1000}")


        return bestSolution
    }

    fun getBasicSolution() : Solution {
        var bestSolution = Solution(this.problem)
        bestSolution.cost = Int.MAX_VALUE

        var facilities = mutableListOf<Node>()

        for(i in 1..this.problem.p){
            var addedNode: Node? = null
            for (node in problem.nodes){
                var solution = Solution(this.problem)
                solution.facilities = facilities.toMutableList()
                solution.facilities.add(node)

                if(solution.getSolutionCost() < bestSolution.getSolutionCost()){
                    bestSolution = solution
                    addedNode = node
                }
            }

            if(addedNode != null){
                facilities.add(addedNode)
            }
        }

        return bestSolution
    }

    fun getBestNeighbor(bestSolution: Solution) : Solution {
        //Create a copy of the best solution I found so far to search its neighborhood
        var bestNeighbor: Solution = Solution(this.problem)
        bestNeighbor.facilities = bestSolution.facilities.toMutableList()
        bestNeighbor.cost = bestSolution.cost
        bestNeighbor.nodeToFacility = HashMap(bestSolution.nodeToFacility)
        bestNeighbor.facNodeMap = HashMap(bestSolution.facNodeMap)

        evaluator.evaluate(bestNeighbor, problem)

        //get the candidates to leave and to ingress
        var candidatesToLeave = bestNeighbor.facilities.toMutableList()
        var candidatesToIngress = problem.nodes.filter { !candidatesToLeave.contains(it) }

        //shuffle them so I won't be biasing the solution
        java.util.Collections.shuffle(candidatesToLeave)
        java.util.Collections.shuffle(candidatesToIngress)

        //get a random pair of leaving and ingressing nodes and give the no improvement (0.0)
        var bestPair:Pair<Node, Node> = Pair(candidatesToLeave[nextInt() % candidatesToLeave.size], candidatesToIngress[nextInt() % candidatesToIngress.size])
        var bestCost = 0

        for(candL in candidatesToLeave){
            for(candI in candidatesToIngress){
                //the aspiration rule is if it improves the solution
                var newCost = evaluator.getCostOfExchange(bestNeighbor, problem, candI, candL)
                if( (!tabu.contains(candL) && !tabu.contains(candI)) || newCost <= bestCost){
                    bestPair = Pair(candL, candI)
                    bestCost = newCost
                }
            }
        }

        //apply the solution with the chosen nodes
        bestNeighbor.facilities.remove(bestPair.first)
        bestNeighbor.facilities.add(bestPair.second)

        //update the tabu
        tabu.add(bestPair.first)
        tabu.add(bestPair.second)

        //limits the tabu size
        if(tabu.size > tenure){
            tabu.removeAt(0)
            tabu.removeAt(0)
        }

        //recalculate the cost o the best neighbor
        bestNeighbor.cost = evaluator.evaluate(bestNeighbor, problem)

        return bestNeighbor
    }
}

fun main(args: Array<String>) {
    var filename = args[0]

    var problem = Problem()
    problem.readFromFile(filename)

    var ts = default.TS()

    var iterations = 100000
    var timeLimit = 5 * 60 * 1000
    var tenureSize = problem.size / args[1].toInt()
    var intensifyFrequency = args[2].toInt()

    var solution = ts.solve(problem, iterations, timeLimit, tenureSize, TS.SearchMethod.BEST_IMPROVING, intensifyFrequency)
}
