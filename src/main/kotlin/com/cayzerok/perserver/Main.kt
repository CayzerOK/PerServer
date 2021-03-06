package com.cayzerok.perserver
import com.google.gson.Gson
import io.ktor.application.Application
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.network.util.ioCoroutineDispatcher
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.io.writeStringUtf8
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.util.*




data class Cell(
        var ID: Long,
        var lastUnit:UUID,
        var origin:UUID,
        var line:String,
        var isReturning: Boolean)



var gson = Gson()

fun Application.main() {
    runBlocking {
        val server = aSocket(ActorSelectorManager(ioCoroutineDispatcher)).tcp().bind(InetSocketAddress("0.0.0.0", 8080 ))
        println("Server started at: ${server.localAddress}")
        while (true) {
            val socket = server.accept()
            val input = socket.openReadChannel()
            val output = socket.openWriteChannel(true)
            val newUnit = Unit(UUID.randomUUID(), socket, input, output, true)
            unitList.add(newUnit)
            unitList.forEach { println(it) }
            output.writeStringUtf8(gson.toJson(newUnit.UUID) + "\r\n")
            launch {
                println("Socket accepted: ${socket.remoteAddress}")
                try {
                    val reciver = launch { Reciver(socket, input, newUnit) }
                    reciver.join()
                } finally {
                    val iterator = unitList.iterator()
                    while (iterator.hasNext()) {
                        while (iterator.hasNext()) {
                            val item = iterator.next()
                            if (item == newUnit) {
                                iterator.remove()
                            }
                        }
                    }
                    println("Unit removed")
                    socket.close()
                }
            }
        }
    }
}