package controllers

import java.math.MathContext

import models._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import com.github.aselab.activerecord.dsl._

class Application extends Controller {
  initTestTables

  def initTestTables = {
    Tables.initialize
    Purchase.deleteAll()

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
  }

  def index = Action {
    val purchases = Purchase.all.toList
    Ok(views.html.purchase_list("Purchase list", purchases))
  }

  def purchase(purchaseId: Long) = Action {
    Purchase.find(purchaseId).getOrElse(None) match {
      case p: Purchase => {
        val purchases = Purchase.all.toList
        Ok(views.html.client_list("Purchase card.", p.id, purchases, p.clients.toList))
      }
      case _ => NotFound
    }
  }

  def client(clientId: Long) = Action {
    Client.find(clientId).getOrElse(None) match {
      case c: Client => {
        val purchases = Purchase.all.toList
        val clients = c.purchase.clients.toList
        Ok(views.html.ware_list("Client card.", c.purchaseId, c.id, purchases, clients, c.wares.toList))
      }
      case _ => NotFound
    }
  }

  def addPurchase = Action { implicit request =>
    val form = Form("name" -> text(minLength = 1))
    val name = form.bindFromRequest.get
    val newPurchase = Purchase(name).create
    Redirect(routes.Application.purchase(newPurchase.id))
  }

  def addClient(purchaseId: Long) = Action { implicit request =>
    Purchase.find(purchaseId).getOrElse(None) match {
      case p: Purchase => {
        Client.inTransaction({
          val form = Form("name" -> text(minLength = 1))
          val name = form.bindFromRequest.get
          val newClient = Client(name).create
          p.clients << newClient
          Redirect(routes.Application.client(newClient.id))
        })
      }
      case _ => NotFound
    }
  }

  def addWare(clientId: Long) = Action { implicit request =>
    Client.find(clientId).getOrElse(None) match {
      case c: Client => {
        Ware.inTransaction({
          val form = Form(tuple("name" -> text(minLength = 1), "price" -> bigDecimal))
          val (name, price) = form.bindFromRequest.get
          val newWare = Ware(name, price).create
          c.wares << newWare
          Redirect(routes.Application.client(clientId))
        })
      }
      case _ => NotFound
    }
  }
}
