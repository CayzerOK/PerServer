package com.cayzerok.perserver

import io.ktor.network.sockets.Socket
import kotlinx.coroutines.experimental.io.ByteReadChannel
import kotlinx.coroutines.experimental.io.ByteWriteChannel
import java.util.*

var unitList:MutableList<Unit> = mutableListOf()

data class Unit(var UUID:UUID, var socket: Socket, var input:ByteReadChannel, var output:ByteWriteChannel, var availability:Boolean)

suspend fun CellRouter(cell: Cell) {
    when {
        cell.isReturning -> {
            val thisUnit = unitList.filter { it.UUID == cell.lastUnit }.single()
            thisUnit.availability = true
            val origin = unitList.filter { it.UUID == cell.origin }.single()
            Responder(origin.output, cell)
        }

        !cell.isReturning -> {
            try {
                val freeUnit = unitList.filter { it.availability == true && it.UUID != cell.origin}.first()
                freeUnit.availability=false
                Responder(freeUnit.output, cell)
            } catch (exc:NoSuchElementException) {
                cell.isReturning = true
                cell.line = "Availabile units not found"
                val returnUnit = unitList.filter { it.UUID == cell.origin}.single()
                Responder(returnUnit.output, cell)
            }

        }
    }
}