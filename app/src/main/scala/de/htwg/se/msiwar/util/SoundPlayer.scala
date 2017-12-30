package de.htwg.se.msiwar.util

import java.io.{BufferedInputStream, InputStream}
import javax.sound.sampled.AudioSystem

object SoundPlayer {
  def playWav(soundFilePath: String): Unit = {
    var resourceIn:InputStream = null
    var buffResourceIn:BufferedInputStream = null
    try {
      resourceIn = getClass.getClassLoader.getResourceAsStream(soundFilePath)
      buffResourceIn = new BufferedInputStream(resourceIn)
      val audioIn = AudioSystem.getAudioInputStream(buffResourceIn)
      val clip = AudioSystem.getClip
      clip.open(audioIn)
      clip.start()
    } finally {
      if(resourceIn != null){
        resourceIn.close()
      }
      if(buffResourceIn != null){
        buffResourceIn.close()
      }
    }
  }
}
