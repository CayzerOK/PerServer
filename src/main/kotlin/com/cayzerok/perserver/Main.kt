package com.cayzerok.perserver
import com.google.gson.Gson
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.network.util.ioCoroutineDispatcher
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.io.writeStringUtf8
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.util.*
import java.net.InetAddress
import java.util.Enumeration
import java.net.NetworkInterface




data class Cell(
        var ID: Long,
        var lastUnit:UUID,
        var origin:UUID,
        var line:String,
        var isReturning: Boolean)



var gson = Gson()

fun main(args:Array<String>) {
    runBlocking {
        val server = aSocket(ActorSelectorManager(ioCoroutineDispatcher)).tcp().bind(InetSocketAddress(Inet4Address.getLocalHost().hostAddress, 8080))
        println("Started echo telnet server at ${server.localAddress}")
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
                    val reciver = launch { Reciver(socket, input, output, newUnit) }
                    reciver.join()
                } catch (e: Exception) {
                    socket.close()
                }
            }
        }
    }
}