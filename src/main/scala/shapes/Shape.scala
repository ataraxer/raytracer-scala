package com.ataraxer.apps.raytracer.scala.shapes

import com.ataraxer.apps.raytracer.scala.linal.Vec3
import com.ataraxer.apps.raytracer.scala.{Intersection, Ray, Pixel}


trait Shape {
  def color: Pixel
  def refractionIndex: Double

  def normalAt(point: Vec3): Vec3
  def intersectionWith(ray: Ray): Option[Intersection]
  def isReflective = color.isReflective
  def isTiled = color.reflectivity == 2
}
