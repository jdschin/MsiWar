package de.htwg.se.msiwar.util

import java.io.FileNotFoundException

import akka.actor.{ActorSystem, Props}
import de.htwg.se.msiwar.model.ActionType._
import de.htwg.se.msiwar.model._

import scala.io.Source
import scala.util.parsing.json.JSON

case class JSONException(private val message: String = "JSON parsing failed") extends Exception(message)

case class GameConfigProviderImpl(gameObjects: List[GameObject], attackSoundPath: String, openingBackgroundImagePath: String,
                             levelBackgroundImagePath: String, actionbarBackgroundImagePath: String, attackImagePath: String,
                             appIconImagePath: String, rowCount: Int, colCount : Int) extends GameConfigProvider {

  def listScenarios: List[String] = {
    FileLoader.loadFilesFromDirPath("/scenarios").sorted
  }

  @throws(classOf[FileNotFoundException])
  @throws(classOf[JSONException])
  @throws(classOf[NoSuchElementException])
  def loadFromFile(configFilePath: String): GameConfigProvider = {
    var newAttackSoundPath = "sounds/explosion.wav"
    var newLevelBackgroundImagePath = "images/background_woodlands.png"
    var newActionbarBackgroundImagePath = "images/background_actionbar.png"
    var newAttackImagePath = "images/hit.png"
    var newRowCount = 9
    var newColCount = 9
    var newGameObjects: List[GameObject] = List[GameObject]()
    var newActions: List[Action] = List[Action]()

    val json = Source.fromInputStream(getClass.getClassLoader.getResourceAsStream("scenarios/" + configFilePath)).getLines.mkString
    JSON.parseFull(json) match {
      case Some(jsonMap: Map[String, Any]) =>
        jsonMap("levelBackgroundImagePath") match {
          case s: String => newLevelBackgroundImagePath = s
          case _ => throw JSONException()
        }

        jsonMap("actionbarBackgroundImagePath") match {
          case s: String => newActionbarBackgroundImagePath = s
          case _ => throw JSONException()
        }

        jsonMap("attackImagePath") match {
          case s: String => newAttackImagePath = s
          case _ => throw JSONException()
        }

        jsonMap("attackSoundPath") match {
          case s: String => newAttackSoundPath = s
          case _ => throw JSONException()
        }

        jsonMap("rowCount") match {
          case i: Double => newRowCount = i.toInt
          case _ => throw JSONException()
        }

        jsonMap("colCount") match {
          case i: Double => newColCount = i.toInt
          case _ => throw JSONException()
        }

        jsonMap("blockObjects") match {
          case listOfMaps: List[Map[String, Any]] =>
            val blockObjects: List[GameObject] = listOfMaps.map(blockObjectFromMap)
            newGameObjects = newGameObjects ::: blockObjects
          case _ => throw JSONException()
        }

        jsonMap("actions") match {
          case listOfMaps: List[Map[String, Any]] =>
            val actionsFromJson: List[Action] = listOfMaps.map(actionFromMap)
            newActions = newActions ::: actionsFromJson
          case _ => throw JSONException()
        }
        jsonMap("playerObjects") match {
          case listOfMaps: List[Map[String, Any]] =>
            val playersFromJson: List[PlayerObject] = listOfMaps.map(playerObjectFromMap(_, newActions))
            newGameObjects = newGameObjects ::: playersFromJson
          case _ => throw JSONException()
        }

      case _ => throw JSONException()
    }
    copy(newGameObjects, newAttackSoundPath, "images/background_opening.png",
      newLevelBackgroundImagePath, newActionbarBackgroundImagePath, newAttackImagePath,
      "images/app_icon.png", newRowCount, newColCount)
  }

  private def actionFromMap(actionMap: Map[String, Any]): Action = {
    val id = actionMap("id").asInstanceOf[Double].toInt
    val description = actionMap("description").asInstanceOf[String]
    val imagePath = actionMap("imagePath").asInstanceOf[String]
    val soundPath = actionMap("soundPath").asInstanceOf[String]
    val actionPoints = actionMap("actionPoints").asInstanceOf[Double].toInt
    val range = actionMap("range").asInstanceOf[Double].toInt
    val damage = actionMap("damage").asInstanceOf[Double].toInt
    val actionTypeId = actionMap("actionType").asInstanceOf[Double].toInt
    val actionType = ActionType.values.toList.find(_.id == actionTypeId)

    Action(id, description, imagePath, soundPath, actionPoints, range, actionType.get, damage)
  }

  private def blockObjectFromMap(blockMap: Map[String, Any]): BlockObject = {
    val name = blockMap("name").asInstanceOf[String]
    val imagePath = blockMap("imagePath").asInstanceOf[String]
    val positionMap = blockMap("position").asInstanceOf[Map[String, Double]]

    val rowIndex = positionMap("rowIndex").toInt
    val columnIndex = positionMap("columnIndex").toInt

    BlockObject(name, imagePath, Position(rowIndex, columnIndex))
  }

  private def playerObjectFromMap(playerMap: Map[String, Any], allActions: List[Action]) = {
    val name = playerMap("name").asInstanceOf[String]
    val imagePath = playerMap("imagePath").asInstanceOf[String]
    val positionMap = playerMap("position").asInstanceOf[Map[String, Any]]
    val rowIndex = positionMap("rowIndex").asInstanceOf[Double].toInt
    val columnIndex = positionMap("columnIndex").asInstanceOf[Double].toInt

    val viewDirectionId = playerMap("viewDirection").asInstanceOf[Double].toInt
    val viewDirectionOpt = Direction.values.toList.find(_.id == viewDirectionId)

    val playerNumber = playerMap("playerNumber").asInstanceOf[Double].toInt
    val wonImagePath = playerMap("wonImagePath").asInstanceOf[String]
    val maxActionPoints = playerMap("maxActionPoints").asInstanceOf[Double].toInt
    val maxHealthPoints = playerMap("maxHealthPoints").asInstanceOf[Double].toInt

    var actions: List[Action] = List[Action]()
    playerMap("actions") match {
      case listOfMaps: List[Map[String, Double]] => listOfMaps.foreach(map => {
        map.foreach {
          case (_, id) =>
            val action: Option[Action] = allActions.find(a => a.id == id)
            if (action.isDefined) {
              actions = actions ::: List(action.get)
            }
        }
      })
    }

    PlayerObject(name, imagePath, Position(rowIndex, columnIndex), viewDirectionOpt.get, playerNumber, wonImagePath, maxActionPoints, maxActionPoints, maxHealthPoints, maxHealthPoints, actions)
  }

  override def generateGame(rowCount: Int, columnCount: Int, completion: (Boolean) => Unit): GameConfigProvider = {
    var newGameObjects: List[GameObject] = List[GameObject]()

    val system = ActorSystem("GameGenerationSystem")
    val master = system.actorOf(Props(new GameGenerationMaster(numberOfWorkers = 4, numberOfMessages = 100, rowCount, colCount, (gameObjects) => {
      if (gameObjects.isDefined) {
        newGameObjects = newGameObjects ::: gameObjects.get
        completion(true)
      } else {
        completion(false)
      }
      system.terminate()
    })), name = "master")

    master ! Generate
    copy(newGameObjects, "sounds/explosion.wav", "images/background_opening.png",
      "images/background_woodlands.png", "images/background_actionbar.png", "images/hit.png",
      "images/app_icon.png", rowCount, columnCount)
  }
}