import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
//import kotlinx.serialization.json.*
import kotlinx.coroutines.launch
//import kotlinx.serialization.*
//import kotlinx.serialization.builtins.LongArraySerializer
//import kotlinx.serialization.builtins.MapSerializer
//import kotlinx.serialization.builtins.PairSerializer
//import kotlinx.serialization.builtins.serializer
////import kotlinx.serialization.cbor.Cbor
//import kotlinx.serialization.internal.LongSerializer
//import kotlinx.serialization.modules.*

//import org.json.*

import com.google.gson.Gson
import com.soywiz.korlibs.samples.clientserver.Action
import io.ktor.utils.io.readInt
import java.nio.charset.Charset

class Client(s: Socket) {
    private val inputChannel = Channel<Request>(Channel.Factory.UNLIMITED)
    private val outputChannel = Channel<Request>(Channel.Factory.UNLIMITED)

    private val inStream = s.openReadChannel()
    private val outStream = s.openWriteChannel()


//    val json = Json(JsonConfiguration.Stable)


    val gson = Gson()

    fun run(){
        GlobalScope.launch(Dispatchers.IO) {
            while (true){
                val req = outputChannel.receive()
                val sered = gson.toJson(req)
//                println(sered.toString())
                val ba = sered.toString().toByteArray()
//                println("Sent ${ba.size} byres: ${ba.toString(charset = Charsets.UTF_8)}")
                outStream.writeInt(ba.size)
                outStream.writeFully(ba, 0, ba.size)
                outStream.flush()
//                if(req.a == Action.MOVE){
//                    println("sent move")
//                }
////                outStream.writeFully(ser, 0, ser.size)
            }
        }

        GlobalScope.launch(Dispatchers.IO){
            while (true){
                val size = inStream.readInt()
//                println(size)
                val ser = ByteArray(size)
                inStream.readFully(ser, 0, size)
//                println("Received $size bytes: ${ser.toString(charset = Charsets.UTF_8)}")
                val req: Request = gson.fromJson(ser.toString(charset = Charsets.UTF_8), Request::class.java)

//                if( req.a == Action.MOVE){
//                    println("Receive move")
//                }
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