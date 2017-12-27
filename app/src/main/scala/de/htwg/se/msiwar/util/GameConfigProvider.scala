package de.htwg.se.msiwar.util

import java.io.FileNotFoundException

import de.htwg.se.msiwar.model.ActionType._
import de.htwg.se.msiwar.model._

import scala.io.Source
import scala.util.parsing.json.JSON

case class JSONException(private val message: String = "JSON parsing failed") extends Exception(message)

object GameConfigProvider {
  // Global sounds
  var attackSoundPath = "src/main/resources/sounds/explosion.wav"

  // Global images
  var openingBackgroundImagePath = "src/main/resources/images/background_opening.png"
  var levelBackgroundImagePath = "src/main/resources/images/background_woodlands.png"
  var actionbarBackgroundImagePath = "src/main/resources/images/background_actionbar.png"
  var attackImagePath = "src/main/resources/images/hit.png"

  // Setup board
  var rowCount = 9
  var colCount = 9

  // Setup actions
  private val moveAction = Action(1, "Panzer bewegen", "src/main/resources/images/action_move.png", "move.wav", 1, 1, MOVE, 0)
  private val shootAction = Action(2, "Schießen", "src/main/resources/images/action_attack.png", "shoot.wav", 1, 3, SHOOT, 2)
  private val waitAction = Action(3, "Warten", "src/main/resources/images/action_wait.png", "shoot.wav", 1, 1, WAIT, 2)
  private var actions = List(moveAction, shootAction, waitAction)

  // Setup players
  private val player1 = PlayerObject("Spieler1", "src/main/resources/images/light_tank_red.png", Position(1, 2), Direction.DOWN, 1, "src/main/resources/images/background_won_red.png", 3, 3, actions)
  private val player2 = PlayerObject("Spieler2", "src/main/resources/images/medium_tank_blue.png", Position(7, 6), Direction.LEFT, 2, "src/main/resources/images/background_won_blue.png", 3, 3, actions)

  private val wood1 = BlockObject("B", "src/main/resources/images/block_wood.png", Position(0, 0))
  private val wood2 = BlockObject("B", "src/main/resources/images/block_wood.png", Position(0, 1))
  private val wood3 = BlockObject("B", "src/main/resources/images/block_wood.png", Position(3, 7))
  private val wood4 = BlockObject("B", "src/main/resources/images/block_wood.png", Position(8, 8))
  private val wood5 = BlockObject("B", "src/main/resources/images/block_wood.png", Position(5, 4))
  private val wood6 = BlockObject("B", "src/main/resources/images/block_wood.png", Position(3, 2))
  private val wood7 = BlockObject("B", "src/main/resources/images/block_wood.png", Position(3, 3))
  private val wood8 = BlockObject("B", "src/main/resources/images/block_wood.png", Position(5, 0))
  private val wood9 = BlockObject("B", "src/main/resources/images/block_wood.png", Position(6, 0))
  private val wood10 = BlockObject("B", "src/main/resources/images/block_wood.png", Position(5, 8))
  private val wood11 = BlockObject("B", "src/main/resources/images/block_wood.png", Position(6, 8))

  private val mountain1 = BlockObject("B", "src/main/resources/images/block_mountain.png", Position(7, 2))
  private val mountain2 = BlockObject("B", "src/main/resources/images/block_mountain.png", Position(6, 6))
  private val mountain3 = BlockObject("B", "src/main/resources/images/block_mountain.png", Position(5, 3))
  private val mountain4 = BlockObject("B", "src/main/resources/images/block_mountain.png", Position(3, 1))
  private val mountain5 = BlockObject("B", "src/main/resources/images/block_mountain.png", Position(6, 2))
  private val mountain6 = BlockObject("B", "src/main/resources/images/block_mountain.png", Position(0, 8))
  private val mountain7 = BlockObject("B", "src/main/resources/images/block_mountain.png", Position(1, 8))
  private val mountain8 = BlockObject("B", "src/main/resources/images/block_mountain.png", Position(0, 3))
  private val mountain9 = BlockObject("B", "src/main/resources/images/block_mountain.png", Position(0, 4))

  private val lake1 = BlockObject("B", "src/main/resources/images/block_lake.png", Position(1, 6))
  private val lake2 = BlockObject("B", "src/main/resources/images/block_lake.png", Position(8, 1))

  private val city1 = BlockObject("B", "src/main/resources/images/block_city.png", Position(3, 5))

  var gameObjects: List[GameObject] = List(player1, player2, wood1, wood2, wood3, wood4, wood5, wood6, wood7, wood8, wood9, wood10, wood11, mountain1, mountain2, mountain3, mountain4, mountain5, mountain6, mountain7, mountain8, mountain9, lake1, lake2, city1)

  @throws(classOf[FileNotFoundException])
  @throws(classOf[JSONException])
  @throws(classOf[NoSuchElementException])
  def loadFromFile(configFilePath: String): Unit = {
    val json = Source.fromFile(configFilePath).getLines.mkString

    val parsed = JSON.parseFull(json)

    parsed match {
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
        map.foreach { case (_, id) =>
          val action: Option[Action] = allActions.find(a => a.id == id)
          if (action.isDefined) {
            actions = actions ::: List(action.get)
          }
        }
      })
    }

    PlayerObject(name, imagePath, Position(rowIndex, columnIndex), viewDirectionOpt.get, playerNumber, wonImagePath, maxActionPoints, maxHealthPoints, actions)
  }
}
