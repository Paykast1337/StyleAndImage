package com.example.styleandimage

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.styleandimage.DataClasses.DataClassRegistr
import com.example.styleandimage.SupportFunctions.SharedPreference
import java.sql.DriverManager
import kotlin.concurrent.thread

class History : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        visibility()

        val dataClientEditText = findViewById<TextView>(R.id.dataHistoryEditText)
        val strUser: String? = intent.getStringExtra("User")
        dataClientEditText.setText(strUser)
        if(dataClientEditText.text.toString() != ""){
            clickOk(dataClientEditText)
        }
    }


    fun visibility(){
        val sharedPreference = SharedPreference(this) //переменная для определения БД для маленького объема данных
        if (sharedPreference.getValueString("phone") != "83833806514") {
            val dataClientTextView = findViewById<TextView>(R.id.dataHistoryTextView)
            val dataClientEditText = findViewById<TextView>(R.id.dataHistoryEditText)
            val buttonOk = findViewById<Button>(R.id.okHistoryButton)
            val historyTableLayout = findViewById<TableLayout>(R.id.historyTableLayout)
            val historyClientTableLayout = findViewById<TableLayout>(R.id.historyClientTableLayout)
            dataClientTextView.visibility = View.GONE
            buttonOk.visibility = View.GONE
            dataClientEditText.visibility = View.GONE
            historyTableLayout.visibility = View.GONE
            historyClientTableLayout.visibility = View.VISIBLE

            addTableClient(sharedPreference.getValueString("phone").toString())
        }
    }

    fun addTableClient(number:String){
        cleanTable()
        val listData: MutableList<DataClassRegistr> = connectRecords("records", number, "number") //Запуск функции подключения к БД и таблицы price, который возвращает лист с данными из таблицы
        for (registr in listData) //Цикл перебора списка с данными
        {// для каждой записи таблицы запускается функция добавления строки таблицы и загрузки в нее данных
            addRowClient(registr.date.toString(), registr.price_name, registr.price.toString())
        }
    }

    fun clickOk(view:View) {
        val editTextTextPersonName2 = findViewById<EditText>(R.id.dataHistoryEditText)
        val textViewDataKlients = findViewById<TextView>(R.id.dataClientHistoryTextView)
        var sorted = "klient"
        if (editTextTextPersonName2.text.toString().get(0) == '8'){ sorted = "number" }
        else { sorted = "klient" }
        cleanTable()
        val listData: MutableList<DataClassRegistr> = connectRecords("records", editTextTextPersonName2.text.toString(), sorted) //Запуск функции подключения к БД и таблицы price, который возвращает лист с данными из таблицы
        textViewDataKlients.text = ""
        var clientName = ""
        for (registr in listData){ //Цикл перебора списка с данными// для каждой записи таблицы запускается функция добавления строки таблицы и загрузки в нее данных
            if (clientName != registr.klients){
                textViewDataKlients.text = "${textViewDataKlients.text} ${registr.klients} (${registr.number}) \n"
            }
            addRow(registr.date.toString(), registr.price_name, registr.price.toString())
            clientName = registr.klients
        }
    }

    fun cleanTable() {
        val tableLayout = findViewById<TableLayout>(R.id.historyTableLayout)
        val childCount = tableLayout.childCount

        // Remove all rows except the first one
        if (childCount > 1) {
            tableLayout.removeViews(1, childCount - 1)
        }
    }

    fun connectRecords(nameTable: String, meaning: String, sorter: String) :MutableList<DataClassRegistr>{ //Функция записи результатов функции подключения к БД
        val listRegistr: MutableList<DataClassRegistr> = mutableListOf() //создание списка
        val user = "fan651sh_sai" //Имя пользователя БД
        val pass = "Dobro1337//" //Пароль к БД
        val url = "jdbc:mysql://fan651sh.beget.tech:3306/fan651sh_sai"
        thread { //Создание потока
            try { //попробовать
                Class.forName("com.mysql.jdbc.Driver")
                val connection = DriverManager.getConnection(url, user, pass) // подключение к БД
                val search = "SELECT * FROM $nameTable " //Составленеи запроса к БД к определенной таблице
                val searchWhere = "WHERE $sorter LIKE ('%$meaning%')" //сортировка с условием
                val result = if (sorter == "All"){ // если сортировка равна все то
                    val searchQuery = connection.prepareStatement(search+";")  // выборка всей таблицы
                     searchQuery.executeQuery() // Запись выборки БД в переменную result
                }
                else{ //иначе
                    val searchQuery = connection.prepareStatement(search+searchWhere+";")  // Выполнение выборки из БД с условием
                    searchQuery.executeQuery() // Запись выборки БД в переменную result
                }
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

    fun addRow(date: String, service: String, price: String) { //функция добавления строки таблицы и загрузки в нее данных
        val tableLayout = findViewById<TableLayout>(R.id.historyTableLayout) //Обозначаем Tablelayout для использования
        val tableRow = TableRow(this) //Добавление переменной типа TableRow, отвечает за целую строку

        val textView1 = TextView(this)//Добавление переменной типа TextView, находится в TableRow и отвечает ха ячейку
        textView1.setText(date) //установка текста в новый TextView
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
        textView2.setText(service)
        textView2.setTextColor(Color.parseColor("#8E24AA"))
        textView2.setBackgroundColor(Color.parseColor("#FFFFFF"))
        textView2.setPadding(4,4,4,4)
        textView2.setTextSize(15F)
        textView2.gravity = Gravity.CENTER
        textView2.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        (textView2.layoutParams as TableRow.LayoutParams).apply {
            weight=8F
            column = 1
            marginStart=4
            topMargin=4
            marginEnd=4
            bottomMargin=4
        }

        val textView3 = TextView(this) //создание следующего textview, аналогично первому
        textView3.setText(price)
        textView3.setTextColor(Color.parseColor("#8E24AA"))
        textView3.setBackgroundColor(Color.parseColor("#FFFFFF"))
        textView3.setPadding(4,4,4,4)
        textView3.setTextSize(15F)
        textView3.gravity = Gravity.CENTER
        textView3.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        (textView3.layoutParams as TableRow.LayoutParams).apply {
            weight=4F
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

    fun addRowClient(date: String, service: String, price: String) { //функция добавления строки таблицы и загрузки в нее данных
        val tableLayout = findViewById<TableLayout>(R.id.historyClientTableLayout) //Обозначаем Tablelayout для использования
        val tableRow = TableRow(this) //Добавление переменной типа TableRow, отвечает за целую строку

        val textView1 = TextView(this)//Добавление переменной типа TextView, находится в TableRow и отвечает ха ячейку
        textView1.setText(date) //установка текста в новый TextView
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
        textView2.setText(service)
        textView2.setTextColor(Color.parseColor("#8E24AA"))
        textView2.setBackgroundColor(Color.parseColor("#FFFFFF"))
        textView2.setPadding(4,4,4,4)
        textView2.setTextSize(15F)
        textView2.gravity = Gravity.CENTER
        textView2.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        (textView2.layoutParams as TableRow.LayoutParams).apply {
            weight=8F
            column = 1
            marginStart=4
            topMargin=4
            marginEnd=4
            bottomMargin=4
        }

        val textView3 = TextView(this) //создание следующего textview, аналогично первому
        textView3.setText(price)
        textView3.setTextColor(Color.parseColor("#8E24AA"))
        textView3.setBackgroundColor(Color.parseColor("#FFFFFF"))
        textView3.setPadding(4,4,4,4)
        textView3.setTextSize(15F)
        textView3.gravity = Gravity.CENTER
        textView3.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        (textView3.layoutParams as TableRow.LayoutParams).apply {
            weight=4F
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