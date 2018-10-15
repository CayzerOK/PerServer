
import com.google.gson.Gson
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.network.util.ioCoroutineDispatcher
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.io.writeStringUtf8
import java.io.IOException
import java.net.InetSocketAddress
import java.util.*


data class Cell(
        var ID: Long,
        var lastUnit:UUID,
        var origin:UUID,
        var line:String,
        var isReturning: Boolean)



var gson = Gson()



fun main(args: Array<String>) = runBlocking {
    val server = aSocket(ActorSelectorManager(ioCoroutineDispatcher)).tcp().bind(InetSocketAddress("127.0.0.1", 2323))
    println("Started echo telnet server at ${server.localAddress}")
    while (true) {
        val socket = server.accept()
        try {
            val input = socket.openReadChannel()
            val output = socket.openWriteChannel(true)
            val uuid = UUID.randomUUID()
            val thisUnit = Unit(uuid, socket, input, output, true)
            unitList.add(thisUnit)
            output.writeStringUtf8(gson.toJson(uuid) + "\r\n")
            println("Socket accepted: ${socket.remoteAddress}")
            val reciver = launch { Reciver(socket, input, thisUnit) }
            reciver.join()
        } catch (e: IOException) {
            socket.close()
        }
    }
}