name := """angular_play_scala"""

version := "1.0-SNAPSHOT"

resolvers += "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  // Select Play modules
  //jdbc,      // The JDBC connection pool and the play.api.db API
  //anorm,     // Scala RDBMS Library
  //filters,   // A set of built-in filters
  //javaCore,  // The core Java API
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.0-SNAPSHOT"
  // Add your own project dependencies in the form:
  // "group" % "artifact" % "version"
)

play.Project.playScalaSettings
