package models

import com.github.aselab.activerecord._
import com.github.aselab.activerecord.dsl._

case class Purchase(@Required name: String) extends ActiveRecord {
  lazy val clients = hasMany[Client]
}
object Purchase extends ActiveRecordCompanion[Purchase] with PlayFormSupport[Purchase]
