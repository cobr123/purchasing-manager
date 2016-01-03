package models

import com.github.aselab.activerecord._
import com.github.aselab.activerecord.dsl._

case class Purchase(@Required name: String) extends ActiveRecord {
  lazy val clients = hasMany[Client]

  def getSum: BigDecimal = {
    clients.map(_.getSum).sum
  }
}

object Purchase extends ActiveRecordCompanion[Purchase] with PlayFormSupport[Purchase]
