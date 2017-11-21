package de.htwg.se.msiwar.model

case class GameObject(name: String, imagePath: String, position: Position, actionPoints: Int, healthPoints: Int, skills: List[Action]) {

}
