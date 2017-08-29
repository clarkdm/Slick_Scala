import slick.driver.MySQLDriver.api._

class People(tag: Tag) extends Table[(Int, String, String, Int, Int, String, String, String, String)](tag, "PEOPLE") {
  def id = column[Int]("PER_ID", O.PrimaryKey, O.AutoInc)

  def fName = column[String]("PER_FNAME")

  def lName = column[String]("PER_LNAME")

  def age = column[Int]("PER_AGE")

  def Address1 = column[Int]("PER_Address1")

  def Address2 = column[String]("PER_Address2")

  def City = column[String]("PER_City")

  def PostalCode = column[String]("PER_PostalCode")

  def Country = column[String]("PER_Country")

  def * = (id, fName, lName, age, Address1, Address2, City, PostalCode, Country)
}