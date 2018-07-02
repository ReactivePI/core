import scala.sys.process._

name := "core"

organization := "reactivepi"

scalaVersion := "2.12.6"

version := "0.2"

bintrayOrganization := Some("reactivepi")

bintrayReleaseOnPublish in ThisBuild := false

licenses += ("Apache-2.0", url("http://opensource.org/licenses/Apache-2.0"))

bintrayPackageLabels := Seq("Scala", "IoT", "Raspberry PI")

val nativeClasses = List(
  "nl.fizzylogic.reactivepi.i2c.I2CDevice"
)

val nativeDeviceSources = List(
  "src/jni/nl_fizzylogic_reactivepi_i2c_I2CDevice.c"
)

val nativeGenerateHeaders = taskKey[Int]("Generates JNI headers for the library")

nativeGenerateHeaders := {
  ("javah -classpath target/scala-2.11/classes -d src/jni " + nativeClasses.mkString(" ")) !
}
