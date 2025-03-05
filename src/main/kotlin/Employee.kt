package org.example

class Employee(name: String, employeeIndex: Int? = null){
    var employeeIndex: Int = employeeIndex ?: CURRENT_INDEX++
    var employeeName: String = if(name.length <= MAX_NAME_SIZE) name else throw Error("Name is too long..")

    constructor(employeeIndex: Int, name: String) : this(name, employeeIndex)

    companion object{
        const val MAX_NAME_SIZE: Int = 60
        var CURRENT_INDEX: Int = 0
        const val EMPLOYEE_SIZE: Int = Int.SIZE_BYTES * 1 + MAX_NAME_SIZE
    }

    override fun toString(): String {
        return "$employeeIndex: $employeeName"
    }
}