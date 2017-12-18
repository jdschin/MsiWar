package de.htwg.se.msiwar.model

import de.htwg.se.msiwar.model.ActionType.ActionType

case class Action(id: Int, description: String, imagePath: String, soundPath: String, actionPoints: Int, range: Int, actionType: ActionType) {
}
