package org.example.utils

import java.util.Scanner

object InputValidator {
    private val scanner: Scanner = Scanner(System.`in`)

    fun readValidStringInput(prompt: String, errorMsg: String): String{
        var result: String

        print(prompt)

        do{
            result = scanner.nextLine().trim()
            if(result.isBlank())
                println(errorMsg)
        }while(result.isBlank())

        return result
    }
    fun readValidIntegerInput(prompt: String, errorMsg: String): Int{
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