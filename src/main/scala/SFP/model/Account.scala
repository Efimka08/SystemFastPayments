package SFP.model

import java.util.UUID

case class Account(id: UUID, balance: Int)

case class CreateAccount()
case class ReplenishAccount(id: UUID, amount: Int)
case class WithdrawAccount(id: UUID, amount: Int)
case class TransferMoney(idFrom: UUID, idTo: UUID, amount: Int)