name := "Slick_Scala"

version := "0.1"

scalaVersion := "2.11.11"

libraryDependencies ++= Seq("com.typesafe.slick" % "slick_2.11" % "3.1.0",
                            "org.slf4j" % "slf4j-nop" % "1.6.4",
                            "mysql" % "mysql-connector-java" % "5.1.36")
