package de.htwg.se.msiwar.util

import de.htwg.se.msiwar.model.GameObject

trait GameConfigProvider {
  def attackSoundPath: String
  def openingBackgroundImagePath: String
  def levelBackgroundImagePath: String
  def actionbarBackgroundImagePath: String
  def attackImagePath: String
  def appIconImagePath: String

  def gameObjects: List[GameObject]

  def listScenarios: List[String]
  def loadFromFile(configFilePath: String): GameConfigProvider
  def generateGame(rowCount: Int, columnCount: Int, completion: (Boolean) => Unit) : GameConfigProvider

  def rowCount: Int
  def colCount: Int
}
