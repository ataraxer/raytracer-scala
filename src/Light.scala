package com.ataraxer.apps.raytracer.scala

import com.ataraxer.apps.raytracer.scala.linal.Vec3

/**
 * Created with IntelliJ IDEA.
 * User: Ataraxer
 * Date: 4/28/13
 * Time: 12:00 PM
 * To change this template use File | Settings | File Templates.
 */
class Light(val position: Vec3, val color: Pixel) {
  override def toString =
    "Light: %s".format(position.toString)
}
