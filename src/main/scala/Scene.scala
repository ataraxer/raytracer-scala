package com.ataraxer.apps.raytracer.scala

import com.ataraxer.apps.raytracer.scala.shapes.{Sphere, Shape}
import com.ataraxer.apps.raytracer.scala.linal.Vec3


case class Scene(camera: Camera, shapes: List[Shape], lights: List[Light]) {
  private def intersections(ray: Ray) = {
    shapes.flatMap( _ intersectionWith ray )
  }


  def closestIntersectionWith(ray: Ray): Option[Intersection] = {
    this.intersections(ray)
      .filter( _.distance > Raytracer.accuracy )
      .sortBy( _.distance )
      .headOption
  }
}
