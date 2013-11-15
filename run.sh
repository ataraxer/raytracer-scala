#!/usr/bin/env bash

scalac -d out src/*.scala src/*/*.scala
scala -cp out com.ataraxer.apps.raytracer.scala.Raytracer 640 480 output.bmp
