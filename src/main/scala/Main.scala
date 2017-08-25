

import scala.concurrent._
import ExecutionContext.Implicits.global

import slick.driver.MySQLDriver.api._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App {
  // The config string refers to mysqlDB that we defined in application.conf
  val db = Database.forConfig("mysqlDB")

  // represents the actual table on which we will be building queries on
  val peopleTable = TableQuery[People]


  // schema definition to generate DROP statement for people table
  val dropPeopleCmd = DBIO.seq(peopleTable.schema.drop)

  // schema definition to generate a CREATE TABLE command
  val initPeopleCmd = DBIO.seq(peopleTable.schema.create)


  println("\n\n")
  dropDB


  //Search_people
  //update_people(23)

  //  initialisePeople
  def dropDB = {
    println("\n dropDB \n")

    //do a drop followed by initialisePeople
    val dropFuture = Future {
      db.run(dropPeopleCmd)
    }
    //Attempt to drop the table, Await does not block here
    Await.result(dropFuture, Duration.Inf).andThen {
      case Success(_) => initialisePeople
      case Failure(error) =>
        println("Dropping the table failed due to: " + error.getMessage)
        initialisePeople
    }
  }


  def initialisePeople = {
    println("\n initialisePeople \n")

    //initialise people
    val setupFuture = Future {
      db.run(initPeopleCmd)
    }
    //once our DB has finished initializing we are ready to roll, Await does not block
    Await.result(setupFuture, Duration.Inf).andThen {
      case Success(_) => runQuery
      case Failure(error) =>
        println("Initialising the table failed due to: " + error.getMessage)
    }
  }

  def runQuery = {
    println("\n runQuery \n")

    val insertPeople = Future {
      val query = peopleTable ++= Seq(
        (10, "Jack", "Wood", 36),
        (20, "Tim", "Brown", 24),
        (20, "bob", "Brown", 23),
        (20, "jef", "failed", 65),
        (20, "Tim", "the", 27),
        (20, "Tim", "Brown", 4),
        (20, "Tim", "the", 54),
        (20, "Tim", "Brown", 13)
      )
      // insert into `PEOPLE` (`PER_FNAME`,`PER_LNAME`,`PER_AGE`)  values (?,?,?)
      println(query.statements.head) // would print out the query one line up
      db.run(query)
    }
    Await.result(insertPeople, Duration.Inf).andThen {
      case Success(result) => println(result); listPeople
      case Failure(error) => println("Welp! Something went wrong! " + error.getMessage)
    }
  }


  def listPeople = {
    println("\n listPeople \n")

    val queryFuture = Future {

        val q = peopleTable
        val updateAction = q.result
        val sql = updateAction.statements.head
        println(sql)
        db.run(updateAction)

    }

    Await.result(queryFuture, Duration.Inf).andThen {
      case Success(result) => println(result); update_people(234)
      case Failure(error) =>
        println("Listing people failed due to: " + error.getMessage)
    }
  }

  def update_people(x: Int) = {
    println("\n update_people \n")

    val queryFuture = Future {
      val q = for {c <- peopleTable if c.fName === "jef"} yield c.age
      val updateAction = q.update(x)
      val sql = q.updateStatement
      println(sql)
      db.run(updateAction)
    }
    Await.result(queryFuture, Duration.Inf).andThen {
      case Success(result) => println(result); delete_people //cleanup DB connection
      case Failure(error) =>
        println("Listing people failed due to: " + error.getMessage)
    }

  }

  def delete_people = {
    println("\n delete_people \n")

    val queryFuture = Future {
      val q = for {c <- peopleTable if c.fName === "bob"} yield c
      val updateAction = q.delete

      val sql = updateAction.statements.head
      println(sql)
      db.run(updateAction)
    }
    Await.result(queryFuture, Duration.Inf).andThen {
      case Success(result) => println(result); Search_people //cleanup DB connection
      case Failure(error) =>
        println("Listing people failed due to: " + error.getMessage)
    }
  }

  def Search_people = {
    println("\n Search_people \n")

    val queryFuture = Future {


      val q = for {c <- peopleTable if c.fName === "Jack"} yield c
      val updateAction = q.result
      val sql = updateAction.statements.head

      println(sql)
      db.run(updateAction)
    }
    Await.result(queryFuture, Duration.Inf).andThen {
      case Success(result) => println(result); num_of_people //cleanup DB connection
      case Failure(error) =>
        println("Listing people failed due to: " + error.getMessage)
    }
  }

  def num_of_people = {
    println("\n num_of_people \n")
    val queryFuture = Future {
      val q = peopleTable.length
      val updateAction = q.result
      val sql = updateAction.statements.head

      println(sql)
      db.run(updateAction)
    }
    Await.result(queryFuture, Duration.Inf).andThen {
      case Success(result) => println(result); average_age_people //cleanup DB connection
      case Failure(error) =>
        println("Listing people failed due to: " + error.getMessage)
    }
  }
  def average_age_people = {
    println("\n num_of_people \n")
    val queryFuture = Future {
      val q = peopleTable.map(_.age).avg
      val updateAction = q.result
      val sql = updateAction.statements.head

      println(sql)
      db.run(updateAction)
    }
    Await.result(queryFuture, Duration.Inf).andThen {
      case Success(result) => println(result); most_common_fName_people //cleanup DB connection
      case Failure(error) =>
        println("Listing people failed due to: " + error.getMessage)
    }
  }


  def most_common_fName_people = {
    println("\n most_common_fName_people \n")
    val queryFuture = Future {
      val q = peopleTable.groupBy { _.fName }
        .map { case (name, group) =>
          (name , group.length)
        }.sortBy(_._2.desc)


      val updateAction = q.result.head
      val sql = updateAction.statements.head

      println(sql)
      db.run(updateAction)
    }
    Await.result(queryFuture, Duration.Inf).andThen {
      case Success(result) => println(result); most_common_lName_people //cleanup DB connection
      case Failure(error) =>
        println("Listing people failed due to: " + error.getMessage)
    }
  }

  def most_common_lName_people = {
    println("\n most_common_lName_people \n")
    val queryFuture = Future {
      val q = peopleTable.groupBy { _.lName }
        .map { case (name, group) =>
          (name , group.length)
        }.sortBy(_._2.desc)


      val updateAction = q.result//.head
      val sql = updateAction.statements.head

      println(sql)
      db.run(updateAction)
    }
    Await.result(queryFuture, Duration.Inf).andThen {
      case Success(result) => println(result); db.close() //cleanup DB connection
      case Failure(error) =>
        println("Listing people failed due to: " + error.getMessage)
    }
  }




}