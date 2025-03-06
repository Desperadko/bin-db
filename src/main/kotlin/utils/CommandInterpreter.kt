package org.example.utils

import org.example.app_logic.Employee.EmployeeManager

object CommandInterpreter{
    private val commands: Map<String, (List<String>) -> Unit> = mapOf(
        "add" to ::handleAddCommand,
        "rmv" to ::handleRemoveCommand,
        "upt" to ::handleUpdateCommand,
        "dsp" to ::handleDisplayCommand
    )

    private fun dissectInput(input: String): MutableList<String>{
        val inputDissected: MutableList<String> = mutableListOf()

        val sb: StringBuilder = StringBuilder()

        for(i in input.indices)
        {
            if(input[i] == ' ' && !sb.contains('"')){
                inputDissected.add(sb.toString())
                sb.clear()
                continue
            }
            else if(input[i] == '"' && sb.contains('"')){
                val filtered = sb.filter { it != '"' }
                inputDissected.add(filtered.toString())
                sb.clear()
                continue
            }

            sb.append(input[i])
        }

        if(sb.isNotEmpty())
            inputDissected.add(sb.toString())

        return inputDissected
    }

    private fun handleAddCommand(args: List<String>){
        if(args.count() < 3)
        {
            println("Insufficient amount of arguments given to add an entity")
            return
        }

        when(args[1]){
            "emp" -> EmployeeManager.processAddCommand(args[2])
            else -> {
                println("No such entity type '${args[1]}'")
                return
            }
        }
    }
    private fun handleRemoveCommand(args: List<String>){
        if(args.count() < 3)
        {
            println("Insufficient amount of arguments given to remove an entity")
            return
        }

        when(args[1]){
            "emp" -> EmployeeManager.processRemoveCommand(args[3])
            else -> {
                println("No such entity type '${args[1]}'")
                return
            }
        }
    }
    private fun handleUpdateCommand(args: List<String>){
        if(args.count() < 3)
        {
            println("Insufficient amount of arguments given to update an entity")
            return
        }

        when(args[1]){
            "emp" -> EmployeeManager.processUpdateCommand(args[2], args[3], args[4])
            else -> {
                println("No such entity type '${args[1]}'")
                return
            }
        }
    }
    private fun handleDisplayCommand(args: List<String>){
        if(args.count() < 2)
        {
            println("Insufficient amount of arguments given to display entities")
            return
        }

        when(args[1]){
            "emp" -> EmployeeManager.processDisplayCommand()
            else -> {
                println("No such entity type '${args[1]}'")
                return
            }
        }
    }

    fun processCommand(userInput: String){
        val inputDissected = dissectInput(userInput)

        val command = inputDissected.firstOrNull()
        if(command != null && commands.contains(command))
            commands[command]?.invoke(inputDissected)
        else
            println("No such command '$command' exists")
    }
}