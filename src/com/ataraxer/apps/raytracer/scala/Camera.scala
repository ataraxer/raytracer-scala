package com.ataraxer.apps.raytracer.scala

import com.ataraxer.apps.raytracer.scala.math.Vec3

/**
 * Created with IntelliJ IDEA.
 * User: Ataraxer
 * Date: 4/28/13
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
class Camera(val position: Vec3, lookAt: Vec3) {
  val direction = (lookAt - position).normalize
  val right = (new Vec3(0, 1, 0) cross direction).normalize
  val down = right cross direction

  def shiftDirection(xShift: Double, yShift: Double) =
    (direction
      + right * (xShift - 0.5)
      + down  * (yShift - 0.5)).normalize
}
