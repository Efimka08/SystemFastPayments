package SFP.repository

import SFP.model._

import java.util.UUID


trait AccountRepository {
  def list(): List[Account]
  def createAccount(account: CreateAccount): Account
  def replenishAccount(account: ReplenishAccount): Option[Account]
  def withdrawAccount(account: WithdrawAccount): Option[Account]
  def transferMoney(account: TransferMoney): Option[Account]
  def deleteAccount(id: UUID)
}
