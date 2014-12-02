package com.ataraxer.apps.raytracer.scala.shapes

import com.ataraxer.apps.raytracer.scala.{Intersection, Ray, Pixel}
import com.ataraxer.apps.raytracer.scala.linal.Vec3


case class Plain(normal: Vec3, distance: Double, color: Pixel) extends Shape {
  val refractionIndex = 1.0

  def normalAt(point: Vec3) = normal

  def intersectionWith(ray: Ray) = {
    val a: Double = ray.direction dot normal

    if (a == 0) {
      // ray is parallel to the plain
      None
    } else {
      val b = normal dot (ray.origin - (normal * distance));
      // distance from the origin to the point of intersection
      Some(Intersection(this, -b/a))
    }
  }
}
