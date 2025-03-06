package org.example.app_logic.Employee

object EmployeeManager{
    fun processAddCommand(arg: String){
        EmployeeRepository.addEmployee(Employee(arg))
    }
    fun processRemoveCommand(arg: Int) {
        EmployeeRepository.removeEmployee(arg)
    }
    fun processUpdateCommand(arg1: Int, arg2: Int, arg3: String) {
        EmployeeRepository.updateEmployee(arg1, EmployeeDto(arg2, arg3))
    }
    fun processDisplayCommand() {
        EmployeeRepository.displayEmployees()
    }
}