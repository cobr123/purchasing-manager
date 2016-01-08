package controllers

import javax.inject.Inject

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import views.html.{client => view}

import models._
import com.github.aselab.activerecord.dsl._

class Clients @Inject() (val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def withPurchase(purchaseId: Long)(block: (Purchase, Request[AnyContent]) => Result) =
    Action { request =>
      Purchase.find(purchaseId).map { m => block(m, request) }.getOrElse { NotFound }
    }

  def index(purchaseId: Long) = withPurchase(purchaseId) { (m, request) =>
    Ok(view.index(m.clients.toList, m.id))
  }

  def show(purchaseId: Long, id: Long) = withPurchase(purchaseId) { (m, request) =>
    m.clients.find(id) match {
      case Some(client) => Ok(view.show(client, purchaseId))
      case _ => NotFound
    }
  }

  def newPage(purchaseId: Long) = withPurchase(purchaseId) { (m, request) =>
    Ok(view.edit(Client.form, purchaseId, routes.Clients.create(purchaseId), "Create", "Client create"))
  }

  def create(purchaseId: Long) = withPurchase(purchaseId) { (m, request) =>
    Client.form(Client.newInstance("purchaseId" -> purchaseId)).bindFromRequest()(request).fold(
      errors => BadRequest(view.edit(errors, purchaseId, routes.Clients.create(purchaseId), "Create", "Client create")), {
        client =>
          Client.transaction { client.save }
          Redirect(routes.Clients.show(client.purchaseId, client.id))
      })
  }

  def edit(purchaseId: Long, id: Long) = withPurchase(purchaseId) { (m, request) =>
    m.clients.find(id) match {
      case Some(client) =>
        Ok(view.edit(Client.form(client.assign("purchaseId" -> purchaseId)), purchaseId, routes.Clients.update(purchaseId, id), "Update", "Client edit"))
      case _ => NotFound
    }
  }

  def update(purchaseId: Long, id: Long) = withPurchase(purchaseId) { (m, request) =>
    m.clients.find(id) match {
      case Some(client) =>
        Client.form(client).bindFromRequest()(request).fold(
          errors => BadRequest(view.edit(errors, purchaseId, routes.Clients.update(purchaseId, id), "Update", "Client edit")), {
            client =>
              Client.transaction { client.save }
              Redirect(routes.Clients.index(purchaseId))
          })
      case _ => NotFound
    }
  }

  def delete(purchaseId: Long, id: Long) = withPurchase(purchaseId) { (m, request) =>
    m.clients.find(id) match {
      case Some(client) =>
        Client.transaction { client.delete }
        Ok
      case _ => NotFound
    }
  }
}
