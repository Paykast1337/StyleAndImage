package com.example.styleandimage

import android.app.DatePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.styleandimage.DataClasses.DataClassRegistr
import com.example.styleandimage.SupportFunctions.SharedPreference
import java.sql.DriverManager
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class Records : AppCompatActivity() {
    var employee = "Выбрать мастера"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_records)


        val sharedPreference = SharedPreference(this) //переменная для определения БД для маленького объема данных
        if (sharedPreference.getValueString("phone") != "83833806514"){
            val spinner = findViewById<ConstraintLayout>(R.id.spinnerRecordsConstraintLayout2)
            val dateEditText = findViewById<EditText>(R.id.dateRecordsEditText)
            val buttonOk = findViewById<Button>(R.id.okRecordsButton)
            val tableEmployee = findViewById<TableLayout>(R.id.recordsTableLayout)
            val tableClient = findViewById<TableLayout>(R.id.recordsClientTableLayout)
            spinner.visibility = View.GONE
            dateEditText.visibility = View.GONE
            buttonOk.visibility = View.GONE
            tableEmployee.visibility = View.GONE
            tableClient.visibility = View.VISIBLE

            println(sharedPreference.getValueString("phone").toString())

            val listRecordsClient = connectRecordsClient(sharedPreference.getValueString("phone").toString())
            for (record in listRecordsClient)
            {
                if (record.date.length == 10){
                    val day = record.date.get(0).toString() + record.date.get(1).toString()
                    val munth = record.date.get(3).toString() + record.date.get(4).toString()
                    val year = record.date.get(8).toString() + record.date.get(9).toString()

                    val time = Calendar.getInstance().time
                    val formatter = SimpleDateFormat("dd.MM.yyyy")
                    val current = formatter.format(time)

                    val dayNow = current.get(0).toString() + current.get(1).toString()
                    val munthNow = current.get(3).toString() + current.get(4).toString()
                    val yearNow = current.get(8).toString() + current.get(9).toString()

                    if (year.toInt() > yearNow.toInt()){
                        println("По году")
                        addRowClients(record.time.toString(),record.date.toString(), record.employee.toString(), record.price_name.toString())
                    }
                    else if (munth.toInt() > munthNow.toInt()){
                        println("По месяцу")
                        addRowClients(record.time.toString(),record.date.toString(), record.employee.toString(), record.price_name.toString())
                    }
                    else if (day.toInt() > dayNow.toInt()){
                        println("По дню")
                        addRowClients(record.time.toString(),record.date.toString(), record.employee.toString(), record.price_name.toString())
                    }
                    else if (year.toInt() == yearNow.toInt() && munth.toInt() == munthNow.toInt() && day.toInt() == dayNow.toInt()){
                        println("Равность")
                        addRowClients(record.time.toString(),record.date.toString(), record.employee.toString(), record.price_name.toString())
                    }
                }
            }
        }

        val date= findViewById<EditText>(R.id.dateRecordsEditText)

        date.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                date.setText(SimpleDateFormat("dd.MM.yyyy").format(cal.time))
            }

            val dialog = DatePickerDialog(this,R.style.MySpinnerDatePickerStyle, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            dialog.show()
        }
        val spinner= findViewById<Spinner>(R.id.employeeRecordsSpinner)
        val mList = arrayOf<String?>("Выбрать мастера","Васильева О.В.", "Неля", "Марина")
        val mArrayAdapter = ArrayAdapter<Any?>(this, R.layout.spinner_style, mList)
        mArrayAdapter.setDropDownViewResource(R.layout.spinner_style)
        spinner.adapter = mArrayAdapter
        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener { // если изменяется выбранное
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) { //то
                employee = parentView?.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        })
    }

    fun clickOk(view: View) {
        val date= findViewById<EditText>(R.id.dateRecordsEditText)
        val error = findViewById<TextView>(R.id.errorRecordsTextView)

        if (date.text.toString() != "" && employee != "Выбрать мастера") {
            val listRecords = connectRecordsEmployee(employee, "employee", date.text.toString(), "date")
            for (record in listRecords)
            {
                addRow(record.time.toString(), record.klients.toString(), record.price_name.toString())
            }
        }
        else {error.text = "Ошибка! Вы пропустили поле!"}
    }

    fun connectRecordsEmployee( meaning1: String, sorter1: String, meaning2: String, sorter2: String) :MutableList<DataClassRegistr>{ //Функция записи результатов функции подключения к БД
        val listRegistr: MutableList<DataClassRegistr> = mutableListOf() //создание списка
        val user = "fan651sh_sai" //Имя пользователя БД
        val pass = "Dobro1337//" //Пароль к БД
        val url = "jdbc:mysql://fan651sh.beget.tech:3306/fan651sh_sai"
        thread { //Создание потока
            try { //попробовать
                Class.forName("com.mysql.jdbc.Driver")
                val connection = DriverManager.getConnection(url, user, pass) // подключение к БД
                val search = "SELECT * FROM records " //Составленеи запроса к БД к определенной таблице
                val searchWhere = "WHERE $sorter1 LIKE ('%$meaning1%') AND $sorter2 LIKE ('%$meaning2%')" //сортировка с условием
                val searchQuery = connection.prepareStatement(search+searchWhere+";")  // выборка всей таблицы
                val result = searchQuery.executeQuery() // Запись выборки БД в переменную result

                while (result.next()) { //цикл пока есть записи в result (построчно)
                    val id: Int = result.getInt(1) // запись значения id в переменную
                    val date: String = result.getString(2) // запись значения даты в переменную
                    val time: String = result.getString(3) // запись значения времени в переменнуюe)
                    val klient: String = result.getString(4) // запись значения фио клиента в переменную
                    val number: Long = result.getLong(5) // запись значения стоимости в переменную
                    val employee: String = result.getString(6) // запись значения фио мастера в переменную
                    val name_price: String = result.getString(7) // запись значения услуги в переменную
                    val price: Int = result.getInt(8) // запись значения стоимости в переменную
                    val stringRegistr = DataClassRegistr(id, date, time, klient, number.toString(), employee, name_price, price) //Добавление переменной типа DataClassRegistr с значениями из result
                    listRegistr.add(stringRegistr) //Добавление переменной в список
                    println(stringRegistr)
                }
            } catch (e: Exception) { //Если не получилось то отлавливаем ошибки и выводим их в консоль
                print(e.message)
            }
        }.join() //Отвечает за приостановку основного потока до выполнения этого
        return listRegistr //Вернуть список с значениями
    }

    fun addRow(time: String, klient: String, name_price: String) { //функция добавления строки таблицы и загрузки в нее данных
        val tableLayout = findViewById<TableLayout>(R.id.recordsTableLayout) //Обозначаем Tablelayout для использования
        val tableRow = TableRow(this) //Добавление переменной типа TableRow, отвечает за целую строку

        val textView1 = TextView(this)//Добавление переменной типа TextView, находится в TableRow и отвечает ха ячейку
        textView1.setText(time) //установка текста в новый TextView
        textView1.setTextColor(Color.parseColor("#8E24AA"))
        textView1.setBackgroundColor(Color.parseColor("#FFFFFF")) //Установка фона в новый TextView
        textView1.setPadding(4,4,4,4) // установка внутренних отступов в новый TextView
        textView1.setTextSize(15F) // установка размера текста в новый TextView
        textView1.gravity = Gravity.CENTER // привязка по центру для нового TextView
        textView1.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT) //изменение ширины и высоты
        (textView1.layoutParams as TableRow.LayoutParams).apply { //Блок настройки отображения виджета
            weight=1F //?
            column=0 // столбец в котором будет находиться новый TextView
            marginStart=4 // отступ слева
            topMargin=4 // отступ сверху
            marginEnd=4 // отступ справа
            bottomMargin=4 // отступ снизу
        }

        val textView2 = TextView(this) //создание следующего textview, аналогично первому
        textView2.setText(klient)
        textView2.setTextColor(Color.parseColor("#8E24AA"))
        textView2.setBackgroundColor(Color.parseColor("#FFFFFF"))
        textView2.setPadding(4,4,4,4)
        textView2.setTextSize(15F)
        textView2.gravity = Gravity.CENTER
        textView2.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        (textView2.layoutParams as TableRow.LayoutParams).apply {
            weight=12F
            column = 1
            marginStart=4
            topMargin=4
            marginEnd=4
            bottomMargin=4
        }

        val textView3 = TextView(this) //создание следующего textview, аналогично первому
        textView3.setText(name_price)
        textView3.setTextColor(Color.parseColor("#8E24AA"))
        textView3.setBackgroundColor(Color.parseColor("#FFFFFF"))
        textView3.setPadding(4,4,4,4)
        textView3.setTextSize(15F)
        textView3.gravity = Gravity.CENTER
        textView3.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        (textView3.layoutParams as TableRow.LayoutParams).apply {
            weight=9F
            column = 2
            marginStart=4
            topMargin=4
            marginEnd=4
            bottomMargin=4
        }

        tableRow.addView(textView1) //Добавление textView1 в tableRow
        tableRow.addView(textView2) //Добавление textView2 в tableRow
        tableRow.addView(textView3) //Добавление textView3 в tableRow
        tableLayout?.addView(tableRow) //Добавление tableRow в TableLayout
    }

    fun connectRecordsClient( number: String) :MutableList<DataClassRegistr>{ //Функция записи результатов функции подключения к БД
        val listRegistr: MutableList<DataClassRegistr> = mutableListOf() //создание списка
        val user = "fan651sh_sai" //Имя пользователя БД
        val pass = "Dobro1337//" //Пароль к БД
        val url = "jdbc:mysql://fan651sh.beget.tech:3306/fan651sh_sai"
        thread { //Создание потока
            try { //попробовать
                Class.forName("com.mysql.jdbc.Driver")
                val connection = DriverManager.getConnection(url, user, pass) // подключение к БД
                val search = "SELECT * FROM records " //Составленеи запроса к БД к определенной таблице
                val searchWhere = "WHERE number LIKE ('%$number%')" //сортировка с условием
                val searchQuery = connection.prepareStatement(search+searchWhere+";")  // выборка всей таблицы
                val result = searchQuery.executeQuery() // Запись выборки БД в переменную result

                while (result.next()) { //цикл пока есть записи в result (построчно)
                    val id: Int = result.getInt(1) // запись значения id в переменную
                    val date: String = result.getString(2) // запись значения даты в переменную
                    val time: String = result.getString(3) // запись значения времени в переменнуюe)
                    val klient: String = result.getString(4) // запись значения фио клиента в переменную
                    val number: Long = result.getLong(5) // запись значения стоимости в переменную
                    val employee: String = result.getString(6) // запись значения фио мастера в переменную
                    val name_price: String = result.getString(7) // запись значения услуги в переменную
                    val price: Int = result.getInt(8) // запись значения стоимости в переменную
                    val stringRegistr = DataClassRegistr(id, date, time, klient, number.toString(), employee, name_price, price) //Добавление переменной типа DataClassRegistr с значениями из result
                    listRegistr.add(stringRegistr) //Добавление переменной в список
                    println(stringRegistr)
                }
            } catch (e: Exception) { //Если не получилось то отлавливаем ошибки и выводим их в консоль
                print(e.message)
            }
        }.join() //Отвечает за приостановку основного потока до выполнения этого
        return listRegistr //Вернуть список с значениями
    }

    fun addRowClients(time: String, date: String, employee: String, name_price: String) { //функция добавления строки таблицы и загрузки в нее данных
        val tableLayout = findViewById<TableLayout>(R.id.recordsClientTableLayout) //Обозначаем Tablelayout для использования
        val tableRow = TableRow(this) //Добавление переменной типа TableRow, отвечает за целую строку

        val textView1 = TextView(this)//Добавление переменной типа TextView, находится в TableRow и отвечает ха ячейку
        textView1.setText("$date \n $time") //установка текста в новый TextView
        textView1.setTextColor(Color.parseColor("#8E24AA"))
        textView1.setBackgroundColor(Color.parseColor("#FFFFFF")) //Установка фона в новый TextView
        textView1.setPadding(4,4,4,4) // установка внутренних отступов в новый TextView
        textView1.setTextSize(15F) // установка размера текста в новый TextView
        textView1.gravity = Gravity.CENTER // привязка по центру для нового TextView
        textView1.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT) //изменение ширины и высоты
        (textView1.layoutParams as TableRow.LayoutParams).apply { //Блок настройки отображения виджета
            weight=1F //?
            column=0 // столбец в котором будет находиться новый TextView
            marginStart=4 // отступ слева
            topMargin=4 // отступ сверху
            marginEnd=4 // отступ справа
            bottomMargin=4 // отступ снизу
        }

        val textView2 = TextView(this) //создание следующего textview, аналогично первому
        textView2.setText(employee)
        textView2.setTextColor(Color.parseColor("#8E24AA"))
        textView2.setBackgroundColor(Color.parseColor("#FFFFFF"))
        textView2.setPadding(4,4,4,4)
        textView2.setTextSize(15F)
        textView2.gravity = Gravity.CENTER
        textView2.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        (textView2.layoutParams as TableRow.LayoutParams).apply {
            weight=12F
            column = 1
            marginStart=4
            topMargin=4
            marginEnd=4
            bottomMargin=4
        }

        val textView3 = TextView(this) //создание следующего textview, аналогично первому
        textView3.setText(name_price)
        textView3.setTextColor(Color.parseColor("#8E24AA"))
        textView3.setBackgroundColor(Color.parseColor("#FFFFFF"))
        textView3.setPadding(4,4,4,4)
        textView3.setTextSize(15F)
        textView3.gravity = Gravity.CENTER
        textView3.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        (textView3.layoutParams as TableRow.LayoutParams).apply {
            weight=9F
            column = 2
            marginStart=4
            topMargin=4
            marginEnd=4
            bottomMargin=4
        }

        tableRow.addView(textView1) //Добавление textView1 в tableRow
        tableRow.addView(textView2) //Добавление textView2 в tableRow
        tableRow.addView(textView3) //Добавление textView3 в tableRow
        tableLayout?.addView(tableRow) //Добавление tableRow в TableLayout
    }
}