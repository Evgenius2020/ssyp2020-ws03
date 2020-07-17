package ru.leadpogrommer.mpg

import com.google.gson.Gson
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

class Client(s: Socket) {
    private val inputChannel = Channel<Request>(Channel.Factory.UNLIMITED)
    private val outputChannel = Channel<Request>(Channel.Factory.UNLIMITED)

    private val inStream = s.openReadChannel()
    private val outStream = s.openWriteChannel()


    private val gson = Gson()

    fun run(){
        GlobalScope.launch(Dispatchers.IO) {
            while (true){
                val req = outputChannel.receive()
                val sered = gson.toJson(req)
                val ba = sered.toString().toByteArray()
                outStream.writeInt(ba.size)
                outStream.writeFully(ba, 0, ba.size)
                outStream.flush()
            }
        }

        GlobalScope.launch(Dispatchers.IO){
            while (true){
                val size = inStream.readInt()
                val ser = ByteArray(size)
                inStream.readFully(ser, 0, size)
                val req: Request = gson.fromJson(ser.toString(charset = Charsets.UTF_8), Request::class.java)
                inputChannel.send(req)
            }
        }
    }


    suspend fun sendRequest(r: Request){
        outputChannel.send(r)
    }

    fun getRequests():ReceiveChannel<Request>{
        return inputChannel
    }


}