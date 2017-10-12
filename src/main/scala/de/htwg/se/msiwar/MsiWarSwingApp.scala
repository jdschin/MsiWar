package de.htwg.se.msiwar

import scala.swing._

object MsiWarSwingApp extends SimpleSwingApplication{
  def top= new MainFrame {
    title = "MSI WAR"
    preferredSize = new Dimension(320, 240)
    contents = new GridPanel(9,9){
        contents += new Label("Test");
    }
  }
}