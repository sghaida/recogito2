package controllers

import jp.t2v.lab.play2.auth.AuthConfig
import models.Roles._
import models.UserService
import models.generated.tables.records.UserRecord
import scala.concurrent.{ ExecutionContext, Future }
import scala.reflect.{ ClassTag, classTag }
import play.api.mvc.{ Result, Results, RequestHeader }

trait Security extends AuthConfig { self: HasDatabase =>
  
  private val NO_PERMISSION = "No permission"
  
  type Id = String

  type User = UserRecord

  type Authority = Role

  val idTag: ClassTag[Id] = classTag[Id]

  val sessionTimeoutInSeconds: Int = 3600
  
  def resolveUser(id: Id)(implicit ctx: ExecutionContext): Future[Option[User]] =
    UserService.findByUsername(id)(db)

  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = {
    val destination = request.session.get("access_uri").getOrElse(myrecogito.routes.MyRecogitoController.index.toString)
    Future.successful(Results.Redirect(destination).withSession(request.session - "access_uri"))
  }

  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] =
    Future.successful(Results.Redirect(landing.routes.LandingController.index))

  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] =
    Future.successful(Results.Redirect(landing.routes.LoginController.showLoginForm).withSession("access_uri" -> request.uri))

  override def authorizationFailed(request: RequestHeader, user: User, authority: Option[Authority])(implicit context: ExecutionContext): Future[Result] =
    Future.successful(Results.Forbidden(NO_PERMISSION))

  def authorize(user: User, authority: Authority)(implicit ctx: ExecutionContext): Future[Boolean] = Future.successful {
    // TODO implement
    true
  }

}