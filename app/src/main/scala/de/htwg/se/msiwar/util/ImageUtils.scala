package de.htwg.se.msiwar.util

import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.InputStream
import javax.imageio.ImageIO
import javax.swing.ImageIcon


object ImageUtils {
  def scale(image: BufferedImage, width: Int, height: Int): Option[BufferedImage] = {
    val bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val g2d = bi.createGraphics()
    g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY))
    g2d.drawImage(image, 0, 0, width, height, null)
    g2d.dispose()
    Option[BufferedImage](bi)
  }

  def loadImage(imagePath: String): Option[BufferedImage] = {
    var image = Option.empty[BufferedImage]
    var is: InputStream = null
    try {
      is = getClass.getClassLoader.getResourceAsStream(imagePath)
      image = Option[BufferedImage](ImageIO.read(is))
    } finally {
      if (is != null) {
        is.close()
      }
    }
    image
  }

  def loadImageIcon(imagePath: String): Option[ImageIcon] = {
    var image = Option.empty[ImageIcon]
    var is: InputStream = null
    try {
      is = getClass.getClassLoader.getResourceAsStream(imagePath)
      image = Option[ImageIcon](new ImageIcon(ImageIO.read(is)))
    } finally {
      if (is != null) {
        is.close()
      }
    }
    image
  }
}
