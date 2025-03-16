package org.example.app_logic.Employee

import java.io.File
import java.io.RandomAccessFile
import java.nio.charset.Charset

object EmployeeRepository{
    private val employeesFile: RandomAccessFile

    private val blockToEmployeeMap: MutableMap<Long, Employee>
    private val employeeIndexToBlockMap: MutableMap<Int, Long>
    private val recentlyFreedPosition: ArrayDeque<Long>

    private const val OUTPUT_FOLDER: String = "outputFolder"
    private const val OUTPUT_FILE: String = "employees"

    init {
        val folder = File(OUTPUT_FOLDER);
        if(!folder.exists())
            folder.mkdirs()

        val file = File(folder, OUTPUT_FILE)
        employeesFile = RandomAccessFile(file, "rw")

        blockToEmployeeMap = mutableMapOf()
        employeeIndexToBlockMap = mutableMapOf()
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
            employeeIndexToBlockMap[employeeIndex] = blockIndex
        }
    }

    fun addEmployee(employee: Employee){
        val writePosition: Long =
            if(recentlyFreedPosition.isNotEmpty()) recentlyFreedPosition.removeFirst() * (Employee.EMPLOYEE_SIZE + Long.SIZE_BYTES)
            else employeesFile.length()

        val blockIndex: Long = writePosition / (Employee.EMPLOYEE_SIZE + Long.SIZE_BYTES)

        blockToEmployeeMap[blockIndex] = employee
        employeeIndexToBlockMap[employee.employeeIndex] = blockIndex

        employeesFile.seek(writePosition)
        writeEmployeeBlockIndex(blockIndex, writePosition)
        writeEmployeeIndex(employee, employeesFile.filePointer)
        writeEmployeeName(employee, employeesFile.filePointer)
    }
    fun removeEmployee(employeeIndex: Int){
        val employeesBlock = employeeIndexToBlockMap[employeeIndex]
            ?: throw Exception("Employee with index: $employeeIndex does not exist..")

        recentlyFreedPosition.addLast(employeesBlock)

        blockToEmployeeMap.remove(employeesBlock)
        employeeIndexToBlockMap.remove(employeeIndex)

        employeesFile.seek(employeesBlock * (Employee.EMPLOYEE_SIZE + Long.SIZE_BYTES))
        val emptyBytes = ByteArray(Employee.EMPLOYEE_SIZE + Long.SIZE_BYTES){ 0xFF.toByte() }
        employeesFile.write(emptyBytes)
    }
    fun updateEmployee(employeeId: Int, employeeDto: EmployeeDto){
        if(employeeId != employeeDto.index && employeeIndexToBlockMap[employeeDto.index] != null)
            throw Exception("Employee with ID: ${employeeDto.index} already exists..")

        val blockIndex = employeeIndexToBlockMap[employeeId] ?:
            throw Exception("Employee with ID: $employeeId does not exist..")

        val employee = blockToEmployeeMap[blockIndex] ?:
            throw Exception("No employee was found in block: $blockIndex ..")

        employee.employeeIndex = employeeDto.index
        employee.employeeName = employeeDto.name

        val writePosition = blockIndex * (Employee.EMPLOYEE_SIZE + Long.SIZE_BYTES) + Long.SIZE_BYTES
        writeEmployeeIndex(employee, writePosition)
        writeEmployeeName(employee, employeesFile.filePointer)
    }
    fun displayEmployees(){
        for(e in blockToEmployeeMap.values)
            println(e)
    }

    private fun writeEmployeeBlockIndex(blockIndex: Long, writePosition: Long) {
        employeesFile.seek(writePosition)
        employeesFile.writeLong(blockIndex)
    }
    private fun writeEmployeeIndex(employee: Employee, writePosition: Long) {
        employeesFile.seek(writePosition)
        employeesFile.writeInt(employee.employeeIndex)
    }
    private fun writeEmployeeName(employee: Employee, writePosition: Long) {
        employeesFile.seek(writePosition)
        val nameInBytes = employee.employeeName.toByteArray(Charset.defaultCharset())
        val paddedNameToMax = nameInBytes.copyOf(Employee.MAX_NAME_SIZE)
        employeesFile.write(paddedNameToMax)
    }

    fun close(){
        employeesFile.close()
    }
}