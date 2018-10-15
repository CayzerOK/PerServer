import io.ktor.network.sockets.Socket
import kotlinx.coroutines.experimental.io.ByteReadChannel
import kotlinx.coroutines.experimental.io.readUTF8Line

suspend fun Reciver(socket: Socket, input:ByteReadChannel, thisUnit:Unit) {
    while (true) {
        val inputJSON = input.readUTF8Line()
        val newCell: Cell = gson.fromJson(inputJSON, Cell::class.java)
        if (newCell.line == "/stop") {
            println(unitList)
            val iterator = unitList.iterator()
            while (iterator.hasNext()) {
                while (iterator.hasNext()) {
                    val item = iterator.next()
                    if (item == thisUnit) {
                        iterator.remove()
                    }
                }
            }
            println(unitList)
        } else {
            println("Cell accepted")
            CellRouter(newCell)
        }
    }
}