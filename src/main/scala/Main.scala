package com.ataraxer.apps.raytracer.scala

import com.ataraxer.apps.raytracer.scala.linal.Vec3
import com.ataraxer.apps.raytracer.scala.shapes.{Shape, Sphere, Plain}

import scala.math.{pow, floor, exp, sqrt}

import java.io.FileOutputStream


object RaytracerMain extends App {
  // Command line options
  val Array(width, height, output) = args
  val raytracer = Raytracer(width.toInt, height.toInt)

  println("rendering...")
  val rendering_start = System.nanoTime
  val pixels = raytracer.render
  val rendering_time = (System.nanoTime - rendering_start) / 1e6
  println("rendering done! time: %fms".format(rendering_time))

  println("saving...")
  val saving_start = System.nanoTime
  FilmSaver(width.toInt, height.toInt).save(pixels, output)
  val saving_time = (System.nanoTime - saving_start) / 1e6
  println("saving done! time: %fms".format(saving_time))
}
