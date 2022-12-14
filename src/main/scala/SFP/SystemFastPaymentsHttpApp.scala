package SFP

import SFP.SystemFastPaymentsApp.repository
import SFP.model._
import SFP.repository.AccountRepositoryInMemory
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport

object SystemFastPaymentsHttpApp extends App with FailFastCirceSupport {
  implicit val system: ActorSystem = ActorSystem("SystemFastPaymentsApp")
  val repository = new AccountRepositoryInMemory

  val AccIdFirst = repository.createAccount(CreateAccount()).id
  val AccIdSecond = repository.createAccount(CreateAccount()).id
  repository.replenishAccount(ReplenishAccount(AccIdFirst, 862))
  repository.replenishAccount(ReplenishAccount(AccIdSecond, 1103))

  val route: Route =
    (path("hello") & get) {
      complete("Hello scala world!")
    } ~
        (path("accounts") & get) {
          val list = repository.list()
          complete(list)
        } ~
        path("account" / JavaUUID) { id =>
          get {
            complete(repository.get(id))
          }
        } ~
        path("account" / "create") {
            (post & entity(as[CreateAccount])) { newAccount =>
                complete(repository.createAccount(newAccount))
            }
        } ~
        path("account" / "replenish") {
            (put & entity(as[ReplenishAccount])) { replenishAccount =>
                complete(repository.replenishAccount(replenishAccount))
            }
        } ~
        path("account" / "withdraw") {
            (put & entity(as[WithdrawAccount])) { withdrawAccount =>
                complete(repository.withdrawAccount(withdrawAccount))
            }
        } ~
        path("account" / "transfer") {
            (put & entity(as[TransferMoney])) { transferMoney =>
                complete(repository.transferMoney(transferMoney))
            }
        } ~
        path("account" / JavaUUID) { id =>
          delete {
            complete(repository.deleteAccount(id))
          }
        }

  Http().newServerAt("0.0.0.0", port = 8080).bind(route)
}
