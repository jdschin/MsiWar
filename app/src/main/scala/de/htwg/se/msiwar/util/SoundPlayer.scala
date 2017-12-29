package de.htwg.se.msiwar.util

import java.io.InputStream
import javax.sound.sampled.AudioSystem

object SoundPlayer {
  def playWav(soundFilePath: String): Unit = {
    var resourceIn:InputStream = null
    try {
      resourceIn = getClass.getClassLoader.getResourceAsStream(soundFilePath)
      val audioIn = AudioSystem.getAudioInputStream(resourceIn)
      val clip = AudioSystem.getClip
      clip.open(audioIn)
      clip.start()
    } finally {
      if(resourceIn != null){
        resourceIn.close()
      }
    }
  }
}
