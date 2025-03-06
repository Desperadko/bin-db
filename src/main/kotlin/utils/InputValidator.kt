package org.example.utils

import java.util.Scanner

object InputValidator {
    fun readValidStringInput(prompt: String, errorMsg: String): String{
        Scanner(System.`in`).use { scanner ->
            var result: String

            println(prompt)

            do{
                result = scanner.nextLine().trim()
                if(result.isBlank())
                    println(errorMsg)
            }while(result.isBlank())

            return result
        }
    }
    fun readValidIntegerInput(prompt: String, errorMsg: String): Int{
        Scanner(System.`in`).use { scanner ->
            println(prompt)

            while(!scanner.hasNextInt()){
                println(errorMsg)
                scanner.nextLine()
            }

            val result: Int = scanner.nextInt()
            scanner.nextLine()

            return result
        }
    }
}