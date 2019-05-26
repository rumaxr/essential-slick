package queries

import slick.jdbc.H2Profile.api._

sealed abstract class Rating(val stars: Int)

object Rating {
  def fromInt: Int => Rating = {
    case 5 => Awesome
    case 4 => Good
    case 3 => NotBad
    case 2 => Meh
    case 1 => Aaargh
    case _ => sys.error("Ratings only apply from 1 to 5")
  }

  def toInt: Rating => Int = {
    case Awesome => 5
    case Good => 4
    case NotBad => 3
    case Meh => 2
    case Aaargh => 1
  }

  final case object Awesome extends Rating(5)

  final case object Good extends Rating(4)

  final case object NotBad extends Rating(3)

  final case object Meh extends Rating(2)

  final case object Aaargh extends Rating(1)

  implicit val columnType: BaseColumnType[Rating] =
    MappedColumnType.base[Rating, Int](Rating.toInt, Rating.fromInt)
}
