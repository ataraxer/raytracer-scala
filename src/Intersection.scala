package com.ataraxer.apps.raytracer.scala

import com.ataraxer.apps.raytracer.scala.shapes.Shape

/**
 * Created with IntelliJ IDEA.
 * User: Ataraxer
 * Date: 4/28/13
 * Time: 12:39 PM
 * To change this template use File | Settings | File Templates.
 */
class Intersection(val shape: Shape, val distance: Double) {
//  def this(i: Intersection) = this(i.shape, i.distance)
  override def toString =
    "Intersection: %s, %f".format(shape, distance)
}