package controllers

import models._
import play.api.i18n.{Messages, Lang, MessagesApi, I18nSupport}
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.Logger
import javax.inject.Inject

class Application @Inject() (val messagesApi: MessagesApi)  extends Controller  with I18nSupport {
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

  def changeLocale = Action {implicit request =>
    val referrer = request.headers.get(REFERER).getOrElse("/")
    val form = Form("locale" -> nonEmptyText)
    Logger.logger.debug("request2Messages.lang: " +  request2Messages.lang)

    form.bindFromRequest.fold(
      errors => {
        Logger.logger.debug("The locale can not be change to : " + errors.get)
        Redirect(referrer)
      },
      locale => {
        Logger.logger.debug("Change user lang to : " + locale)
        Redirect(referrer).withLang(Lang(locale))
      }
    )
  }
}
