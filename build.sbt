import AssemblyKeys._

name := "EncDec"

version := "1.0"

val sparkVersion = "1.0.0"

//val hadoopVersion = "2.0.0-cdh4.4.0"
val hadoopVersion = "2.3.0-cdh5.0.1"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "com.skplanet.pdp.cryptoutils" % "crypto-dic" % "0.3",
  "org.apache.hadoop" % "hadoop-hdfs" % hadoopVersion % "provided" exclude("commons-daemon", "commons-daemon") exclude( "log4j", "log4j"),
  "org.apache.hadoop" % "hadoop-common" % hadoopVersion % "provided" exclude("log4j", "log4j") exclude ("org.slf4j", "slf4j-log4j12"),
  "org.specs2" %% "specs2" % "2.3.12" % "test"
)

resolvers ++= Seq(
  "SKP Repository" at "http://mvn.skplanet.com/content/repositories/releases",
  "CDH" at "https://repository.cloudera.com/artifactory/cloudera-repos/"
)

scalacOptions in Test ++= Seq("-Yrangepos")

mainClass in assembly := Some("com.skplanet.di.tools.EncDec")
