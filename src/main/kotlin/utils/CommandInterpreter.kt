package org.example.utils

import org.example.app_logic.Employee.EmployeeService

object CommandInterpreter{
    const val HELP_PROMPT: String =
        "Current commands:\n" +
                "hlp - View which entities have implemented commands\n" +
                "hlp emp - View Employee commands\n" +
                "WIP.."

    private val commands: Map<String, (List<String>) -> Unit> = mapOf(
        "add" to ::handleAddCommand,
        "rmv" to ::handleRemoveCommand,
        "upd" to ::handleUpdateCommand,
        "dsp" to ::handleDisplayCommand,
        "hlp" to ::handleHelpCommand
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

        val commandSpecificArgs = args.drop(2)

        when(args[1]){
            "emp" -> EmployeeService.processAddCommand(commandSpecificArgs)
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

        val commandSpecificArgs = args.drop(2)

        when(args[1]){
            "emp" -> EmployeeService.processRemoveCommand(commandSpecificArgs)
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

        val commandSpecificArgs = args.drop(2)

        when(args[1]){
            "emp" -> EmployeeService.processUpdateCommand(commandSpecificArgs)
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
            "emp" -> EmployeeService.processDisplayCommand()
            else -> {
                println("No such entity type '${args[1]}'")
                return
            }
        }
    }
    private fun handleHelpCommand(args: List<String>){
        if(args.count() > 2)
        {
            println("Too much arguments given. Try 'hlp'.")
            return
        }

        when(args.lastOrNull()?.lowercase()){
            "hlp" -> displayCommands()
            "emp" -> EmployeeService.displayCommands()
            else -> println("No such entity exists. Try 'hlp'.")
        }
    }

    private fun displayCommands(){
        println(HELP_PROMPT)
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