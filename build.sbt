name := """page2feed"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
	jdbc,
	anorm,
	cache,
	ws,
	"org.postgresql" % "postgresql" % "9.3-1102-jdbc41",
	"net.ruippeixotog" %% "scala-scraper" % "0.1.1"
)
