package com.ataraxer.apps.raytracer.scala

import com.ataraxer.apps.raytracer.scala.linal.Vec3
import com.ataraxer.apps.raytracer.scala.shapes.{Shape, Sphere, Plain}

import scala.math.{pow, floor, exp, sqrt}

import java.io.FileOutputStream

/**
 * Created with IntelliJ IDEA.
 * User: Ataraxer
 * Date: 4/27/13
 * Time: 10:17 PM
 * To change this template use File | Settings | File Templates.
 */
object Raytracer {
  val width  = 1280
  val height = 720
  val aspectRatio = width.toDouble / height
  val aaDepth = 1
  val accuracy = 0.000001
  val clearColor = new Pixel(0, 0, 0, 0)
  val ambientLight = 0.2

  val refractionOn = false
  val reflectionsOn = true
  val lightingOn = true

  def scene: Scene = {
    val prettyGreen = new Pixel(0.5, 1.0, 0.5, 0.3)
    val prettyBlue = new Pixel(0.25, 0.25, 0.75, 0.5)
    val tileFloor = new Pixel(1.0, 1.0, 1.0, 2)
    val white = new Pixel(1.0, 1.0, 1.0, 0.0)
    val mirror = new Pixel(1.0, 1.0, 1.0, 0.95)

    val cameraPosition = new Vec3(3, 1.5, -4)
    val center = new Vec3(0.5, 0, 0)
//    val center = new Vec3(0, 0, 0)
    val sceneCamera = new Camera(cameraPosition, center)

    val shapes: List[Shape] = List(
      new Plain(new Vec3(0, 1, 0), -1, tileFloor),
      new Plain(new Vec3(0, 0, 1), 9, mirror),
      new Sphere(new Vec3(-1.75, 0, 0), 1, prettyGreen),
      new Sphere(new Vec3(1.75, 0, 0), 1, prettyBlue))

//    val shapes: List[Shape] = List(
//      new Plain(new Vec3(0, 1, 0), -1, tileFloor),
//      new Sphere(new Vec3(0, 0, 0), 1, prettyGreen))

    val lights: List[Light] = List(
      new Light(new Vec3(-7, 10, -10), white))

    new Scene(sceneCamera, shapes, lights)
  }


  def blendPixels(pixels: List[Pixel]): Pixel =
    // TODO: implement
    new Pixel(0, 0, 0, 0)


//  def refractionColor2(ray: Ray, color: Pixel, shape: Shape, scene: Scene,
//  shapeNormal: Vec3, intersectionPosition: Vec3, Depth: Int, Krf: Double, refractionIndexOut: Double,
//  distance: Double, HitOrMiss: Int)
//  {
//    val refractionIndexIn: Double = shape.refractionIndex
//    val n: Double = refractionIndexOut / refractionIndexIn;
//    // hit or miss --> distance to intersection?
//    val N1: Vec3 = shapeNormal * HitOrMiss;
//    val CosThetaI: Double = -N1 dor ray.direction;
//    val SinThetaI: Double = sqrt(1.0 - CosThetaI*CosThetaI);
//    val SinThetaT: Double = n * SinThetaI;
//    if(SinThetaT*SinThetaT < 1.0) {
//      double CosThetaT = sqrt(1.0 - SinThetaT*SinThetaT);
//      Cvector3 R4 = ray.GetDirection()*n - N1*(n*CosThetaI+CosThetaT);
//      R4.Normalize();
//      val refr_color = new Pixel(0.0, 0.0, 0.0, 0.0);
//      double dist1;
//      Cvector3 R5 = intersectionPosition + R4*EPSILON;
//      Ray R6 = Ray( R5,R4);
//      ray_trace( R6, refr_color, scene,Depth+1,refraction_index,dist1);
//      // Beer's Law
//      Color absorbance = shape->GetMaterial()->GetColor()*0.15*dist1*(-1.0);
//      Color transparency = Color( exp(absorbance.x), exp(absorbance.y), exp(absorbance.z) );
//      color += refr_color*transparency;
//    }
//    return 0;
//  }

  def refractionColor(ray: Ray, intersectionPosition: Vec3, shapeColor: Pixel, shapeNormal: Vec3, shape: Shape): Pixel = {
    println("refracting")
    def cofunc(v: Double) = sqrt(1 - v*v)

    //val vVec: Vec3 = (point - ray.origin).normalize
    val cosOut = shapeNormal dot -ray.direction
    val sinOut = cofunc(cosOut)

    if (cosOut < 0) println("in")
               else println("out")

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

      val refractionRay = new Ray(
        intersectionPosition,
        refractionRayDirection)

      (this traceRay refractionRay) * shapeColor.transparency
    } else
      new Pixel(0, 0, 0, 0)

//    if (cosine < 0){ // going into the sphere
//      float nr = 0.66;
//      val   sineIn = n * sineOut
//      val cosineIn = sqrt(1 - sineIn * sineIn);
//
//      if (cosineIn >= 0) {
////        val transmisiveRay = (nr * (normal.dot(-vVec))-rootContent)*normal-(nr*-vVec);
//        val transmisiveRay = (nr * cosineOut - cosineIn) * shapeNormal - (nr * -ray.direction);
//        deltaColor += shape.transparancy * shade((point + 0.0009 * vVec), transmisiveRay, recursionDepth + 1);
//      }
//    }
//    else { // going out of sphere
//      float nr = 1.5;
//      float rootContent = sqrtf(1 - nr * nr * (1-(-normal.dot(-rayDirection)*(-normal.dot(-rayDirection)))));
//      if(rootContent >= 0.0){
//        transmisiveRay = (nr * (-normal.dot(-rayDirection)) - rootContent) * -normal - ( nr * -rayDirection );
//        pixelColor += object.getTransparency() * shade((point + 0.0009 *
//          rayDirection),transmisiveRay, recursionDepth + 1);
//      }
//    }

  }


  def reflectionColor(ray: Ray, intersectionPosition: Vec3, shapeColor: Pixel, shapeNormal: Vec3) = {
    val reflectionDirection: Vec3 =
      ray.direction reflectAgainst shapeNormal

    val reflectionRay = new Ray(
      intersectionPosition,
      reflectionDirection)

    // determine what ray intersects with
    (this traceRay reflectionRay) * shapeColor.reflectivity
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

  var recursion = 5

  def intersectionColor(ray: Ray, intersection: Intersection, depth: Int = 0): Pixel = {
//    recursion -= 1
//    if (recursion < 0) {
//      recursion = 5
//      return new Pixel(0, 0, 0, 0)
//    }

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

    if (refractionOn)
      finalColor += refractionColor(ray, intersectionPosition, shapeColor, shapeNormal, intersection.shape)

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
    def time[A](operation: => A) = {
      val s = System.nanoTime
      val result = operation
      val time = (System.nanoTime-s)/1e6
      println("done in: %fms".format(time))
      result
    }
    def tmp = {
      val pixels = render(width, height)
      FilmSaver.save(pixels, "scene2.bmp")
    }
    time{tmp}
//    pixelAt(240, height - 240)
//    pixelAt(width/2, height/2)
  }
}
