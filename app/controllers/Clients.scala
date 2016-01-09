package controllers

import javax.inject.Inject

import play.api.Logger
import play.api.i18n.{Messages, I18nSupport, MessagesApi}
import play.api.mvc._
import views.html.{client => view}

import models._
import com.github.aselab.activerecord.dsl._

class Clients @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def withPurchase(purchaseId: Long)(block: (Purchase, Request[AnyContent]) => Result) =
    Action { implicit request =>
      Purchase.find(purchaseId).map { m => block(m, request) }.getOrElse {
        NotFound
      }
    }

  def index(purchaseId: Long) = withPurchase(purchaseId) { (m, request) =>
    Ok(view.index(m.clients.toList, m.id))
  }

  def show(purchaseId: Long, id: Long) = Action { implicit request =>
    Purchase.find(purchaseId) match {
      case Some(p) => {
        p.clients.find(id) match {
          case Some(client) => Ok(view.show(client, purchaseId))
          case _ => NotFound
        }
      }
      case _ => NotFound
    }
  }

  def newPage(purchaseId: Long) = Action { implicit request =>
    Purchase.find(purchaseId) match {
      case Some(p) => {
        Ok(view.edit(Client.form, purchaseId, routes.Clients.create(purchaseId), Messages("common.create"), Messages("activerecord.models.Client.create")))
      }
      case _ => NotFound
    }
  }

  def create(purchaseId: Long) = withPurchase(purchaseId) { (m, request) =>
    Client.form(Client.newInstance("purchaseId" -> purchaseId)).bindFromRequest()(request).fold(
      errors => BadRequest(view.edit(errors, purchaseId, routes.Clients.create(purchaseId), Messages("common.create"), Messages("activerecord.models.Client.create"))), {
        client =>
          Client.transaction {
            client.save
          }
          Redirect(routes.Clients.show(client.purchaseId, client.id))
      })
  }

  def edit(purchaseId: Long, id: Long) = Action { implicit request =>
    Purchase.find(purchaseId) match {
      case Some(p) => {
        p.clients.find(id) match {
          case Some(client) =>
            Ok(view.edit(Client.form(client.assign("purchaseId" -> purchaseId)), purchaseId, routes.Clients.update(purchaseId, id), Messages("common.update"), "Client edit"))
          case _ => NotFound
        }
      }
      case _ => NotFound
    }
  }

  def update(purchaseId: Long, id: Long) = withPurchase(purchaseId) { (m, request) =>
    m.clients.find(id) match {
      case Some(client) =>
        Client.form(client).bindFromRequest()(request).fold(
          errors => BadRequest(view.edit(errors, purchaseId, routes.Clients.update(purchaseId, id), Messages("common.update"), "Client edit")), {
            client =>
              Client.transaction {
                client.save
              }
              Redirect(routes.Purchases.show(purchaseId))
          })
      case _ => NotFound
    }
  }

  def delete(purchaseId: Long, id: Long) = withPurchase(purchaseId) { (m, request) =>
    m.clients.find(id) match {
      case Some(client) =>
        Client.transaction {
          client.delete
        }
        Ok
      case _ => NotFound
    }
  }
}
