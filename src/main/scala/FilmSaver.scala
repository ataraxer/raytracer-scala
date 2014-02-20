package com.ataraxer.apps.raytracer.scala

import java.io.FileOutputStream

/**
 * Created with IntelliJ IDEA.
 * User: Ataraxer
 * Date: 5/1/13
 * Time: 1:48 PM
 * To change this template use File | Settings | File Templates.
 */
object FilmSaver {
  val filmDirectory = "film/"

  def save(pixels: List[Pixel], filmName: String) =
    saveBMP(pixels, filmName)

  private def saveBMP(pixels: List[Pixel], filmName: String) = {
    /* args */
    val dpi = 72
    /* args END */

    val k = Raytracer.width * Raytracer.height
    val imageSize = 4 * k
    val fileSize = 54 + imageSize

    val factor: Double = 39.375
    val meter: Int = factor.toInt

    val ppm: Int = dpi * meter

    def toByte(value: Int): List[Byte] =
      (for (i <- 0 to 3)
      yield (value >> 8 * i).toByte).toList

    val bmpFileHeader: List[Byte] =
      List[Byte]('B'.toByte, 'M'.toByte) ++ // biType 2
        toByte(fileSize) ++                 // biSize 4
        List[Byte](0,0,0,0) ++              // biReserved{1,2} 4
        List[Byte](54,0,0,0)                // biOffBits 4

    val bmpInfoHeader: List[Byte] =
      List[Byte](40, 0, 0, 0) ++            // biSize 4
      toByte(Raytracer.width) ++            // biWidth 4
      toByte(Raytracer.height) ++           // biHeight 4
      List[Byte](1,0) ++                    // biPlanes 2
      List[Byte](24,0) ++                   // biBitCount 2
      List[Byte](0,0,0,0) ++                // biCompression 4
      toByte(imageSize) ++                  // biSizeImage 4
      toByte(ppm) ++                        // biXPelsPerMeter 4
      toByte(ppm) ++                        // biYPelsPerMeter 4
      List[Byte](0,0,0,0) ++                // biClrUsed 4
      List[Byte](0,0,0,0)                   // biClrImportant 4

    val file: FileOutputStream = new FileOutputStream(filmDirectory + filmName)
    try {
      file.write(bmpFileHeader.toArray)
      file.write(bmpInfoHeader.toArray)
      for (pixel <- pixels) {
        file.write(pixel.toArray)
      }
    } finally {
      file.close()
    }
  }
}
