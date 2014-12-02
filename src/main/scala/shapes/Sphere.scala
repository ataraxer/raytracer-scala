package com.ataraxer.apps.raytracer.scala.shapes

import math.{pow, sqrt}
import com.ataraxer.apps.raytracer.scala.{Intersection, Ray, Pixel}
import com.ataraxer.apps.raytracer.scala.linal.Vec3


case class Sphere(center: Vec3, radius: Double, color: Pixel) extends Shape {
  def refractionIndex = 1.0

  def normalAt(point: Vec3) = (point - center).normalize

  def intersectionWith(ray: Ray) = {
    val a: Double = 1  // normalized

    val b: Double =
      (2 * (ray.origin.x - center.x) * ray.direction.x) +
      (2 * (ray.origin.y - center.y) * ray.direction.y) +
      (2 * (ray.origin.z - center.z) * ray.direction.z)

    val c: Double =
      pow(ray.origin.x - center.x, 2) +
      pow(ray.origin.y - center.y, 2) +
      pow(ray.origin.z - center.z, 2) -
      pow(radius, 2)

    // discriminant
    val D: Double = (b * b) - (4 * c)

    if (D > 0) {
      // two intersections
      val rootOne: Double = ((-b - sqrt(D)) / 2) - 0.000001
      val intersectionPoint =
        if (rootOne > 0) {
          // the first root is the smallest positive root
          rootOne
        } else {
          // root two
          ((sqrt(D) - b) / 2) - 0.000001
        }
      Some(Intersection(this, intersectionPoint))
    } else {
      // the ray missed the sphere
      None
    }
  }
}
