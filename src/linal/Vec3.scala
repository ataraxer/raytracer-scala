package com.ataraxer.apps.raytracer.scala.linal

import math.sqrt


/**
 * Created with IntelliJ IDEA.
 * User: Ataraxer
 * Date: 4/28/13
 * Time: 10:21 AM
 * To change this template use File | Settings | File Templates.
 */
class Vec3(val x: Double, val y: Double, val z: Double) {
  // default constructor
  def this() = this(0, 0, 0)

  override def toString: String =
    "(%f, %f, %f)".format(x, y, z)

  def magnitude =
    sqrt(x*x + y*y + z*z)

  def isNormal: Boolean =
    (magnitude == 1)

  def normalize =
    new Vec3(x/magnitude, y/magnitude, z/magnitude)

  def dot(v: Vec3): Double =
    (x*v.x + y*v.y + z*v.z)

  def cross(v: Vec3) = new Vec3(
    (y*v.z) - (z*v.y),
    (z*v.x) - (x*v.z),
    (x*v.y) - (y*v.x))

  def +(v: Vec3) =
    new Vec3(x+v.x, y+v.y, z+v.z)

  def -(v: Vec3) =
    new Vec3(x-v.x, y-v.y, z-v.z)

  def unary_- =
    new Vec3(-x, -y, -z)

  def *(s: Double) =
    new Vec3(x*s, y*s, z*s)

//  def reflectAgainst(surfaceNormal: Vec3): Vec3 = {
//    val dot1: Double  = surfaceNormal dot (-this)
//    val scalar1: Vec3 = surfaceNormal * dot1
//    val add1: Vec3    = scalar1 + this
//    val scalar2: Vec3 = add1 * 2
//    val add2: Vec3    = (-this) + scalar2
//    add2.normalize
//  }

  def reflectAgainst(surfaceNormal: Vec3): Vec3 =
    (this - surfaceNormal * (this dot surfaceNormal) * 2)
  /*
  def *(v: Vec3) = new Mat3(
    x*v.x, x*v.y, x*v.z,
    y*v.x, y*v.y, y*v.z,
    z*v.x, z*v.y, z*v.z)
  */
}
