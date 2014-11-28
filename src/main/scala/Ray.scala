package com.ataraxer.apps.raytracer.scala

import com.ataraxer.apps.raytracer.scala.linal.Vec3

/**
 * Created with IntelliJ IDEA.
 * User: Ataraxer
 * Date: 4/28/13
 * Time: 12:00 PM
 * To change this template use File | Settings | File Templates.
 */
case class Ray(origin: Vec3, direction: Vec3, refractionIndex: Double = 1.0) {
  def positionOf(intersection: Intersection) =
    origin + (direction * intersection.distance)
}
