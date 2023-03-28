package com.example.styleandimage

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.styleandimage.DataClasses.DataClassPrice
import java.sql.DriverManager
import java.sql.ResultSet
import kotlin.concurrent.thread


class Price : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_price)

        styleSpinner()
    }

    fun styleSpinner(){
        var sorted = ""
        val spinner = findViewById<Spinner>(R.id.typePriceSpinner) //Обозначаем выпадающий список для использования
        val mList = arrayOf<String?>("Все", "Мужской", "Женский", "Детский", "Маникюр", "Педикюр")
        val mArrayAdapter = ArrayAdapter<Any?>(this, R.layout.spinner_style, mList)
        mArrayAdapter.setDropDownViewResource(R.layout.spinner_style)
        spinner.adapter = mArrayAdapter

        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener { // если изменяется выбранное

            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) { //то
                if (parentView?.getItemAtPosition(position).toString() == "Все"){ sorted = "All" } // если выбранное значение из списка равно "Все" то сортировка будет равно алл
                else { sorted = "type" } // иначе сортировка равна type
                cleanTable() //функция очистки таблицы
                val listData: MutableList<DataClassPrice> = connectPrice(parentView?.getItemAtPosition(position).toString(), sorted) //Запуск функции подключения к БД и таблицы price, который возвращает лист с данными из таблицы
                for (price in listData) //Цикл перебора списка с данными
                {// для каждой записи таблицы запускается функция добавления строки таблицы и загрузки в нее данных
                    addRow(price.name, price.view, price.price.toString())
                }
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {//хз
                return
            }
        })

    }

     fun cleanTable() {
         val tableLayout = findViewById<TableLayout>(R.id.priceTableLayout)
         val childCount = tableLayout.childCount
         if (childCount > 1) {
             tableLayout.removeViews(1, childCount - 1)
        }
    }

    fun addRow(name: String, view: String, price: String) { //функция добавления строки таблицы и загрузки в нее данных
        val tableLayout = findViewById<TableLayout>(R.id.priceTableLayout) //Обозначаем Tablelayout для использования
        val tableRow = TableRow(this) //Добавление переменной типа TableRow, отвечает за целую строку
        val textView1 = TextView(this)//Добавление переменной типа TextView, находится в TableRow и отвечает ха ячейку
        textView1.setText(name) //установка текста в новый TextView
        textView1.setTextColor(Color.parseColor("#8E24AA"))
        textView1.setBackgroundColor(Color.parseColor("#FFFFFF")) //Установка фона в новый TextView
        textView1.setPadding(4,4,4,4) // установка внутренних отступов в новый TextView
        textView1.setTextSize(15F) // установка размера текста в новый TextView
        textView1.gravity = Gravity.CENTER // привязка по центру для нового TextView
        textView1.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT) //изменение ширины и высоты
        (textView1.layoutParams as TableRow.LayoutParams).apply { //Блок настройки отображения виджета
            weight=8F //?
            column=0 // столбец в котором будет находиться новый TextView
            marginStart=4 // отступ слева
            topMargin=4 // отступ сверху
            marginEnd=4 // отступ справа
            bottomMargin=4 // отступ снизу
        }

        val textView2 = TextView(this) //создание следующего textview, аналогично первому
        textView2.setText(price)
        textView2.setTextColor(Color.parseColor("#8E24AA"))
        textView2.setBackgroundColor(Color.parseColor("#FFFFFF"))
        textView2.setPadding(4,4,4,4)
        textView2.setTextSize(15F)
        textView2.gravity = Gravity.CENTER
        textView2.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        (textView2.layoutParams as TableRow.LayoutParams).apply {
            weight=4F
            column = 1
            marginStart=4
            topMargin=4
            marginEnd=4
            bottomMargin=4
        }

        tableRow.addView(textView1) //Добавление textView1 в tableRow
        tableRow.addView(textView2) //Добавление textView2 в tableRow
        tableLayout?.addView(tableRow) //Добавление tableRow в TableLayout
    }

    fun connectPrice(meaning: String, sorter: String) :MutableList<DataClassPrice>{ //Функция записи результатов функции подключения к БД
        val user = "fan651sh_sai" //Имя пользователя БД
        val pass = "Dobro1337//" //Пароль к БД
        val url = "jdbc:mysql://fan651sh.beget.tech:3306/fan651sh_sai" //Строка подключения (Библиотека_для_подключения_к_БД://ип:порт/Имя_бд)

        val listPrice: MutableList<DataClassPrice> = mutableListOf() //создание списка
        thread { //Создание потока
            try { //попробовать
                Class.forName("com.mysql.jdbc.Driver")
                val connection = DriverManager.getConnection(url, user, pass) // подключение к БД
                val search = "SELECT * FROM price " //Составленеи запроса к БД к определенной таблице
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
                    val name: String = result.getString(2) // запись значения Названия услуги в переменную
                    val view: String = result.getString(3) // запись значения Зала в переменную
                    val price: Int = result.getInt(4) // запись значения Цены в переменную
                    val stringPrice = DataClassPrice(id, name, view, price) //Добавление переменной типа DataClassPrice с значениями из result
                    listPrice.add(stringPrice) //Добавление переменной в список
                }
            } catch (e: Exception) { //Если не получилось то отлавливаем ошибки и выводим их в консоль
                print(e.message)
            }
        }.join() //Отвечает за приостановку основного потока до выполнения этого
        return listPrice //Вернуть список с значениями
    }
}