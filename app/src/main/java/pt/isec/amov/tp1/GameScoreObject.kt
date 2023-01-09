package pt.isec.amov.tp1

class GameScoreObject(
    var username: String,
    var score: Int,
    var gameTime: Int,
    var id: String = ""
) {
    constructor() : this("", 0, 0, "")
}
