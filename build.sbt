
lazy val V = new {
  val cats: String      = "2.1.1"
  val circe: String     = "0.13.0"
  val http4s: String    = "0.21.6"
  val scalamock: String = "4.4.0"
  val scalatest: String = "3.2.0"
}

lazy val root = (project in file("."))
  .settings(
    organization := "org.genplan",
    name := "spotify4s",
    version := "0.1",
    scalaVersion := "2.13.3",
    libraryDependencies ++= Seq(
      "org.typelevel"         %% "cats-core"           % V.cats,
      "io.circe"              %% "circe-core"          % V.circe,
      "io.circe"              %% "circe-generic"       % V.circe,
      "io.circe"              %% "circe-literal"       % V.circe,
      "org.http4s"            %% "http4s-client"       % V.http4s,
      "org.http4s"            %% "http4s-circe"        % V.http4s,
      "io.circe"              %% "circe-parser"        % V.circe     % Test,
      "org.scalamock"         %% "scalamock"           % V.scalamock % Test,
      "org.scalatest"         %% "scalatest"           % V.scalatest % Test,
      "org.http4s"            %% "http4s-blaze-client" % V.http4s    % Test,
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1")
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Xfatal-warnings",
)
