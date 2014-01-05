name := """angular_play_scala"""

version := "1.0-SNAPSHOT"

resolvers ++= Seq(
  "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
  Resolver.url("sbt-plugin-releases", url("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns),
  "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  // Select Play modules
  //jdbc,      // The JDBC connection pool and the play.api.db API
  cache,
  //anorm,     // Scala RDBMS Library
  filters,   // A set of built-in filters
  //javaCore,  // The core Java API
  // Add your own project dependencies in the form:
  // "group" % "artifact" % "version"
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.0",
  "securesocial" %% "securesocial" % "2.1.2"
)

play.Project.playScalaSettings