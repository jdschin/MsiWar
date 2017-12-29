package de.htwg.se.msiwar.util

import java.io.File
import javax.sound.sampled.AudioSystem

object SoundPlayer {
  def playWav(soundFilePath: String): Unit = {
    val audioIn = AudioSystem.getAudioInputStream(new File(getClass.getClassLoader.getResource(soundFilePath).getPath))
    val clip = AudioSystem.getClip
    clip.open(audioIn)
    clip.start()
  }
}
