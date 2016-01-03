package models

import com.github.aselab.activerecord._
import com.github.aselab.activerecord.dsl._

case class Client(@Required name: String) extends ActiveRecord {
  @Required var purchaseId: Long = _
  lazy val purchase = belongsTo[Purchase]
  lazy val wares = hasMany[Ware]
}
object Client extends ActiveRecordCompanion[Client] with PlayFormSupport[Client]
