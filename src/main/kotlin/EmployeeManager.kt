package org.example

import java.io.File
import java.io.RandomAccessFile
import java.nio.charset.Charset

data class EmployeeManager(val fileName: String) {
    private val employeesFile: RandomAccessFile

    private val blockToEmployeeMap: MutableMap<Long, Employee>
    private val indexToBlockMap: MutableMap<Int, Long>
    private val recentlyFreedPosition: ArrayDeque<Long>

    companion object{
        const val OUTPUT_FOLDER: String = "./OutputFolder"
    }

    init {
        val folder = File(OUTPUT_FOLDER);
        if(!folder.exists())
            folder.mkdirs()

        val file = File(folder, fileName)
        employeesFile = RandomAccessFile(file, "rw")

        blockToEmployeeMap = mutableMapOf()
        indexToBlockMap = mutableMapOf()
        recentlyFreedPosition = ArrayDeque()
        if(file.exists()){
            loadLists()
        }

        val lastIndex = blockToEmployeeMap.maxOfOrNull { e -> e.value.employeeIndex }
        Employee.CURRENT_INDEX = if(lastIndex != null) lastIndex + 1 else 0
    }

    private fun loadLists() {
        for(i in 0 until employeesFile.length() / Employee.EMPLOYEE_SIZE)
        {
            employeesFile.seek(i * (Employee.EMPLOYEE_SIZE + Long.SIZE_BYTES))

            val blockIndex = employeesFile.readLong()
            if(blockIndex == - 1L)
            {
                recentlyFreedPosition.addLast(i)
                continue
            }

            val employeeIndex = employeesFile.readInt()

            val buffer = ByteArray(Employee.MAX_NAME_SIZE)
            employeesFile.readFully(buffer)
            val employeeName = buffer
                .filter{it != 0x00.toByte()}
                .toByteArray()
                .toString(Charset.defaultCharset())

            blockToEmployeeMap[blockIndex] = Employee(employeeIndex, employeeName)
            indexToBlockMap[employeeIndex] = blockIndex
        }
    }

    fun addEmployee(employee: Employee){
        val writePosition: Long =
            if(recentlyFreedPosition.isNotEmpty()) recentlyFreedPosition.removeFirst() * (Employee.EMPLOYEE_SIZE + Long.SIZE_BYTES)
            else employeesFile.length()

        val blockIndex: Long = writePosition / (Employee.EMPLOYEE_SIZE + Long.SIZE_BYTES)

        blockToEmployeeMap[blockIndex] = employee
        indexToBlockMap[employee.employeeIndex] = blockIndex

        employeesFile.seek(writePosition)
        employeesFile.writeLong(blockIndex)
        employeesFile.writeInt(employee.employeeIndex)
        val nameInBytes = employee.employeeName.toByteArray(Charset.defaultCharset())
        val paddedNameToMax = nameInBytes.copyOf(Employee.MAX_NAME_SIZE)
        employeesFile.write(paddedNameToMax)
    }
    fun removeEmployee(employeeIndex: Int){
        val employeesBlock = indexToBlockMap[employeeIndex]
            ?: throw Exception("Employee with index: $employeeIndex does not exist..")

        recentlyFreedPosition.addLast(employeesBlock)

        blockToEmployeeMap.remove(employeesBlock)
        indexToBlockMap.remove(employeeIndex)

        employeesFile.seek(employeesBlock * (Employee.EMPLOYEE_SIZE + Long.SIZE_BYTES))
        val emptyBytes = ByteArray(Employee.EMPLOYEE_SIZE + Long.SIZE_BYTES){ 0xFF.toByte() }
        employeesFile.write(emptyBytes)
    }
    fun updateEmployee(employeeId: Int, employeeDto: EmployeeDto){
        val blockIndex = indexToBlockMap[employeeId] ?:
            throw Exception("Employee with id: $employeeId does not exist..")

        val employee = blockToEmployeeMap[blockIndex] ?:
            throw Exception("No employee was found in block: $blockIndex ..")

        employee.employeeIndex = employeeDto.index
        employee.employeeName = employeeDto.name

        employeesFile.seek(blockIndex * (Employee.EMPLOYEE_SIZE + Long.SIZE_BYTES) + Long.SIZE_BYTES)
        employeesFile.writeInt(employee.employeeIndex)
        val nameInBytes = employee.employeeName.toByteArray(Charset.defaultCharset())
        val paddedNameToMax = nameInBytes.copyOf(Employee.MAX_NAME_SIZE)
        employeesFile.write(paddedNameToMax)
    }
    fun getEmployee(employeeIndex: Int): Employee {
        val blockIndex = indexToBlockMap[employeeIndex] ?:
            throw Exception("Employee with index: $employeeIndex does not exist..")
        return blockToEmployeeMap[blockIndex] ?:
            throw Exception("No employee was found in block $blockIndex ..")
    }
    fun displayEmployees(){
        for(e in blockToEmployeeMap.values)
            println(e)
    }
}