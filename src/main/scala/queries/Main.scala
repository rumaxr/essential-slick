package queries

import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.duration._

object Main extends App {

  import queries.Rating.columnType

  case class Album(artist: String, title: String, year: Int, rating: Rating, id: Long = 0L)

  class AlbumTable(tag: Tag) extends Table[Album](tag, _tableName = "albums") {

    def artist: Rep[String] = column[String]("artist")

    def title: Rep[String] = column[String]("title")

    def year: Rep[Int] = column[Int]("year")

    def rating: Rep[Rating] = column[Rating]("rating")

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    override def * = (artist, title, year, rating, id) <> (Album.tupled, Album.unapply)
  }

  lazy val AlbumTable = TableQuery[AlbumTable]

  val createTableAction =
    AlbumTable.schema.create

  val insertAlbumsAction =
    AlbumTable ++= Seq(
      Album("Keyboard Cat", "Keyboard Cat's Greatest Hits", 2009, Rating.Awesome),
      Album("Spice Girls", "Spice", 1996, Rating.Awesome),
      Album("Rick Astley", "Whenever You Need Somebody", 1987, Rating.Good),
      Album("Manowar", "The Triumph of Steel", 1992, Rating.Meh),
      Album("Justin Bieber", "Believe", 2013, Rating.Aaargh)
    )

  val selectAlbumsAction =
    AlbumTable.result

  val selectWhereQuery =
    AlbumTable
      .filter(_.artist === "Spice Girls")

  val selectSortedQuery =
    AlbumTable
      .sortBy(_.year.desc)

  val exercise1 =
    AlbumTable
      .filter(_.year > 1990)
      .filter(_.rating >= (Rating.NotBad: Rating)) // .filter(_.rating.asColumnOf[Int] >= Rating.NotBad.stars)
      .sortBy(_.artist.asc)
      .result

  val exercise2 =
    AlbumTable
      .sortBy(_.year.asc)
      .map(_.title)
      .result

  val db = Database.forConfig("scalaxdb")

  def exec[T](action: DBIO[T]): T =
    Await.result(db.run(action), 2 seconds)

  exec(createTableAction)
  exec(insertAlbumsAction)
  println("Exercise 1")
  //  exec(selectAlbumsAction).foreach(println)
  exec(exercise1).foreach(println)
  //  exec(exercise2).foreach(println)
}
