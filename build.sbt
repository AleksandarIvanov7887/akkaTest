name := "akkaTest"

version := "0.1"

scalaVersion := "2.13.0"

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.23",
  "com.typesafe.akka" %% "akka-remote" % "2.5.23",
  "com.typesafe.akka" %% "akka-http"   % "10.1.9",
  "com.typesafe.akka" %% "akka-stream" % "2.5.23",
  "com.iheart" %% "ficus" % "1.4.7",
  "org.typelevel" %% "cats-core" % "2.0.0",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.25" % Test,
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
//  "com.twitter" % "storehaus-cache_2.12" % "0.15.0"
)

//scalacOptions ++= Seq(
//  "-Xfatal-warnings",
//  "-Ypartial-unification"
//)