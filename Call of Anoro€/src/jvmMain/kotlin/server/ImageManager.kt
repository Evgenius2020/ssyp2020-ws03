package server

import java.lang.Exception

class ImageManager{
    val base = hashMapOf<Int, Int>()

    var counter = 1

    fun setImage(team: Int){
        if(!base.containsKey(team)){
            base[team] = counter++
            if(counter > 8){
                throw Exception("Too many teams")
            }
        }
    }
}