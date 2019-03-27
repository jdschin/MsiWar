package de.htwg.se.msiwar.util

import java.io.FileNotFoundException

import akka.actor.{ActorSystem, Props}
import de.htwg.se.msiwar.model.ActionType._
import de.htwg.se.msiwar.model._

import scala.io.Source
import scala.util.parsing.json.JSON

case class JSONException(private val message: String = "JSON parsing failed") extends Exception(message)

class GameConfigProviderImpl extends GameConfigProvider {
  // Global sounds
  var attackSoundPath = "sounds/explosion.wav"

  // Global images
  var openingBackgroundImagePath = "images/background_opening.png"
  var levelBackgroundImagePath = "images/background_woodlands.png"
  var actionbarBackgroundImagePath = "images/background_actionbar.png"
  var attackImagePath = "images/hit.png"
  var appIconImagePath = "images/app_icon.png"

  // Setup board
  var rowCount = 9
  var colCount = 9

  // Setup actions
  private val moveAction = Action(id = 1, "Move", "images/action_move.png", "move.wav", actionPoints = 1, range = 1, MOVE, damage = 0)
  private val shootAction = Action(id = 2, "Shoot", "images/action_attack.png", "shoot.wav", actionPoints = 1, range = 3, SHOOT, damage = 2)
  private val waitAction = Action(id = 3, "Wait", "images/action_wait.png", "shoot.wav", actionPoints = 1, range = 1, WAIT, damage = 2)
  private var actions = List(moveAction, shootAction, waitAction)

  // Setup players
  private val player1 = PlayerObject("Player1", "images/light_tank_red.png", Position(1, 2), Direction.DOWN, playerNumber = 1, "images/background_won_red.png", maxActionPoints = 3, maxHealthPoints = 3, actions)
  private val player2 = PlayerObject("Player2", "images/medium_tank_blue.png", Position(7, 6), Direction.LEFT, playerNumber = 2, "images/background_won_blue.png", maxActionPoints = 3, maxHealthPoints = 3, actions)

  private val wood1 = BlockObject("B", "images/block_wood.png", Position(0, 0))
  private val wood2 = BlockObject("B", "images/block_wood.png", Position(0, 1))
  private val wood3 = BlockObject("B", "images/block_wood.png", Position(3, 7))
  private val wood4 = BlockObject("B", "images/block_wood.png", Position(8, 8))
  private val wood5 = BlockObject("B", "images/block_wood.png", Position(5, 4))
  private val wood6 = BlockObject("B", "images/block_wood.png", Position(3, 2))
  private val wood7 = BlockObject("B", "images/block_wood.png", Position(3, 3))
  private val wood8 = BlockObject("B", "images/block_wood.png", Position(5, 0))
  private val wood9 = BlockObject("B", "images/block_wood.png", Position(6, 0))
  private val wood10 = BlockObject("B", "images/block_wood.png", Position(5, 8))
  private val wood11 = BlockObject("B", "images/block_wood.png", Position(6, 8))

  private val mountain1 = BlockObject("B", "images/block_mountain.png", Position(7, 2))
  private val mountain2 = BlockObject("B", "images/block_mountain.png", Position(6, 6))
  private val mountain3 = BlockObject("B", "images/block_mountain.png", Position(5, 3))
  private val mountain4 = BlockObject("B", "images/block_mountain.png", Position(3, 1))
  private val mountain5 = BlockObject("B", "images/block_mountain.png", Position(6, 2))
  private val mountain6 = BlockObject("B", "images/block_mountain.png", Position(0, 8))
  private val mountain7 = BlockObject("B", "images/block_mountain.png", Position(1, 8))
  private val mountain8 = BlockObject("B", "images/block_mountain.png", Position(0, 3))
  private val mountain9 = BlockObject("B", "images/block_mountain.png", Position(0, 4))

  private val lake1 = BlockObject("B", "images/block_lake.png", Position(1, 6))
  private val lake2 = BlockObject("B", "images/block_lake.png", Position(8, 1))

  private val city1 = BlockObject("B", "images/block_city.png", Position(3, 5))

  var gameObjects: List[GameObject] = List(player1, player2, wood1, wood2, wood3, wood4, wood5, wood6, wood7, wood8, wood9, wood10, wood11, mountain1, mountain2, mountain3, mountain4, mountain5, mountain6, mountain7, mountain8, mountain9, lake1, lake2, city1)

  def listScenarios: List[String] = {
    FileLoader.loadFilesFromDirPath("/scenarios").sorted
  }

  @throws(classOf[FileNotFoundException])
  @throws(classOf[JSONException])
  @throws(classOf[NoSuchElementException])
  def loadFromFile(configFilePath: String): GameConfigProvider = {
    val json = Source.fromInputStream(getClass.getClassLoader.getResourceAsStream("scenarios/" + configFilePath)).getLines.mkString
    JSON.parseFull(json) match {
      case Some(jsonMap: Map[String, Any]) =>
        gameObjects = List[GameObject]()
        actions = List[Action]()

        jsonMap("levelBackgroundImagePath") match {
          case s: String => levelBackgroundImagePath = s
          case _ => throw JSONException()
        }

        jsonMap("actionbarBackgroundImagePath") match {
          case s: String => actionbarBackgroundImagePath = s
          case _ => throw JSONException()
        }

        jsonMap("attackImagePath") match {
          case s: String => attackImagePath = s
          case _ => throw JSONException()
        }

        jsonMap("attackSoundPath") match {
          case s: String => attackSoundPath = s
          case _ => throw JSONException()
        }

        jsonMap("rowCount") match {
          case i: Double => rowCount = i.toInt
          case _ => throw JSONException()
        }

        jsonMap("colCount") match {
          case i: Double => colCount = i.toInt
          case _ => throw JSONException()
        }

        jsonMap("blockObjects") match {
          case listOfMaps: List[Map[String, Any]] =>
            val blockObjects: List[GameObject] = listOfMaps.map(blockObjectFromMap)
            gameObjects = gameObjects ::: blockObjects
          case _ => throw JSONException()
        }

        jsonMap("actions") match {
          case listOfMaps: List[Map[String, Any]] =>
            val actionsFromJson: List[Action] = listOfMaps.map(actionFromMap)
            actions = actions ::: actionsFromJson
          case _ => throw JSONException()
        }
        jsonMap("playerObjects") match {
          case listOfMaps: List[Map[String, Any]] =>
            val playersFromJson: List[PlayerObject] = listOfMaps.map(playerObjectFromMap(_, actions))
            gameObjects = gameObjects ::: playersFromJson
          case _ => throw JSONException()
        }

      case _ => throw JSONException()
    }
    this
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

    PlayerObject(name, imagePath, Position(rowIndex, columnIndex), viewDirectionOpt.get, playerNumber, wonImagePath, maxActionPoints, maxHealthPoints, actions)
  }

  override def generateGame(rowCount: Int, columnCount: Int, completion: (Boolean) => Unit): GameConfigProvider = {
    // Setup board
    this.rowCount = rowCount
    this.colCount = columnCount

    val system = ActorSystem("GameGenerationSystem")
    val master = system.actorOf(Props(new GameGenerationMaster(numberOfWorkers = 4, numberOfMessages = 100, rowCount, colCount, actions, (gameObjects) => {
      if (gameObjects.isDefined) {
        this.gameObjects = gameObjects.get
        completion(true)
      } else {
        completion(false)
      }
      system.terminate()
    })), name = "master")

    master ! Generate
    this
  }
}