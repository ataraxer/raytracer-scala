package com.ataraxer.apps.raytracer.scala

import java.io.FileOutputStream
import java.nio.{ByteBuffer, ByteOrder}


case class FilmSaver(width: Int, height: Int) {
  val filmDirectory = "film/"

  def save(pixels: List[Pixel], filmName: String) =
    saveBMP(pixels, filmName)

  private def saveBMP(pixels: List[Pixel], filmName: String) = {
    /* args */
    val dpi = 72
    /* args END */

    val k = width * height
    val imageSize = 4 * k
    val fileSize = 54 + imageSize

    val factor: Double = 39.375
    val meter: Int = factor.toInt

    val ppm: Int = dpi * meter

    def toByte(value: Int): List[Byte] =
      (for (i <- 0 to 3)
      yield (value >> 8 * i).toByte).toList

    val bmpFileHeader = ByteBuffer.allocate(14)
    bmpFileHeader.order(ByteOrder.LITTLE_ENDIAN)

    bmpFileHeader.put('B'.toByte)                // biType 2
    bmpFileHeader.put('M'.toByte)                // --------
    bmpFileHeader.putInt(fileSize)               // biSize 4
    bmpFileHeader.put(Array[Byte](0, 0, 0, 0))   // biReserved{1,2} 4
    bmpFileHeader.put(Array[Byte](54, 0, 0, 0))  // biOffBits 4

    bmpFileHeader.rewind()


    val bmpInfoHeader = ByteBuffer.allocate(40)
    bmpInfoHeader.order(ByteOrder.LITTLE_ENDIAN)
    bmpInfoHeader.put(Array[Byte](40, 0, 0, 0))  // biSize 4
    bmpInfoHeader.putInt(width)                  // biWidth 4
    bmpInfoHeader.putInt(height)                 // biHeight 4
    bmpInfoHeader.put(Array[Byte](1, 0))         // biPlanes 2
    bmpInfoHeader.put(Array[Byte](24,0))         // biBitCount 2
    bmpInfoHeader.put(Array[Byte](0, 0, 0, 0))   // biCompression 4
    bmpInfoHeader.putInt(imageSize)              // biSizeImage 4
    bmpInfoHeader.putInt(ppm)                    // biXPelsPerMeter 4
    bmpInfoHeader.putInt(ppm)                    // biYPelsPerMeter 4
    bmpInfoHeader.put(Array[Byte](0, 0, 0, 0))   // biClrUsed 4
    bmpInfoHeader.put(Array[Byte](0, 0, 0, 0))   // biClrImportant 4

    bmpInfoHeader.rewind()

    val imageBuffer = ByteBuffer.allocate(imageSize)
    pixels.foreach( _.writeTo(imageBuffer) )

    imageBuffer.rewind()

    val fileChannel = new FileOutputStream(filmDirectory + filmName).getChannel

    try {
      fileChannel.write(bmpFileHeader)
      fileChannel.write(bmpInfoHeader)
      fileChannel.write(imageBuffer)
    } finally {
      fileChannel.close()
    }
  }
}
