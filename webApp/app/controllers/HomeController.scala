package controllers

import javax.inject._

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.stream.Materializer
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, MergeHub, Sink, Source}
import akka.{Done, NotUsed}
import de.htwg.se.msiwar.aview.MainApp._
import de.htwg.se.msiwar.controller.{CellChanged, GameStarted}
import play.api.libs.json.{JsArray, JsValue, Json}
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

  listenTo(controller)

  reactions += {
    case _: CellChanged => sendJsonToClient()
    case _: GameStarted => sendJsonToClient()
  }


  private type WSMessage = String

  private def sendJsonToClient(): Unit = {
    if (webSocketUrlOpt.isDefined) {

      val printSink: Sink[Message, Future[Done]] =
        Sink.foreach {
          case message: TextMessage.Strict =>
            println(message.text)
        }

      val assetsPreFix = "/assets/"
      val backgroundPath = assetsPreFix.concat(appController.levelBackgroundImagePath)
      val openingPath = assetsPreFix.concat(appController.openingBackgroundImagePath)

      var rows = List[JsValue]()
      for (row <- 0 until appController.rowCount) {
        var columns = List[JsValue]()
        for (column <- 0 until appController.columnCount) {
          val pathOp = appController.cellContentImagePath(row, column)
          var json: Option[JsValue] = Option.empty
          if (pathOp.isDefined) {

            json = Option(Json.obj("path" -> assetsPreFix.concat(pathOp.get)))

          } else {
            json = Option(Json.obj("path" -> "undefined"))
          }
          columns = columns ::: List(json.get)
        }
        rows = rows ::: List(JsArray(columns))
      }

      val config = Json.obj(
        "levelBackgroundImagePath" -> backgroundPath,
        "openingBackgroundImagePath" -> openingPath,
        "rows" -> JsArray(rows)
      )

      val jsonSource: Source[Message, NotUsed] =
        Source.single(TextMessage(config.toString()))

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
      case _ =>
        Future.successful(userFlow).map { flow =>
          Right(flow)
        }.recover {
          case e: Exception =>
            val msg = "Cannot create websocket"
            val result = InternalServerError(msg)
            Left(result)
        }
    }
  }
}
