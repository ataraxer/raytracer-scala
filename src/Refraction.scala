import com.ataraxer.apps.raytracer.scala.math.Vec3
import com.ataraxer.apps.raytracer.scala.{Pixel, Intersection, Ray}
import com.ataraxer.apps.raytracer.scala.shapes.Shape
import scala.math._

/**
 * Created with IntelliJ IDEA.
 * User: Ataraxer
 * Date: 5/3/13
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
class Refraction {

//  def refractionColor(ray: Ray, shape: Shape, shapeNormal: Vec3, refractionIndexOutside: Double, intersection: Intersection) = {
//    val intersectionPosition = ray positionOf intersection
//
//    val refractionIndexInside: Double = shape.refractionIndex
//    val n: Double = refractionIndexOutside/refractionIndexInside
//
//    val N1: Vec3 = shapeNormal * HitOrMiss
//
//    val incomingRayCosine = -N1 dot ray.direction
//    val incomingRaySine = sqrt(1.0 - incomingRayCosine * incomingRayCosine)
//    val refractedRaySine = n * incomingRaySine
//
//    if(refractedRaySine * refractedRaySine < 1.0) {
//      val refractedRayCosine = sqrt(1.0 - refractedRaySine * refractedRaySine)
//      val R4: Vec3 =
//        (ray.direction * n - N1 * (n * refractedRayCosine + refractedRayCosine)).normalize
//      var refractionColor: Pixel = new Pixel(0.0, 0.0, 0.0, 0)
//      val rayOrigin: Vec3 = intersectionPoint + R4 * 0.000001
//      val refractionRay = new Ray(rayOrigin, rayDirection, refractionIndexInside)
//      // Beer's Law
//      val absorbance: Pixel = shapeColor * 0.15 /** distance*/ * (-1.0)
//      val transparency: Pixel = new Pixel(exp(absorbance.red), exp(absorbance.green), exp(absorbance.blue))
//      color = (this traceRay reflectionRay) refractionColor * transparency
//    } else
//      new Pixel(0, 0, 0, 0)
//  }
//
//


}
