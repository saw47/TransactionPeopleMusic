import java.time.LocalDateTime
import kotlin.random.Random
import kotlin.random.nextInt

var userBuyHistory = mutableSetOf<Int>()
var nowBuyAmount: Double = .00
var totalAmount: Double = .00
var totalAmountWithDiscount: Double = .00

fun buyMusic() {
    while (true) {
        print("Нажмите Y для покупки чего-либо на рандомную сумму или Q для завершения и подсчёта суммы со скидкой\n>>")
        var choice: String? = readLine()
        when (choice) {
            "Y", "y" -> {
                nowBuyAmount = Random.nextInt(100..12000).toDouble()
                totalAmount += nowBuyAmount
                userBuyHistory.add(LocalDateTime.now().month.value)
                println("что-то за $nowBuyAmount куплено, продолжаем")
                continue
            }
            "Q", "q" -> {
                discount()
                println("Сумма покупок без скидки - ${"%.2f".format(totalAmount)}, " +
                        "со скидкой -  ${"%.2f".format(totalAmountWithDiscount)}")
                println("_________________________________")
                break
            }
            else -> {
                println("Введено что-то недопустимое")
                continue
            }
        }
    }
}

fun discount() {
    val amountInProgress: Double = if (totalAmount.toInt() in 0..1000) {
        totalAmount
    } else if (totalAmount.toInt() in 1001..10000) {
        totalAmount - 100
    } else if(totalAmount.toInt() > 10001) {
        totalAmount * 0.95
    } else {
        totalAmount
    }
    totalAmountWithDiscount = if (isRegularCustomer()) {
        amountInProgress * 0.99
    } else {
        amountInProgress
    }
}

fun isRegularCustomer(): Boolean {
    var isARegular = false
    for (month in userBuyHistory) {
        if (month != userBuyHistory.first()) {
            isARegular = true
            break
        }
    }
    return isARegular
}
