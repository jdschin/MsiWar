package de.htwg.se.msiwar.util

import java.awt.RenderingHints
import java.awt.image.BufferedImage


object ImageTransformer {
  def scale(image:BufferedImage, width:Int, height:Int ): BufferedImage = {
    val bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val g2d = bi.createGraphics()
    g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY))
    g2d.drawImage(image, 0, 0, width, height, null)
    g2d.dispose()
    bi
  }
}
