package com.cayzerok.perserver

import kotlinx.coroutines.experimental.io.ByteWriteChannel
import kotlinx.coroutines.experimental.io.writeStringUtf8

suspend fun Responder(output:ByteWriteChannel, cell: Cell) {
    val outLine = gson.toJson(cell)
    output.writeStringUtf8("$outLine\r\n")
}