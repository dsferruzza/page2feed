name := """page2feed"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

scalacOptions ++= Seq(
	"-unchecked",
	"-deprecation",
	"-feature",
	"-Xfatal-warnings",
	"-Xfuture",
	"-Xlint"
)

libraryDependencies ++= Seq(
	jdbc,
	evolutions,
	ws,
	"com.typesafe.play" %% "anorm" % "2.5.0",
	"org.postgresql" % "postgresql" % "9.4-1202-jdbc42",
	"net.ruippeixotog" %% "scala-scraper" % "0.1.1"
)
