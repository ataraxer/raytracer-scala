package com.ataraxer.apps.raytracer.scala

import java.nio.ByteBuffer


case class Pixel(
  red: Double,
  green: Double,
  blue: Double,
  reflectivity: Double,
  transparency: Double = 0.5)
{
  def brightness =
    (red + green + blue)/3

  def average(p: Pixel) =
    Pixel(
      (red * p.red)/2,
      (green * p.green)/2,
      (blue * p.blue)/2,
      reflectivity)

  def *(p: Pixel) =
    Pixel(
      red * p.red,
      green * p.green,
      blue * p.blue,
      reflectivity)

  def *(s: Double) =
    Pixel(red * s, green * s, blue * s, reflectivity)

  def +(p: Pixel) =
    Pixel(red + p.red, green + p.green, blue + p.blue, reflectivity)

  def clip = {
//    double red = mRed, green = mGreen, blue = mBlue;
    val allLight: Double = this.red + this.green + this.blue
    val excessLight: Double = allLight - 3
    def smooth(component: Double) =
      component + excessLight * (component / allLight)

    var red   = if (excessLight > 0) smooth(this.red) else this.red
    var green = if (excessLight > 0) smooth(this.green) else this.green
    var blue  = if (excessLight > 0) smooth(this.blue) else this.blue

    if (red > 1) { red = 1 }
    if (green > 1) { green = 1 }
    if (blue > 1) { blue = 1 }
    if (red < 0) { red = 0 }
    if (green < 0) { green = 0 }
    if (blue < 0) { blue = 0 }
    Pixel(red, green, blue, reflectivity)
  }

  def isReflective = reflectivity > 0 && reflectivity <= 1

//  def clip =
//    Pixel(red % 1, green % 1, blue % 1, reflectivity)

  override def toString =
    "Pixel(R%d, G%d, B%d)".format(
      (red * 255).toInt,
      (green * 255).toInt,
      (blue * 255).toInt)

  def writeTo(buffer: ByteBuffer) = {
    buffer.put((blue * 255).toByte)
    buffer.put((green * 255).toByte)
    buffer.put((red * 255).toByte)
  }
}
