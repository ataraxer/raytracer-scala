package com.ataraxer.apps.raytracer.scala

import com.ataraxer.apps.raytracer.scala.linal.Vec3
import com.ataraxer.apps.raytracer.scala.shapes.{Shape, Sphere, Plain}

import scala.math.{pow, floor, exp, sqrt}

import java.io.FileOutputStream


object RaytracerMain extends App {
  val accuracy = 0.000001


  val scene = {
    val prettyGreen = Pixel(0.5, 1.0, 0.5, 0.3)
    val prettyBlue = Pixel(0.25, 0.25, 0.75, 0.5)
    val tileFloor = Pixel(1.0, 1.0, 1.0, 2)
    val white = Pixel(1.0, 1.0, 1.0, 0.0)
    val mirror = Pixel(1.0, 1.0, 1.0, 0.95)

    val cameraPosition = Vec3(3, 1.5, -4)
    val center = Vec3(0.5, 0, 0)
    //val center = Vec3(0, 0, 0)
    val sceneCamera = Camera(cameraPosition, center)

    val shapes: List[Shape] = List(
      Plain(Vec3(0, 1, 0), -1, tileFloor),
      Plain(Vec3(0, 0, 1), 9, mirror),
      Sphere(Vec3(-1.75, 0, 0), 1, prettyGreen),
      Sphere(Vec3(1.75, 0, 0), 1, prettyBlue))

    //val shapes: List[Shape] = List(
      //Plain(Vec3(0, 1, 0), -1, tileFloor),
      //Sphere(Vec3(0, 0, 0), 1, prettyGreen))

    val lights: List[Light] = List(
      Light(Vec3(-7, 10, -10), white))

    Scene(sceneCamera, shapes, lights, accuracy)
  }


  // Command line options
  val Array(width, height, output) = args
  val raytracer = Raytracer(width.toInt, height.toInt, accuracy)

  println("rendering...")
  val rendering_start = System.nanoTime
  val pixels = raytracer.render(scene)
  val rendering_time = (System.nanoTime - rendering_start) / 1e6
  println("rendering done! time: %fms".format(rendering_time))

  println("saving...")
  val saving_start = System.nanoTime
  FilmSaver(width.toInt, height.toInt).save(pixels, output)
  val saving_time = (System.nanoTime - saving_start) / 1e6
  println("saving done! time: %fms".format(saving_time))
}
