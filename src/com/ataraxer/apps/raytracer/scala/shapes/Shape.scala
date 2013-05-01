package com.ataraxer.apps.raytracer.scala.shapes

import com.ataraxer.apps.raytracer.scala.math.Vec3
import com.ataraxer.apps.raytracer.scala.{Intersection, Ray, Pixel}

/**
 * Created with IntelliJ IDEA.
 * User: Ataraxer
 * Date: 4/28/13
 * Time: 12:34 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class Shape(val color: Pixel) {
  def normalAt(point: Vec3): Vec3
  def intersectionWith(ray: Ray): Intersection
  def isReflective = color.isReflective
  def isTiled = color.reflectivity == 2
}
