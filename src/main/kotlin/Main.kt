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
//    TODO("CLI DONE IN A WHILE(TRUE) LOOP - WIP")
//    TODO("PROMPT WITH EVERY COMMAND - 1.GET 2.ADD ..")
//    TODO("DONE LIKE NORMAL -> add --emp(loyee) `<employeeName>`")
//    TODO("THIS WAY WILL BE LEFT ROOM FOR POTENTIAL -> add --dep(artment) `<departmentName>`")
}
