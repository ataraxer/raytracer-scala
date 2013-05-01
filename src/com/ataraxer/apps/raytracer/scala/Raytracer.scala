package com.ataraxer.apps.raytracer.scala

import com.ataraxer.apps.raytracer.scala.math.Vec3
import com.ataraxer.apps.raytracer.scala.shapes.{Shape, Sphere, Plain}

import scala.math.{pow, floor}

import java.io.FileOutputStream

/**
 * Created with IntelliJ IDEA.
 * User: Ataraxer
 * Date: 4/27/13
 * Time: 10:17 PM
 * To change this template use File | Settings | File Templates.
 */
object Raytracer {
  val width  = 640
  val height = 480
  val aspectRatio = width.toDouble / height
  val aaDepth = 1
  val accuracy = 0.000001
  val clearColor = new Pixel(0, 0, 0, 0)
  val ambientLight = 0.2

  val reflectionsOn = true
  val lightingOn = true

  def scene(): Scene = {
    val prettyGreen = new Pixel(0.5, 1.0, 0.5, 0.3)
    val prettyBlue = new Pixel(0.25, 0.25, 0.75, 0.5)
    val tileFloor = new Pixel(1.0, 1.0, 1.0, 2)
    val white = new Pixel(1.0, 1.0, 1.0, 0.0)
    val mirror = new Pixel(1.0, 1.0, 1.0, 0.95)

    val cameraPosition = new Vec3(3, 1.5, -4)
    val center = new Vec3(0.5, 0, 0)
    val sceneCamera = new Camera(cameraPosition, center)

    val shapes: List[Shape] = List(
      new Plain(new Vec3(0, 1, 0), -1, tileFloor),
      new Plain(new Vec3(0, 0, 1), 9, mirror),
      new Sphere(new Vec3(-1.75, 0, 0), 1, prettyGreen),
      new Sphere(new Vec3(1.75, 0, 0), 1, prettyBlue))

    val lights: List[Light] = List(
      new Light(new Vec3(-7, 10, -10), white))

    new Scene(sceneCamera, shapes, lights)
  }


  def blendPixels(pixels: List[Pixel]): Pixel =
    // TODO: implement
    new Pixel(0, 0, 0, 0)


  def reflectionColor(ray: Ray, intersectionPosition: Vec3, shapeColor: Pixel, shapeNormal: Vec3) = {
    // reflection from objects with specular intensity
    val reflectionDirection: Vec3 =
      ray.direction reflectAgainst shapeNormal

    val reflectionRay = new Ray(
      intersectionPosition,
      reflectionDirection)

    // determine what ray intersects with first
    val reflectionIntersection = scene closestIntersectionWith reflectionRay
    if (reflectionIntersection != null) {
      val reflectionIntersectionColor =
        this intersectionColor (reflectionRay, reflectionIntersection)

      reflectionIntersectionColor * shapeColor.reflectivity
    } else
      new Pixel(0, 0, 0, 0)
  }


  def lightColor(ray: Ray, intersectionPosition: Vec3, shapeColor: Pixel, shapeNormal: Vec3) = {
    var deltaColor: Pixel = new Pixel(0, 0, 0, 0)

    for (light <- scene.lights) {
      val directionToLight =
        (light.position - intersectionPosition).normalize
      val distanceToLight = directionToLight.magnitude

      val angle = shapeNormal dot directionToLight

      if (angle > 0) {
        val shadowRay = new Ray(
          intersectionPosition,
          directionToLight)

        val shadowIntersection = scene closestIntersectionWith shadowRay
        val shadowed =
          (shadowIntersection != null && shadowIntersection.distance <= distanceToLight)

        if (!shadowed) {
          deltaColor += (shapeColor * light.color * angle)

//          if (intersection.shape.isReflective) {
          if (shapeColor.isReflective) {
            val reflectionDirection: Vec3 =
              ray.direction reflectAgainst shapeNormal

            val specular: Double = reflectionDirection dot directionToLight
            if (specular > 0)
              deltaColor +=
                (light.color * pow(specular, 10) * shapeColor.reflectivity)
          }
        }
      }
    }
    deltaColor
  }


  def intersectionColor(ray: Ray, intersection: Intersection): Pixel = {
    val intersectionPosition = ray positionOf intersection

    val shapeNormal = intersection.shape normalAt intersectionPosition
    var shapeColor = intersection.shape.color

    if (intersection.shape.isTiled) {
      // checkered-tile floor pattern
      val square: Int =
        floor(intersectionPosition.x).toInt + floor(intersectionPosition.z).toInt

      if ((square % 2) == 0)
        shapeColor = new Pixel(0, 0, 0, 0)
    }

    var finalColor: Pixel =
      if (lightingOn)
        shapeColor * ambientLight + lightColor(ray, intersectionPosition, shapeColor, shapeNormal)
      else
        shapeColor

    if (reflectionsOn && intersection.shape.isReflective)
      finalColor += reflectionColor(ray, intersectionPosition, shapeColor, shapeNormal)



    finalColor.clip
  }


  def traceRay(ray: Ray): Pixel = {
    val intersection = scene closestIntersectionWith ray
    if (intersection != null)
      this intersectionColor (ray, intersection)
    else
      clearColor
  }


  def rayToPixel(camera: Camera, x: Int, y: Int) = {
    val epsilon = 0.5
    var xAmount = (x + epsilon) / width
    var yAmount = ((height - y) + epsilon) / height

    if (width > height) {
      xAmount = aspectRatio * (xAmount - 0.5) + 0.5
    } else if (height < width) {
      yAmount = (1/aspectRatio) * (yAmount - 0.5) + 0.5
    }

    val direction: Vec3 = camera shiftDirection (xAmount, yAmount)
    new Ray(camera.position, direction)
  }


  def pixelAt(x: Int, y: Int, aaDepth: Int = 0): Pixel = {
    if (aaDepth > 0) {
      val aaPixels =
        (for (aax <- 0 to aaDepth; aay <- 0 to aaDepth)
          yield pixelAt(x + aax, y + aay)).toList
      this blendPixels aaPixels
    } else
      this traceRay rayToPixel(scene.camera, x, y)
  }


  def render(width: Int, height: Int) =
    (for (y <- 0 to height-1; x <- 0 to width-1)
      yield pixelAt(x, y)).toList

  def main(args: Array[String]) {
    println("rendering...")
    def tmp = {
      val pixels = render(width, height)
      FilmSaver.save(pixels, "scene2.bmp")
    }
    time{tmp}
  }

  def time[A](operation: => A) = {
    val s = System.nanoTime
    val result = operation
    val time = (System.nanoTime-s)/1e6
    println("done in: %fms".format(time))
    result
  }
}
