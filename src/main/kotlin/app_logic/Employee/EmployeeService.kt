package org.example.app_logic.Employee

object EmployeeService{
    const val HELP_COMMANDS: String =
        "Current Employee commands:\n" +
                "add emp \"<employeeName>\" - Add employee\n" +
                "rmv emp <employeeId> - Remove employee\n" +
                "upd emp <currentEmployeeId> <newEmployeeId> \"<newEmployeeName>\" - Update employee\n" +
                "dsp emp - Display all employees\n"

    fun processAddCommand(args: List<String>){
        if(args.count() != 1)
            throw Exception("Given arguments were insufficient/more than needed.")

        val employeeName = args[0]

        if(employeeName.isBlank())
            throw Exception("Given argument was blank.")

        if(employeeName.any { (!it.isLetter() && !it.isWhitespace()) } )
            throw Exception("Given Employee name should not contain anything other than letters and spaces.")

        EmployeeRepository.addEmployee(Employee(employeeName))
    }
    fun processRemoveCommand(args: List<String>) {
        if(args.count() != 1)
            throw Exception("Given arguments were insufficient/more than needed.")

        val employeeId = args[0].toIntOrNull() ?:
            throw Exception("Given argument was blank or invalid.")

        if(employeeId < 0)
            throw Exception("Given ID must be a positive number.")

        EmployeeRepository.removeEmployee(employeeId)
    }
    fun processUpdateCommand(args: List<String>) {
        if(args.count() != 3)
            throw Exception("Given arguments were insufficient/more than needed.")

        if(args.any { it.isBlank() } )
            throw Exception("From the given arguments, at least one is blank")

        val employeeId = args[0].toIntOrNull() ?:
            throw Exception("Given ID was invalid.")

        if(employeeId < 0)
            throw Exception("Given ID must be a positive number.")

        val newEmployeeId = args[1].toIntOrNull() ?:
            throw Exception("Given new ID was invalid.")

        if(newEmployeeId < 0)
            throw Exception("Given new ID must be a positive number.")

        if(args[2].any { !it.isLetter() && !it.isWhitespace() } )
            throw Exception("Given new Employee name should not contain anything other than letters and spaces.")

        val employeeNewName = args[2]

        EmployeeRepository.updateEmployee(employeeId, EmployeeDto(newEmployeeId, employeeNewName))
    }
    fun processDisplayCommand() {
        EmployeeRepository.displayEmployees()
    }
    fun displayCommands(){
        println(HELP_COMMANDS)
    }
}