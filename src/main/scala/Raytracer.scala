package com.ataraxer.apps.raytracer.scala

import com.ataraxer.apps.raytracer.scala.linal.Vec3
import com.ataraxer.apps.raytracer.scala.shapes.{Shape, Sphere, Plain}

import scala.math.{pow, floor, exp, sqrt}


case class Raytracer(width: Int, height: Int) {
  def aspectRatio = width.toDouble / height
  val aaDepth = 1
  val accuracy = 0.000001
  val clearColor = Pixel(0, 0, 0, 0)
  val ambientLight = 0.2

  val refractionOn = false
  val reflectionsOn = true
  val lightingOn = true

  def scene: Scene = {
    val prettyGreen = Pixel(0.5, 1.0, 0.5, 0.3)
    val prettyBlue = Pixel(0.25, 0.25, 0.75, 0.5)
    val tileFloor = Pixel(1.0, 1.0, 1.0, 2)
    val white = Pixel(1.0, 1.0, 1.0, 0.0)
    val mirror = Pixel(1.0, 1.0, 1.0, 0.95)

    val cameraPosition = Vec3(3, 1.5, -4)
    val center = Vec3(0.5, 0, 0)
//    val center = Vec3(0, 0, 0)
    val sceneCamera = Camera(cameraPosition, center)

    val shapes: List[Shape] = List(
      Plain(Vec3(0, 1, 0), -1, tileFloor),
      Plain(Vec3(0, 0, 1), 9, mirror),
      Sphere(Vec3(-1.75, 0, 0), 1, prettyGreen),
      Sphere(Vec3(1.75, 0, 0), 1, prettyBlue))

//    val shapes: List[Shape] = List(
//      Plain(Vec3(0, 1, 0), -1, tileFloor),
//      Sphere(Vec3(0, 0, 0), 1, prettyGreen))

    val lights: List[Light] = List(
      Light(Vec3(-7, 10, -10), white))

    Scene(sceneCamera, shapes, lights, accuracy)
  }


  def blendPixels(pixels: List[Pixel]): Pixel =
    // TODO: implement
    Pixel(0, 0, 0, 0)


  def refractionColor(ray: Ray, intersectionPosition: Vec3, shapeColor: Pixel, shapeNormal: Vec3, shape: Shape): Pixel = {
    def cofunc(v: Double) = sqrt(1 - v*v)

    //val vVec: Vec3 = (point - ray.origin).normalize
    val cosOut = shapeNormal dot -ray.direction
    val sinOut = cofunc(cosOut)

    val n: Double =
      if(cosOut < 0)
        1.0/1.52
        //ray.refractionIndex / shape.refractionIndex
      else
        1.52/1.0
        //shape.refractionIndex / ray.refractionIndex

    val sinIn  = n * sinOut
    val cosIn  = cofunc(sinIn)

    if (sinIn <= 1 && sinIn >= -1) {

      val refractionRayDirection = shapeNormal * (n * cosOut - cosIn) + (ray.direction * n)

      val refractionRay = Ray(
        intersectionPosition,
        refractionRayDirection)

      (this traceRay refractionRay) * shapeColor.transparency
    } else {
      Pixel(0, 0, 0, 0)
    }
  }


  def reflectionColor(ray: Ray, intersectionPosition: Vec3, shapeColor: Pixel, shapeNormal: Vec3) = {
    val reflectionDirection: Vec3 =
      ray.direction reflectAgainst shapeNormal

    val reflectionRay = Ray(
      intersectionPosition,
      reflectionDirection)

    // determine what ray intersects with
    (this traceRay reflectionRay) * shapeColor.reflectivity
  }


  def lightColor(ray: Ray, intersectionPosition: Vec3, shapeColor: Pixel, shapeNormal: Vec3) = {
    var deltaColor: Pixel = Pixel(0, 0, 0, 0)

    for (light <- scene.lights) {
      val directionToLight =
        (light.position - intersectionPosition).normalize
      val distanceToLight = directionToLight.magnitude

      val angle = shapeNormal dot directionToLight

      if (angle > 0) {
        val shadowRay = Ray(
          intersectionPosition,
          directionToLight)

        val shadowIntersection = scene closestIntersectionWith shadowRay
        val shadowed = shadowIntersection map {
          _.distance <= distanceToLight
        } getOrElse false

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


  def intersectionColor(ray: Ray, intersection: Intersection, depth: Int = 0): Pixel = {
    val intersectionPosition = ray positionOf intersection

    val shapeNormal = intersection.shape normalAt intersectionPosition
    var shapeColor = intersection.shape.color

    if (intersection.shape.isTiled) {
      // checkered-tile floor pattern
      val square: Int =
        floor(intersectionPosition.x).toInt + floor(intersectionPosition.z).toInt

      if ((square % 2) == 0)
        shapeColor = Pixel(0, 0, 0, 0)
    }

    var finalColor: Pixel =
      if (lightingOn)
        shapeColor * ambientLight + lightColor(ray, intersectionPosition, shapeColor, shapeNormal)
      else
        shapeColor

    if (reflectionsOn && intersection.shape.isReflective)
      finalColor += reflectionColor(ray, intersectionPosition, shapeColor, shapeNormal)

    if (refractionOn)
      finalColor += refractionColor(ray, intersectionPosition, shapeColor, shapeNormal, intersection.shape)

    finalColor.clip
  }


  def traceRay(ray: Ray): Pixel = {
    val intersection = scene closestIntersectionWith ray

    intersection map {
      this.intersectionColor(ray, _)
    } getOrElse {
      clearColor
    }
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
    Ray(camera.position, direction)
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


  def render = {
    (for (y <- 0 to height-1; x <- 0 to width-1)
      yield pixelAt(x, y)).toList
  }
}
