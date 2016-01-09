package controllers

import javax.inject.Inject

import play.api.Logger
import play.api.i18n.{Messages, I18nSupport, MessagesApi}
import play.api.mvc._
import views.html.{ware => view}

import models._
import com.github.aselab.activerecord.dsl._

class Wares @Inject() (val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def withClient(clientId: Long)(block: (Client, Request[AnyContent]) => Result) =
    Action {implicit request =>
      Client.find(clientId).map { m => block(m, request) }.getOrElse { NotFound }
    }

  def index(purchaseId: Long, clientId: Long) = Action { implicit request =>
    Client.find(clientId) match {
      case Some(c) => {
        Ok(view.index(c.wares.toList, c.purchaseId, c.id))
      }
      case _ => NotFound
    }
  }

  def show(purchaseId: Long, clientId: Long, id: Long) = Action { implicit request =>
    Client.find(clientId) match {
      case Some(c) => {
        c.wares.find(id) match {
          case Some(ware) => Ok(view.show(ware, purchaseId, clientId))
          case _ => NotFound
        }
      }
      case _ => NotFound
    }
  }

  def newPage(purchaseId: Long, clientId: Long) = Action { implicit request =>
    Client.find(clientId) match {
      case Some(c) => {
        Ok(view.edit(Ware.form, purchaseId, clientId, routes.Wares.create(purchaseId, clientId), Messages("common.create"), Messages("activerecord.models.Ware.create")))
      }
      case _ => NotFound
    }
  }

  def create(purchaseId: Long, clientId: Long) = withClient(clientId) { (m, request) =>
    Ware.form(Ware.newInstance("clientId" -> clientId)).bindFromRequest()(request).fold(
      errors => BadRequest(view.edit(errors, purchaseId, clientId, routes.Wares.create(purchaseId, clientId), Messages("common.create"), Messages("activerecord.models.Ware.create"))), {
        ware =>
          Ware.transaction { ware.save }
          Redirect(routes.Wares.show(purchaseId, ware.clientId, ware.id))
      })
  }

  def edit(purchaseId: Long, clientId: Long, id: Long) = Action { implicit request =>
    Client.find(clientId) match {
      case Some(c) => {
        c.wares.find(id) match {
          case Some(ware) =>
            Ok(view.edit(Ware.form(ware.assign("clientId" -> clientId)), purchaseId, clientId, routes.Wares.update(purchaseId, clientId, id), Messages("common.update"), "Ware edit"))
          case _ => NotFound
        }
      }
      case _ => NotFound
    }
  }

  def update(purchaseId: Long, clientId: Long, id: Long) = withClient(clientId) { (m, request) =>
    m.wares.find(id) match {
      case Some(ware) =>
        Ware.form(ware).bindFromRequest()(request).fold(
          errors => BadRequest(view.edit(errors, purchaseId, clientId, routes.Wares.update(purchaseId, clientId, id), Messages("common.update"), "Ware edit")), {
            ware =>
              Ware.transaction { ware.save }
              Redirect(routes.Clients.show(purchaseId, clientId))
          })
      case _ => NotFound
    }
  }

  def delete(purchaseId: Long, clientId: Long, id: Long) = withClient(clientId) { (m, request) =>
    m.wares.find(id) match {
      case Some(ware) =>
        Ware.transaction { ware.delete }
        Ok
      case _ => NotFound
    }
  }
}
