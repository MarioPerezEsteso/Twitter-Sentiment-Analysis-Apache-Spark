name := "TwitterSentimentAnalysisSpark"

version := "1.0"

scalaVersion := "2.10.5"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.3.0" % "provided",
  "org.apache.spark" %% "spark-streaming" % "1.3.0",
  "org.apache.spark" %% "spark-streaming-twitter" % "1.3.0",
  "org.apache.spark" %% "spark-mllib" % "1.3.0"
)