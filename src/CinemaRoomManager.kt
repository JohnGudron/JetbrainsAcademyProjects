import java.util.Locale

class CinemaRoomManager {

    fun main() {
        println("Enter the number of rows:")
        val rowsNum = readLine()!!.toInt()
        println("Enter the number of seats in each row:")
        val seatsNum = readLine()!!.toInt()
        val totalSeats = rowsNum * seatsNum
        val cinemaHall = MutableList(rowsNum) { MutableList(seatsNum) { "S" } }
        var purchasedTicketCounter = 0
        var currentIncome = 0

        fun printHall() {
            println("Cinema:")
            println("  ${(1..seatsNum).joinToString(" ")}")
            for (i in cinemaHall.indices) {
                print("${i + 1} ")
                println(cinemaHall[i].joinToString(" "))
            }
        }

        fun buyTicket() {
            while (true) {
                println("Enter a row number:")
                val selectRow = readLine()!!.toInt()
                println("Enter a seat number in that row:")
                val selectSeat = readLine()!!.toInt()
                try {
                    if (cinemaHall[selectRow - 1][selectSeat - 1] == "B") throw Exception()
                    val ticketPrice = if (totalSeats < 60) 10 else if (selectRow <= rowsNum / 2) 10 else 8
                    println("Ticket price: $$ticketPrice")
                    cinemaHall[selectRow - 1][selectSeat - 1] = "B"
                    purchasedTicketCounter++
                    currentIncome += ticketPrice
                    break
                } catch (e: IndexOutOfBoundsException) {
                    println("Wrong input!")
                } catch (e: Exception) {
                    println("That ticket has already been purchased!")
                }
            }
        }

        fun statistics() {
            println("Number of purchased tickets: $purchasedTicketCounter")
            println("Percentage: ${"%.2f".format(purchasedTicketCounter.toDouble() / totalSeats * 100).format(Locale.US)}%")
            println("Current income: $$currentIncome")
            println(
                "Total income: $${
                    if (totalSeats < 60) totalSeats * 10
                    else if (rowsNum % 2 == 0) totalSeats * 9
                    else totalSeats * 9 - seatsNum
                }"
            )
        }

        fun menu() {
            println("\n1. Show the seats\n2. Buy a ticket\n3. Statistics\n0. Exit")
            when (readLine()!!.toInt()) {
                1 -> {
                    printHall()
                    menu()
                }
                2 -> {
                    buyTicket()
                    menu()
                }
                3 -> {
                    statistics()
                    menu()
                }
            }
        }

        menu()
    }
}