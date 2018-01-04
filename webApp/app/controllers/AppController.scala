package controllers

import javax.inject.{Inject, Singleton}

import de.htwg.se.msiwar.aview.MainApp.controller
import play.api.mvc._

@Singleton
class AppController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def rowCount: Int = {
    controller.rowCount
  }

  def columnCount: Int = {
    controller.columnCount
  }

  def cellContentImagePath(rowIndex: Int, columnIndex: Int): Option[String] = {
    controller.cellContentImagePath(rowIndex, columnIndex)
  }

  def openingBackgroundImagePath: String = {
    controller.openingBackgroundImagePath
  }

  def levelBackgroundImagePath: String = {
    controller.levelBackgroundImagePath
  }

  def appIconImagePath: String = {
    controller.appIconImagePath
  }

}
