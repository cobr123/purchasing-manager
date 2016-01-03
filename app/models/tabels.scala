package models

import com.github.aselab.activerecord._
import com.github.aselab.activerecord.dsl._

object Tables extends ActiveRecordTables with PlaySupport {
  val purchases = table[Purchase]
  val wares = table[Ware]
  val clients = table[Client]
}