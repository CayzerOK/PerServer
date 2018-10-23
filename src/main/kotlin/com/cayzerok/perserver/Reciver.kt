package com.cayzerok.perserver

import io.ktor.network.sockets.Socket
import kotlinx.coroutines.experimental.io.ByteReadChannel
import kotlinx.coroutines.experimental.io.ByteWriteChannel
import kotlinx.coroutines.experimental.io.readUTF8Line

suspend fun Reciver(socket: Socket, input:ByteReadChannel, thisUnit: Unit) {
    var job = true
    while (job==true) {
        val inputJSON = input.readUTF8Line()
        println(inputJSON)
        when(inputJSON) {
            null -> {
                println("Socket ${socket.remoteAddress} Closed")
                unitList.forEach { println(it) }
                val iterator = unitList.iterator()
                while (iterator.hasNext()) {
                    while (iterator.hasNext()) {
                        val item = iterator.next()
                        if (item == thisUnit) {
                            iterator.remove()
                        }
                    }
                }
                socket.close()
                unitList.forEach { println(it) }
            }
            else -> {
                val newCell: Cell = gson.fromJson(inputJSON, Cell::class.java)
                println("Cell accepted")
                CellRouter(newCell)
            }
        }
    }
}