inThisBuild(
  List(
    organization := "com.kubukoz",
    homepage := Some(url("https://github.com/kubukoz/zio-playground")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "kubukoz",
        "Jakub Kozłowski",
        "kubukoz@gmail.com",
        url("https://kubukoz.com")
      )
    )
  )
)

def crossPlugin(x: sbt.librarymanagement.ModuleID) = compilerPlugin(x.cross(CrossVersion.full))

val compilerPlugins = List(
  crossPlugin("org.typelevel" % "kind-projector" % "0.11.2"),
  crossPlugin("com.github.cb372" % "scala-typed-holes" % "0.1.7"),
  compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
)

val commonSettings = Seq(
  scalaVersion := "2.12.13",
  scalacOptions --= Seq("-Xfatal-warnings"),
  name := "zio-playground",
  libraryDependencies ++= compilerPlugins,
  libraryDependencies ++= Seq(
    "org.scalameta" %% "semanticdb" % "4.0.0",
    "dev.zio" %% "zio" % "1.0.4-2"
  )
)

val root =
  project.in(file(".")).settings(commonSettings).settings(skip in publish := true)
