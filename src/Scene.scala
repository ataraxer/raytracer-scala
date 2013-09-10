package com.ataraxer.apps.raytracer.scala

import com.ataraxer.apps.raytracer.scala.shapes.{Sphere, Shape}
import com.ataraxer.apps.raytracer.scala.linal.Vec3

/**
 * Created with IntelliJ IDEA.
 * User: Ataraxer
 * Date: 4/28/13
 * Time: 11:35 AM
 * To change this template use File | Settings | File Templates.
 */
class Scene(val camera: Camera, val shapes: List[Shape], val lights: List[Light]) {

  private def intersections(ray: Ray): List[Intersection] =
    for (shape <- shapes)
      yield shape intersectionWith ray

  def closestIntersectionWith(ray: Ray): Intersection = {
    var min: Double = 0
    var result: Intersection = null

//    for (intersection <- this intersections ray)
//      println(intersection)

    for (intersection <- this intersections ray
         if intersection != null &&
           intersection.distance > Raytracer.accuracy) {
      if (intersection.distance < min || result == null) {
        min = intersection.distance
        result = intersection
      }
    }

    result
  }

}
