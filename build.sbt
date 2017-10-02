name := """play-scalajs-proto-example"""
organization := "kurtome"
version := "1.0-SNAPSHOT"

val scalaV = "2.12.3"

lazy val server = (project in file("server")).enablePlugins(PlayScala)
    .settings(
      scalaVersion := scalaV,

      // Include JS output from web project
      scalaJSProjects := Seq(web),
      pipelineStages in Assets := Seq(scalaJSPipeline),
      pipelineStages := Seq(digest, gzip),
      // triggers scalaJSPipeline when using compile or continuous compilation
      compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,

      libraryDependencies ++= Seq(
        guice,
        ws,
        "com.vmunier" %% "scalajs-scripts" % "1.1.1",
        "com.trueaccord.scalapb" %% "scalapb-json4s" % "0.3.2",
        "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
      )

      // Adds additional packages into Twirl
      //TwirlKeys.templateImports += "kurtome.controllers._"

      // Adds additional packages into conf/routes
      // play.sbt.routes.RoutesKeys.routesImport += "kurtome.binders._"
    )
    .dependsOn(sharedJvm)

val webBaseDir = file("web")
lazy val web = (project in webBaseDir)
  .settings(
    scalaVersion := scalaV,
    // This is an application with a main method
    scalaJSUseMainModuleInitializer := true,
    // Scala libraries that are Scalajs compatible
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.2"
    )
    // Pure javascript libraries
    //npmDependencies in Compile += "grpc" -> "1.6.0"
  ).enablePlugins(ScalaJSPlugin)
  .dependsOn(sharedJs)

val sharedBaseDir = file("shared")
lazy val shared = (crossProject.crossType(CrossType.Pure) in sharedBaseDir)
  .settings(
      scalaVersion := scalaV,

      // Define the location for proto source files.
      PB.protoSources in Compile += sharedBaseDir / "proto",

      PB.protoSources in Compile += target.value / "protobuf_external",

      // Configure locaion for gnerated proto source code.
      PB.targets in Compile := Seq(scalapb.gen() -> (sourceManaged in Compile).value),

      libraryDependencies ++= Seq(
        "com.trueaccord.scalapb" %%% "scalapb-runtime" % com.trueaccord.scalapb.compiler.Version.scalapbVersion,
        "com.trueaccord.scalapb" %%% "scalapb-runtime" % com.trueaccord.scalapb.compiler.Version.scalapbVersion % "protobuf"
      )
  )
  .jvmSettings( )
  .jsSettings( )


lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

// loads the server project at sbt startup
onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value