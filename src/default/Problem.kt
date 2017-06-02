package default

/**
 * Created by alanfortlink on 5/30/17.
 */
class Problem{

    var size = 0
    var p = 0
    var nodes = mutableListOf<Node>()
    var distances = arrayListOf<ArrayList<Int>>()

    fun readFromFile(filename: String){
        val file = java.io.File(filename)
        val lines = file.readLines()

        this.size = lines[0].split(" ")[0].toInt()
        this.p = lines[0].split(" ")[1].toInt()

        var nodesList = lines.subList(1, lines.size)

        for((index, line) in nodesList.withIndex()){
            if(line.isEmpty()){
                break
            }

            var values = line.split(" ")
            var x = values[0].toDouble()
            var y = values[1].toDouble()

            var node = Node(x, y)
            node.index = index

            this.nodes.add(node)
        }

        calcDistances()
    }

    fun calcDistances(){

        for(i in 0..size-1){
            var distances = arrayListOf<Int>()
            for(j in 0..size-1){
                distances.add(nodes[i].getDistanceTo(nodes[j]).toInt())
            }

            this.distances.add(distances)
        }

    }

}
