data class Statistic(val teamMembers: HashMap<Int, Array<String>>,
                     val teamScore: HashMap<Int, Int>,
                     val nickToKills: HashMap<String, Int>,
                     val nickToDeaths: HashMap<String, Int>)