package com.ataraxer.apps.raytracer.scala

import com.ataraxer.apps.raytracer.scala.linal.Vec3
import com.ataraxer.apps.raytracer.scala.shapes.{Shape, Sphere, Plain}

import scala.math.{pow, floor, exp, sqrt}


case class Raytracer(width: Int, height: Int, accuracy: Double) {
  def aspectRatio = width.toDouble / height
  val aaDepth = 1
  val clearColor = Pixel(0, 0, 0, 0)
  val ambientLight = 0.2

  val refractionOn = false
  val reflectionsOn = true
  val lightingOn = true


  def blendPixels(pixels: List[Pixel]): Pixel =
    // TODO: implement
    Pixel(0, 0, 0, 0)


  def refractionColor(
    scene: Scene,
    ray: Ray,
    intersectionPosition: Vec3,
    shapeColor: Pixel,
    shapeNormal: Vec3,
    shape: Shape): Pixel =
  {
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

      traceRay(scene, refractionRay) * shapeColor.transparency
    } else {
      Pixel(0, 0, 0, 0)
    }
  }


  def reflectionColor(
    scene: Scene,
    ray: Ray,
    intersectionPosition: Vec3,
    shapeColor: Pixel,
    shapeNormal: Vec3) =
  {
    val reflectionDirection: Vec3 =
      ray.direction reflectAgainst shapeNormal

    val reflectionRay = Ray(
      intersectionPosition,
      reflectionDirection)

    // determine what ray intersects with
    traceRay(scene, reflectionRay) * shapeColor.reflectivity
  }


  def lightColor(
    scene: Scene,
    ray: Ray,
    intersectionPosition: Vec3,
    shapeColor: Pixel,
    shapeNormal: Vec3) =
  {
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


  def intersectionColor(
    scene: Scene,
    ray: Ray,
    intersection: Intersection,
    depth: Int = 0): Pixel =
  {
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
        shapeColor * ambientLight + lightColor(scene, ray, intersectionPosition, shapeColor, shapeNormal)
      else
        shapeColor

    if (reflectionsOn && intersection.shape.isReflective)
      finalColor += reflectionColor(scene, ray, intersectionPosition, shapeColor, shapeNormal)

    if (refractionOn)
      finalColor += refractionColor(scene, ray, intersectionPosition, shapeColor, shapeNormal, intersection.shape)

    finalColor.clip
  }


  def traceRay(scene: Scene, ray: Ray): Pixel = {
    val intersection = scene closestIntersectionWith ray

    intersection map {
      this.intersectionColor(scene, ray, _)
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


  def pixelAt(scene: Scene, x: Int, y: Int, aaDepth: Int = 0): Pixel = {
    if (aaDepth > 0) {
      val aaPixels =
        (for (aax <- 0 to aaDepth; aay <- 0 to aaDepth)
          yield pixelAt(scene, x + aax, y + aay)).toList
      this blendPixels aaPixels
    } else
      traceRay(scene, rayToPixel(scene.camera, x, y))
  }


  def render(scene: Scene): Seq[Pixel] = {
    for {
      y <- 0 until height
      x <- 0 until width
    } yield {
      pixelAt(scene, x, y)
    }
  }
}
