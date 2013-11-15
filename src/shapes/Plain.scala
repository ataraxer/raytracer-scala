package com.ataraxer.apps.raytracer.scala.shapes

import com.ataraxer.apps.raytracer.scala.{Intersection, Ray, Pixel}
import com.ataraxer.apps.raytracer.scala.linal.Vec3

/**
 * Created with IntelliJ IDEA.
 * User: Ataraxer
 * Date: 4/28/13
 * Time: 4:05 PM
 * To change this template use File | Settings | File Templates.
 */
class Plain(normal: Vec3, distance: Double, color: Pixel) extends Shape(color) {
  def normalAt(point: Vec3) = normal
  def intersectionWith(ray: Ray): Intersection = {
    val a: Double = ray.direction dot normal

    if (a == 0) {
      // ray is parallel to the plain
      return null
    }

    val b: Double = normal dot (ray.origin - (normal * distance));
    new Intersection(this, -b/a) // distance from the origin to the point of intersection
  }
}
