package controllers


import javax.inject.Inject

import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi, Messages}
import play.api.mvc._
import views.html.{purchase => view}

import models._
import com.github.aselab.activerecord.dsl._

class Purchases @Inject() (val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def index = Action { implicit request =>
    Logger.logger.debug("request2Messages.lang: " +  Messages("common.languages"))
    Ok(view.index(Purchase.all.toList))
  }

  def show(id: Long) = Action {implicit request =>
    Purchase.find(id) match {
      case Some(purchase) => Ok(view.show(purchase))
      case _ => NotFound
    }
  }

  def newPage = Action { implicit request =>
    Ok(view.edit(Purchase.form, routes.Purchases.create, "Create", "Purchase create"))
  }

  def create = Action { implicit request =>
    Purchase.form.bindFromRequest.fold(
      errors => BadRequest(view.edit(errors, routes.Purchases.create, "Create", "Purchase create")), {
        purchase =>
          Purchase.transaction { purchase.save }
          Redirect(routes.Purchases.show(purchase.id))
      })
  }

  def edit(id: Long) = Action { implicit request =>
    Purchase.find(id) match {
      case Some(purchase) => Ok(view.edit(Purchase.form(purchase), routes.Purchases.update(id), "Update", "Purchase edit"))
      case _ => NotFound
    }
  }

  def update(id: Long) = Action { implicit request =>
    Purchase.find(id) match {
      case Some(purchase) =>
        Purchase.form(purchase).bindFromRequest.fold(
          errors => BadRequest(view.edit(errors, routes.Purchases.update(id), "Update", "Purchase edit")), {
            purchase =>
              Purchase.transaction { purchase.save }
              Redirect(routes.Purchases.index)
          })
      case _ => NotFound
    }
  }

  def delete(id: Long) = Action {
    Purchase.find(id) match {
      case Some(purchase) =>
        Purchase.transaction { purchase.delete }
        Ok
      case _ => NotFound
    }
  }
}
