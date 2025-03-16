package org.example

import org.example.app_logic.Employee.Employee
import org.example.app_logic.Employee.EmployeeDto
import org.example.app_logic.Employee.EmployeeRepository
import org.example.utils.CommandInterpreter
import org.example.utils.InputValidator

fun main() {
    CommandInterpreter.processCommand("hlp")
    var input: String
    while(true){
        input = InputValidator.readValidStringInput("user: ", "Cannot accept blank input.")
        CommandInterpreter.processCommand(input)
    }
}
