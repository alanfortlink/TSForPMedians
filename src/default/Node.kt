package default

/**
 * Created by alanfortlink on 5/31/17.
 */
data class Node(val x: Double, val y: Double){

    var index = -1

    constructor(node: Node) : this(node.x, node.y) {
        index = node.index
    }

    fun getDistanceTo(node: default.Node) : Double{
        return Math.floor(Math.sqrt( Math.pow(x - node.x, 2.0) + Math.pow(y - node.y, 2.0)) + 0.5)
    }
}
