package SFP.repository

import SFP.model.{Account, CreateAccount, ReplenishAccount, TransferMoney, WithdrawAccount}

import java.util.UUID
import scala.collection.mutable

class AccountRepositoryInMemory extends AccountRepository {
  private val bank = mutable.Map[UUID, Account]()

  override def list(): List[Account] = {
    bank.values.toList
  }

  override def createAccount(create: CreateAccount): Account = {
    val account = Account(id = UUID.randomUUID(), balance = 0)
    bank.put(account.id, account)
    account
  }

  override def replenishAccount(replenish: ReplenishAccount): Option[Account] = {
    bank.get(replenish.id).map { account =>
      val replenished = account.copy(balance = account.balance + replenish.amount)
      bank.put(account.id, replenished)
      replenished
    }
  }

  override def withdrawAccount(withdraw: WithdrawAccount): Option[Account] = {
    bank.get(withdraw.id).map { account =>
      if (account.balance < withdraw.amount) {
        println(s"Счет ${account.id} недостаточно средств для снятия ${withdraw.amount}. Доступно: ${account.balance}")
        val withdrawed = account.copy()
        bank.put(account.id, withdrawed)
        withdrawed
      }
      else {
        val withdrawed = account.copy(balance = account.balance - withdraw.amount)
        bank.put(account.id, withdrawed)
        withdrawed
      }
    }
  }

  override def transferMoney(transfer: TransferMoney): Option[Account] = {
    bank.get(transfer.idFrom).map { account =>
      if (account.balance < transfer.amount) {
        println(s"Счет ${account.id} недостаточно средств для перевода ${transfer.amount}. Доступно: ${account.balance}")
        val transferred = account.copy()
        bank.put(account.id, transferred)
        transferred
      }
      else {
        val transferred = account.copy(balance = account.balance - transfer.amount)
        bank.put(account.id, transferred)
        bank.get(transfer.idTo).map { account =>
          val transferred = account.copy(balance = account.balance + transfer.amount)
          bank.put(account.id, transferred)
        }
        transferred
      }
    }
  }

  override def deleteAccount(id: UUID) = {
    bank.remove(id)
  }
}