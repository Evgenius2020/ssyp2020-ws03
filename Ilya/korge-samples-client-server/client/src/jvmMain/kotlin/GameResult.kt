enum class GameResult(val text: String, var audio: String) {
    WIN("You Won!", "win.wav"),
    LOOSE("You Loose", "loose.wav")
}