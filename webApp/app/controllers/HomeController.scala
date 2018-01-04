package controllers

import javax.inject._

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.stream.Materializer
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, MergeHub, Sink, Source}
import akka.{Done, NotUsed}
import de.htwg.se.msiwar.aview.MainApp._
import de.htwg.se.msiwar.controller.{AttackActionResult, CellChanged, GameStarted, PlayerWon}
import play.api.libs.json.{JsArray, JsObject, JsValue, Json}
import play.api.mvc._

import scala.Option.empty
import scala.concurrent.{ExecutionContext, Future}
import scala.swing.Reactor


@Singleton
class HomeController @Inject()(cc: ControllerComponents)
                              (implicit actorSystem: ActorSystem,
                               mat: Materializer,
                               executionContext: ExecutionContext,
                               webJarsUtil: org.webjars.play.WebJarsUtil,
                               appController: AppController,
                               assets: Assets)
  extends AbstractController(cc) with Reactor {

  private var webSocketUrlOpt: Option[String] = empty
  private val assetsPreFix = "/assets/"

  listenTo(controller)

  reactions += {
    case e: CellChanged => sendCellChangedJsonToClient(e.rowColumnIndexes)
    case _: GameStarted => sendGameStartedJsonToClient()
    case e: AttackActionResult => sendAttackActionResultJsonToClient(e.rowIndex, e.columnIndex, e.attackImagePath)
    case e: PlayerWon => sendPlayerWonJsonToClient(e.wonImagePath)
  }


  private type WSMessage = String

  private def sendPlayerWonJsonToClient(wonImagePath: String): Unit = {
    val config = Json.obj(
      "event" -> "PlayerWon",
      "wonImagePath" -> assetsPreFix.concat(wonImagePath)
    )
    sendJsonToClient(config)
  }

  private def sendAttackActionResultJsonToClient(rowIndex: Int, columnIndex: Int, attackImagePath: String): Unit = {

    val pathOp = controller.cellContentImagePath(rowIndex, columnIndex)
    var cellContentImagePath = Option.empty[String]

    if (pathOp.isDefined) {
      cellContentImagePath = Option(assetsPreFix.concat(pathOp.get))
    } else {
      cellContentImagePath = Option("undefined")
    }
    val config = Json.obj(
      "event" -> "AttackActionResult",
      "cellContentImagePath" -> cellContentImagePath.get,
      "rowIndex" -> rowIndex,
      "columnIndex" -> columnIndex,
      "attackImagePath" -> assetsPreFix.concat(attackImagePath)
    )
    sendJsonToClient(config)
  }

  private def sendCellChangedJsonToClient(rowColumnIndexes: List[(Int, Int)]): Unit = {

    var cells = List[JsValue]()

    rowColumnIndexes.foreach {
      case (rowIndex, columnIndex) =>
        val pathOp = controller.cellContentImagePath(rowIndex, columnIndex)
        var cellContentImagePath = Option.empty[String]
        if (pathOp.isDefined) {
          cellContentImagePath = Option(assetsPreFix.concat(pathOp.get))
        } else {
          cellContentImagePath = Option("undefined")
        }
        val json = Json.obj(
          "rowIndex" -> rowIndex,
          "columnIndex" -> columnIndex,
          "cellContentImagePath" -> cellContentImagePath.get)
        cells = cells ::: List(json)

    }

    val config = Json.obj(
      "event" -> "CellChanged",
      "cells" -> JsArray(cells)
    )
    sendJsonToClient(config)
  }

  private def sendGameStartedJsonToClient(): Unit = {

    val backgroundPath = assetsPreFix.concat(appController.levelBackgroundImagePath)
    val openingPath = assetsPreFix.concat(appController.openingBackgroundImagePath)

    var rows = List[JsValue]()
    for (row <- 0 until appController.rowCount) {
      var columns = List[JsValue]()
      for (column <- 0 until appController.columnCount) {
        val pathOp = appController.cellContentImagePath(row, column)
        var json: Option[JsValue] = Option.empty
        if (pathOp.isDefined) {

          json = Option(Json.obj("cellContentImagePath" -> assetsPreFix.concat(pathOp.get)))

        } else {
          json = Option(Json.obj("cellContentImagePath" -> "undefined"))
        }
        columns = columns ::: List(json.get)
      }
      rows = rows ::: List(JsArray(columns))
    }

    val config = Json.obj(
      "event" -> "GameStarted",
      "levelBackgroundImagePath" -> backgroundPath,
      "openingBackgroundImagePath" -> openingPath,
      "rows" -> JsArray(rows)
    )
    sendJsonToClient(config)
  }

  private def sendJsonToClient(json: JsObject): Unit = {
    if (webSocketUrlOpt.isDefined) {

      val printSink: Sink[Message, Future[Done]] =
        Sink.foreach {
          case message: TextMessage.Strict =>
            println(message.text)
        }

      val jsonSource: Source[Message, NotUsed] =
        Source.single(TextMessage(json.toString()))

      val flow: Flow[Message, Message, Future[Done]] =
        Flow.fromSinkAndSourceMat(printSink, jsonSource)(Keep.left)
      Http().singleWebSocketRequest(WebSocketRequest(webSocketUrlOpt.get), flow)
    }
  }

  private val (socketSink, socketSource) = {
    val source = MergeHub.source[WSMessage]
      .log("source")
      .recoverWithRetries(-1, { case _: Exception ⇒ Source.empty })

    val sink = BroadcastHub.sink[WSMessage]
    source.toMat(sink)(Keep.both).run()
  }

  private val userFlow: Flow[WSMessage, WSMessage, _] = {
    Flow.fromSinkAndSource(socketSink, socketSource)
  }

  def index: Action[AnyContent] = Action { implicit request: RequestHeader =>
    webSocketUrlOpt = Option(routes.HomeController.socket().webSocketURL())
    Ok(views.html.main(webSocketUrlOpt.get, appController))
  }

  def socket(): WebSocket = {
    WebSocket.acceptOrResult[WSMessage, WSMessage] {
      _ =>
        Future.successful(userFlow).map { flow =>
          Right(flow)
        }.recover {
          case _: Exception =>
            val msg = "Cannot create websocket"
            val result = InternalServerError(msg)
            Left(result)
        }
    }
  }
}
