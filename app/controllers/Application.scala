package controllers

import java.math.MathContext

import models._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import com.github.aselab.activerecord.dsl._

class Application extends Controller {
  Tables.initialize
//  initTestTables

  def initTestTables = Action {
//    Purchase.deleteAll()

    for (pi <- 1 to 3) {
      val p1 = Purchase("Purchase #" + pi).create
      for (ci <- 1 to 5) {
        val c1 = Client("Client #" + ci).create
        p1.clients << c1
        for (wi <- 1 to 7) {
          val w1 = Ware("Ware #" + wi, wi + ci + pi).create
          c1.wares << w1
        }
      }
    }
    Redirect(routes.Application.index)
  }

  def index = Action {
    Redirect(routes.Purchases.index)
  }
}
