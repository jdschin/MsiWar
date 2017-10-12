package de.htwg.se.msiwar

import scala.swing._
import scala.swing.event.MouseEntered

object MsiWarSwingApp extends SimpleSwingApplication {
  def top = new MainFrame {
    title = "MSI WAR"
    preferredSize = new Dimension(320, 240)

    contents = new GridPanel(9, 9) {
      val a = 0
      for (a <- 0 to 81) {
        var label = new Label("Test" + a) {
          listenTo(mouse.moves)
          reactions += {
            case e: MouseEntered => onMouseEntered()
          }

          def onMouseEntered() {
            border = new javax.swing.border.LineBorder(java.awt.Color.BLUE)
          }
        }
        contents += label
      }
    }
  }
}