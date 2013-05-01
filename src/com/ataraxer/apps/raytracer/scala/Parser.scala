package com.ataraxer.apps.raytracer.scala

import scala.io.Source

/**
 * Created with IntelliJ IDEA.
 * User: Ataraxer
 * Date: 5/1/13
 * Time: 12:31 PM
 * To change this template use File | Settings | File Templates.
 */
class Parser(fileName: String) {
  val file = Source.fromFile(fileName)

  def process(line: String) = {

  }

  def parse() = {
    var token = ""
    //var tokens = Array[String]()

    val emptyString = "\\s+".r

    val tokens = file.mkString.split("[,()\t]|[ \n]+")

    for (str <- tokens if str.nonEmpty)
      println(str)

//    val Pattern = "([a-cA-C])".r
//    word.firstLetter match {
//      case Pattern(c) => c
//      case _ =>
//    }


    file.close()
  }


}

object Parser {
  val keyWords = List("Colors", "Shapes", "Lights", "Camera")
  val shapes = List("Plain", "Sphere")
  val delimiters = List(',', ':', '(', ')', '\n', '\t', ' ')
}