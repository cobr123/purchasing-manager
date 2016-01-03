package models

import com.github.aselab.activerecord._
import com.github.aselab.activerecord.dsl._

case class Ware(@Required name: String) extends ActiveRecord {
  @Required var clientId: Long = _
  lazy val client = belongsTo[Client]
}
object Ware extends ActiveRecordCompanion[Ware] with PlayFormSupport[Ware]
