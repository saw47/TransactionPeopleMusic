import kotlin.random.*

    val likes: Int = Random.nextInt(0..7000000)
    var peoples: String? = null

    fun peopleCounter() {
        if (likes == 0) {
            peoples = "человек"
        } else if (likes != 11 && (likes - 1) % 10 == 0) {
            peoples = "человеку"
        } else {
            peoples = "людям"
        }
        print("Понравилось $likes $peoples\n")
        println("_________________________________")
    }






