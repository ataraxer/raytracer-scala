package com.ataraxer.apps.raytracer.scala

import com.ataraxer.apps.raytracer.scala.math.Vec3

/**
 * Created with IntelliJ IDEA.
 * User: Ataraxer
 * Date: 4/28/13
 * Time: 12:00 PM
 * To change this template use File | Settings | File Templates.
 */
class Ray(val origin: Vec3, val direction: Vec3) {
  def positionOf(intersection: Intersection) =
    origin + (direction * intersection.distance)

  override def toString =
    "Ray: D%s\tO%s".format(direction, origin)
}
