name := "core"

organization := "reactivepi"

scalaVersion := "2.11.6"

version := "0.1"

bintrayOrganization := Some("reactivepi")

bintrayReleaseOnPublish in ThisBuild := false

licenses += ("Apache-2.0", url("http://opensource.org/licenses/Apache-2.0"))

bintrayPackageLabels := Seq("Scala", "IoT", "Raspberry PI")

val nativeClasses = List(
  "nl.fizzylogic.reactivepi.i2c.I2CDevice",
  "nl.fizzylogic.reactivepi.spi.SPIDevice"
)

val nativeGenerateHeaders = taskKey[Int]("Generates JNI headers for the library")

nativeGenerateHeaders := {
  ("javah -classpath target/scala-2.11/classes -d src/jni " + nativeClasses.mkString(" ")) !
}
