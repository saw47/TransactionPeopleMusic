import java.time.LocalDateTime
import kotlin.random.*

class VK {
    var dayTransaction = mutableMapOf<LocalDateTime, Double>()
    var monthTransaction = mutableMapOf<LocalDateTime, Double>()
    var nowTransaction = mutableMapOf<LocalDateTime, Double>()

    var allTransactionToSession: Double = .00
    var allCommissionToSession: Double = .00
    var amount: Double = .00
    var commission: Double = .00

    val intAccount: Int = Random.nextInt(0..4)
    val outAccount: Int = Random.nextInt(0..4)

    fun startTransactionProcess() {
        print(
            "Тип вашей карты - ${PayCard.name(outAccount)}, вы переводите деньги на карту" +
                    " ${PayCard.name(intAccount)}\n"
        )

        while (true) {
            print("Введите сумму перевода в рублях и нажмите ENTER. Для завершения программы нажмите q: ")
            var userInput = readLine()
            if (userInput == "q") {
                println(
                    "Программа завершена. Вы перевели ${"%.2f".format(allTransactionToSession / 100)} c комиссией " +
                            "${"%.2f".format(allCommissionToSession / 100)}. Заходите ещё."
                )
                break
            } else {
                try {
                    amount = (userInput?.toDouble()!! * 100)
                    if (amount < 0) {
                        throw Exception("Negative number Exception")
                    }
                    nowTransaction[LocalDateTime.now()] = amount
                    commissionPrint()
                    continue
                } catch (e: java.lang.Exception) {
                    print("Введено недопустимое значение суммы перевода\n")
                    continue
                }
            }
        }
    }

    private fun commissionPrint() {
        when (outAccount) {
            PayCard.VKPAY -> {
                if (checkVKTransactionLimit()) {
                    dayTransaction.putAll(nowTransaction)
                    monthTransaction.putAll(nowTransaction)
                    commission = 0.00
                    allTransactionToSession += amount
                    printIsOkay()
                } else {
                    printIsNotOkayVK()
                }
                nowTransaction.clear()
            }
            PayCard.MASTERCARD, PayCard.MAESTRO, PayCard.VISA, PayCard.MIR -> {
                if (checkCardTransactionToDay() && checkCardTransactionToMonth()) {
                    if (intAccount == PayCard.VKPAY) {
                    } else if (outAccount == PayCard.MASTERCARD || outAccount == PayCard.MAESTRO) {
                        if (amount in 30000.00..7500000.00) {
                            commission = 0.00
                        } else {
                            commission = amount * 0.006 + 2000
                        }
                    } else if (outAccount == PayCard.VISA || outAccount == PayCard.MIR) {
                        commission = if ((amount * 0.0075) < 3500.00) {
                            3500.00
                        } else {
                            amount * 0.0075
                        }
                    }
                    dayTransaction.putAll(nowTransaction)
                    monthTransaction.putAll(nowTransaction)
                    allTransactionToSession += amount
                    allCommissionToSession += commission
                    printIsOkay()
                } else printIsNotOkayAllCard()
                nowTransaction.clear()
            }

        }
    }

    fun printIsOkay() {
        println(
            "Сумма перевода со счёта ${PayCard.name(outAccount)} на счёт ${PayCard.name(intAccount)}" +
                    ": ${"%.2f".format((amount / 100))}, комиссия за перевод: " +
                    "${"%.2f".format((commission / 100))} "
        )
    }

    fun printIsNotOkayVK() {
        println(
            "Превышен лимит переводов, приходите завтра или в следующем месяце. " +
                    "Лимит переводов в день 15000 рублей, вы перевели " +
                    "${"%.2f".format((dayTransaction.values.sum() / 100))} " +
                    "и пытаетесь перевести " +
                    "ещё ${"%.2f".format((amount / 100))} \n " +
                    "Лимит переводов в месяц 40000 рублей, вы перевели " +
                    "${"%.2f".format((monthTransaction.values.sum() / 100))} " +
                    "и пытаетесь перевести " +
                    "ещё ${"%.2f".format((amount / 100))} \n "
        )
    }

    fun printIsNotOkayAllCard() {
        println(
            "Превышен лимит переводов, приходите завтра или в следующем месяце. " +
                    "Лимит переводов в день 150000 рублей, вы перевели " +
                    "${"%.2f".format((dayTransaction.values.sum() / 100))} " +
                    "и пытаетесь перевести " +
                    "ещё ${"%.2f".format((amount / 100))} \n " +
                    "Лимит переводов в месяц 600000 рублей, вы перевели " +
                    "${"%.2f".format((monthTransaction.values.sum() / 100))} " +
                    "и пытаетесь перевести " +
                    "ещё ${"%.2f".format((amount / 100))} \n "
        )
    }

    fun checkCardTransactionToDay(): Boolean {
        var approve = true
        if (nowTransaction.values.sum() > TransactionLimiter.CARDDAYLIMIT)
            approve = false

        if (dayTransaction.isNotEmpty() && nowTransaction.keys.first().dayOfYear != dayTransaction.keys.first().dayOfYear)
            dayTransaction.clear()

        if ((dayTransaction.values.sum() + nowTransaction.values.sum()) > TransactionLimiter.CARDDAYLIMIT)
            approve = false

        return approve
    }

    fun checkCardTransactionToMonth(): Boolean {
        var approve = true

        if (nowTransaction.values.sum() > TransactionLimiter.CARDMONTHLIMIT)
            approve = false

        if (monthTransaction.isNotEmpty() && nowTransaction.keys.first().month != monthTransaction.keys.first().month)
            monthTransaction.clear()

        if ((monthTransaction.values.sum() + nowTransaction.values.sum()) > TransactionLimiter.CARDMONTHLIMIT)
            approve = false

        return approve
    }

    fun checkVKTransactionLimit(): Boolean {
        var approve = true

        if (nowTransaction.values.sum() > TransactionLimiter.VKPAYEACHLIMIT)
            approve = false

        if (monthTransaction.isNotEmpty() && nowTransaction.keys.first().month != monthTransaction.keys.first().month)
            monthTransaction.clear()

        if ((monthTransaction.values.sum() + nowTransaction.values.sum()) > TransactionLimiter.VKPAYMONTHLIMIT)
            approve = false

        return approve
    }
}

object PayCard {
    const val MASTERCARD = 0
    const val MAESTRO = 1
    const val VISA = 2
    const val MIR = 3
    const val VKPAY = 4

    fun name(account: Int): String = when (account) {
        0 -> "MASTERCARD"
        1 -> "MAESTRO"
        2 -> "VISA"
        3 -> "MIR"
        4 -> "VKPAY"
        else -> "Опаньки... х%?ня какая-то"
    }
}

object TransactionLimiter {
    const val CARDDAYLIMIT = 15000000
    const val CARDMONTHLIMIT = 60000000
    const val VKPAYEACHLIMIT = 1500000
    const val VKPAYMONTHLIMIT = 4000000
}




