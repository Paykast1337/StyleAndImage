package com.example.styleandimage

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.styleandimage.DataClasses.DataClassPrice
import com.example.styleandimage.DataClasses.DataClassRegistr
import com.example.styleandimage.SupportFunctions.SharedPreference
import com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker
import java.sql.DriverManager
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread


class NewRecords : AppCompatActivity() {
    var employee = "Выбрать мастера"
    var viewPrice = "Вид услуги"
    var priceName = "Выбрать услугу"
    var priceValue = 0
    var employeeClient = "Выбрать мастера"
    var viewPriceClient = "Вид услуги"
    var priceNameClient = "Выбрать услугу"
    var priceValueClient = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_records)
        visibility()
        dataTimePickers()
        spinnerEmployee()
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////FUNCTION
    fun dataTimePickers(){
        val timeEditText = findViewById<EditText>(R.id.timeNewRecordsEditText)
        timeEditText.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                timeEditText.setText(SimpleDateFormat("HH:mm").format(cal.time))
            }
            TimePickerDialog( this, R.style.ThemeMyTimePickerStyle, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }
        val dateEditText = findViewById<EditText>(R.id.dateNewRecordsEditText)
        dateEditText.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                dateEditText.setText(SimpleDateFormat("dd.MM.yyyy").format(cal.time))
            }
            val dialog = DatePickerDialog(this,R.style.MySpinnerDatePickerStyle, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            dialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            dialog.show()
        }
        val timeClientNewRecordsEditText = findViewById<EditText>(R.id.timeClientNewRecordsEditText)
        timeClientNewRecordsEditText.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                timeClientNewRecordsEditText.setText(SimpleDateFormat("HH:mm").format(cal.time))
            }
            TimePickerDialog( this, R.style.ThemeMyTimePickerStyle, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }
        val dateClientNewRecordsEditText = findViewById<EditText>(R.id.dateClientNewRecordsEditText)
        dateClientNewRecordsEditText.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                dateClientNewRecordsEditText.setText(SimpleDateFormat("dd.MM.yyyy").format(cal.time))
            }
            val dialog = DatePickerDialog(this,R.style.MySpinnerDatePickerStyle, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            dialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            dialog.show()
        }
    }

    fun spinnerEmployee(){
        val empoloyeeNewRecordsSpinner= findViewById<Spinner>(R.id.empoloyeeNewRecordsSpinner)
        val mList = arrayOf<String?>("Выбрать мастера","Васильева О.В.", "Неля", "Марина")
        val mArrayAdapter = ArrayAdapter<Any?>(this, R.layout.spinner_style, mList)
        mArrayAdapter.setDropDownViewResource(R.layout.spinner_style)
        empoloyeeNewRecordsSpinner.adapter = mArrayAdapter
        empoloyeeNewRecordsSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener { // если изменяется выбранное
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) { //то
                employee = parentView?.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        })

        var sorted1 = ""
        val viewPriceEmployeeNewRecordsSpinner= findViewById<Spinner>(R.id.viewPriceEmployeeNewRecordsSpinner)
        val viewPriceEmployeeNewRecordsSpinnerList = arrayOf<String?>("Вид услуги","Мужской", "Женский", "Детский","Окрашивание","Маникюр","Педикюр")
        val viewPriceEmployeeNewRecordsSpinnerListArrayAdapter = ArrayAdapter<Any?>(this, R.layout.spinner_style, viewPriceEmployeeNewRecordsSpinnerList)
        viewPriceEmployeeNewRecordsSpinnerListArrayAdapter.setDropDownViewResource(R.layout.spinner_style)
        viewPriceEmployeeNewRecordsSpinner.adapter = viewPriceEmployeeNewRecordsSpinnerListArrayAdapter
        viewPriceEmployeeNewRecordsSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener { // если изменяется выбранное
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) { //то
                viewPrice = parentView?.getItemAtPosition(position).toString()
                if (viewPrice == "Вид услуги"){ sorted1 = "All" } // если выбранное значение из списка равно "Все" то сортировка будет равно алл
                else { sorted1 = "type" } // иначе сортировка равна type
                var priceNameEmployeeNewRecordsSpinnerList = arrayOf<String?>()
                val listData: MutableList<DataClassPrice> = connectPrice(parentView?.getItemAtPosition(position).toString(), sorted1)
                for (priceName in listData){
                    priceNameEmployeeNewRecordsSpinnerList += priceName.name
                }
                spinnerEmployeePriceName(priceNameEmployeeNewRecordsSpinnerList)
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        })
    }
    fun spinnerEmployeePriceName(array: Array<String?>){
        val priceNameNewRecordsSpinner= findViewById<Spinner>(R.id.priceNameEmployeeNewRecordsSpinner)
        val priceNameNewRecordsSpinnerListArrayAdapter = ArrayAdapter<Any?>(this, R.layout.spinner_style, array)
        priceNameNewRecordsSpinnerListArrayAdapter.setDropDownViewResource(R.layout.spinner_style)
        priceNameNewRecordsSpinner.adapter = priceNameNewRecordsSpinnerListArrayAdapter
        priceNameNewRecordsSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener { // если изменяется выбранное
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) { //то
                val listData: MutableList<DataClassPrice> = connectPrice(parentView?.getItemAtPosition(position).toString(), "name")
                for (priceName in listData){
                    priceValue = priceName.price
                }
                priceName = parentView?.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        })
    }

    fun spinnerClient(){
        val empoloyeeClientNewRecordsSpinner= findViewById<Spinner>(R.id.empoloyeeClientNewRecordsSpinner)
        val empoloyeeClientNewRecordsSpinnerList = arrayOf<String?>("Выбрать мастера","Васильева О.В.", "Неля", "Марина")
        val empoloyeeClientNewRecordsSpinnerArrayAdapter = ArrayAdapter<Any?>(this, R.layout.spinner_style, empoloyeeClientNewRecordsSpinnerList)
        empoloyeeClientNewRecordsSpinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_style)
        empoloyeeClientNewRecordsSpinner.adapter = empoloyeeClientNewRecordsSpinnerArrayAdapter
        empoloyeeClientNewRecordsSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener { // если изменяется выбранное
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) { //то
                employeeClient = parentView?.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        })
        var sorted = ""
        val viewPriceNewRecordsSpinner= findViewById<Spinner>(R.id.viewPriceNewRecordsSpinner)
        val viewPriceNewRecordsSpinnerList = arrayOf<String?>("Вид услуги","Мужской", "Женский", "Детский","Окрашивание","Маникюр","Педикюр")
        val viewPriceNewRecordsSpinnerListArrayAdapter = ArrayAdapter<Any?>(this, R.layout.spinner_style, viewPriceNewRecordsSpinnerList)
        viewPriceNewRecordsSpinnerListArrayAdapter.setDropDownViewResource(R.layout.spinner_style)
        viewPriceNewRecordsSpinner.adapter = viewPriceNewRecordsSpinnerListArrayAdapter
        viewPriceNewRecordsSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener { // если изменяется выбранное
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) { //то
                viewPriceClient = parentView?.getItemAtPosition(position).toString()
                if (viewPriceClient == "Вид услуги"){ sorted = "All" } // если выбранное значение из списка равно "Все" то сортировка будет равно алл
                else { sorted = "type" } // иначе сортировка равна type
                var priceNameNewRecordsSpinnerList = arrayOf<String?>()
                val listData: MutableList<DataClassPrice> = connectPrice(parentView?.getItemAtPosition(position).toString(), sorted)
                for (priceName in listData){
                    priceNameNewRecordsSpinnerList += priceName.name
                    priceValueClient = priceName.price
                }
                spinnerClientPriceName(priceNameNewRecordsSpinnerList)
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        })
    }
    fun spinnerClientPriceName(array: Array<String?>){
        val priceNameNewRecordsSpinner= findViewById<Spinner>(R.id.priceNameNewRecordsSpinner)
        val priceNameNewRecordsSpinnerListArrayAdapter = ArrayAdapter<Any?>(this, R.layout.spinner_style, array)
        priceNameNewRecordsSpinnerListArrayAdapter.setDropDownViewResource(R.layout.spinner_style)
        priceNameNewRecordsSpinner.adapter = priceNameNewRecordsSpinnerListArrayAdapter
        priceNameNewRecordsSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener { // если изменяется выбранное
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) { //то
                val listData: MutableList<DataClassPrice> = connectPrice(parentView?.getItemAtPosition(position).toString(), "name")
                for (priceName in listData){
                    priceValueClient = priceName.price
                }
                priceNameClient = parentView?.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        })
    }

    fun visibility(){
        val sharedPreference = SharedPreference(this) //переменная для определения БД для маленького объема данных
        if (sharedPreference.getValueString("phone") != "83833806514"){
            spinnerClient()
            val employeeSpinnerNewRecordsConstraintLayout = findViewById<ConstraintLayout>(R.id.employeeSpinnerNewRecordsConstraintLayout)
            val klientNewRecordsEditText = findViewById<EditText>(R.id.klientNewRecordsEditText)
            val phoneNewRecordsEditText = findViewById<EditText>(R.id.phoneNewRecordsEditText)
            val dateNewRecordsEditText = findViewById<EditText>(R.id.dateNewRecordsEditText)
            val timeNewRecordsEditText = findViewById<EditText>(R.id.timeNewRecordsEditText)
            val priceNameNewRecordsEditText = findViewById<ConstraintLayout>(R.id.viewPriceSpinnerNewRecordsConstraintLayout)
            val priceNewRecordsEditText = findViewById<ConstraintLayout>(R.id.priceNameSpinnerNewRecordsConstraintLayout)
            val historyNewRecordsButton = findViewById<Button>(R.id.historyNewRecordsButton)
            val okNewRecordsButton = findViewById<Button>(R.id.okNewRecordsButton)
            employeeSpinnerNewRecordsConstraintLayout.visibility = View.GONE
            klientNewRecordsEditText.visibility = View.GONE
            phoneNewRecordsEditText.visibility = View.GONE
            dateNewRecordsEditText.visibility = View.GONE
            timeNewRecordsEditText.visibility = View.GONE
            priceNameNewRecordsEditText.visibility = View.GONE
            priceNewRecordsEditText.visibility = View.GONE
            historyNewRecordsButton.visibility = View.GONE
            okNewRecordsButton.visibility = View.GONE

            val dateClientNewRecordsEditText = findViewById<EditText>(R.id.dateClientNewRecordsEditText)
            val timeClientNewRecordsEditText = findViewById<EditText>(R.id.timeClientNewRecordsEditText)
            val employeeClientSpinnerNewRecordsConstraintLayout = findViewById<ConstraintLayout>(R.id.employeeClientSpinnerNewRecordsConstraintLayout)
            val viewPriceClientSpinnerNewRecordsConstraintLayout = findViewById<ConstraintLayout>(R.id.viewPriceClientSpinnerNewRecordsConstraintLayout)
            val priceNameClientSpinnerNewRecordsConstraintLayout = findViewById<ConstraintLayout>(R.id.priceNameClientSpinnerNewRecordsConstraintLayout)
            val okClientNewRecordsButton = findViewById<Button>(R.id.okClientNewRecordsButton)
            okClientNewRecordsButton.visibility = View.VISIBLE
            dateClientNewRecordsEditText.visibility = View.VISIBLE
            timeClientNewRecordsEditText.visibility = View.VISIBLE
            employeeClientSpinnerNewRecordsConstraintLayout.visibility = View.VISIBLE
            viewPriceClientSpinnerNewRecordsConstraintLayout.visibility = View.VISIBLE
            priceNameClientSpinnerNewRecordsConstraintLayout.visibility = View.VISIBLE
        }
    }

    fun clickOkClient(view: View) {
        val date= findViewById<EditText>(R.id.dateClientNewRecordsEditText)
        val time= findViewById<EditText>(R.id.timeClientNewRecordsEditText)
        val errorTextView = findViewById<TextView>(R.id.errorСlientNewRecordsTextView)
        val sharedPreference = SharedPreference(this) //переменная для определения БД для маленького объема данных

        var check = true
        if (date.text.toString() != "" && time.text.toString() != "" && employeeClient != "Выбрать мастера" && priceNameClient != "Выбрать услугу"){
            val listRecords = connectRecords()
            var klient = ""
            for (record in listRecords){
                if (record.date == date.text.toString() && record.time == time.text.toString() && record.employee == employeeClient){
                    check = false
                }
                if (record.number == sharedPreference.getValueString("phone").toString()){
                    klient = record.klients
                }
            }
            if (check == true){
                recordsAddContent("records",  date.text.toString(),time.text.toString(),
                    klient, sharedPreference.getValueString("phone").toString(), employeeClient,
                    priceNameClient, priceValueClient.toString())
                errorTextView.setText("Запись успешно добавлена!")
            }
            else errorTextView.setText("Ошибка! Дата и время уже заняты")
        }
        else errorTextView.setText("Ошибка! Вы пропустили поле")
    }

    fun clickOk(view: View) {
        val errorText= findViewById<TextView>(R.id.errorNewRecordsTextView)
        val klient= findViewById<EditText>(R.id.klientNewRecordsEditText)
        val number= findViewById<EditText>(R.id.phoneNewRecordsEditText)
        val date= findViewById<EditText>(R.id.dateNewRecordsEditText)
        val time= findViewById<EditText>(R.id.timeNewRecordsEditText)
        var check = true

        if (klient.text.toString() != "" && number.text.toString() != ""&& priceName != "" &&
            employee != "Выбрать мастера" && date.text.toString() != "" && time.text.toString() != ""){
            if (number.text.length == 11){
                val listRecords = connectRecords()
                for (record in listRecords){
                    if (record.date == date.text.toString() && record.time == time.text.toString() && record.employee == employee){
                        check = false
                    }
                }
                if (check == true){
                    recordsAddContent("records",  date.text.toString(),time.text.toString(),
                        klient.text.toString(), number.text.toString(), employee,
                        priceName, priceValue.toString())
                    errorText.setText("Запись успешно добавлена!")
                }
                else errorText.setText("Ошибка! Дата и время уже заняты")
            }
            else errorText.setText("Ошибка! Номер телефона должен содержать 11 символов и начинаться с 8")
        }
        else errorText.setText("Ошибка! Вы пропустили поле")
    }

    fun clickHistory(view: View) {
        val klient= findViewById<EditText>(R.id.klientNewRecordsEditText)
        val number= findViewById<EditText>(R.id.phoneNewRecordsEditText)
        val errorText= findViewById<TextView>(R.id.errorNewRecordsTextView)

        if (klient.text.toString() != ""){
            val transitionIntent = Intent(this, History::class.java)
            transitionIntent.putExtra("User", klient.text.toString())
            startActivity(transitionIntent)
        }
        else if (number.text.toString() != ""){
            val transitionIntent = Intent(this, History::class.java)
            transitionIntent.putExtra("User", number.text.toString())
            startActivity(transitionIntent)
        }
        else{
            errorText.setText("Введите номер телефона или ФИО клиента!")
        }
    }


    fun recordsAddContent(nameTable: String, date: String, time: String, klient: String, number: String, employee: String, price_name: String, price: String){
        thread { //Создание потока
            try { //попробовать
                val user = "fan651sh_sai" //Имя пользователя БД
                val pass = "Dobro1337//" //Пароль к БД
                val url = "jdbc:mysql://fan651sh.beget.tech:3306/fan651sh_sai" //Строка подключения (Библиотека_для_подключения_к_БД://ип:порт/Имя_бд)
                Class.forName("com.mysql.jdbc.Driver")
                val connection = DriverManager.getConnection(url, user, pass)
                val newPrice: Int = price.toInt()
                val numberToLong = number.toLong()
                val insert ="INSERT INTO $nameTable (id, date, time, klient, number, employee, name_price, price) VALUES " +
                        "(NULL, '$date', '$time', '$klient', '$numberToLong', '$employee', '$price_name', $newPrice);"
                val querynIsertAccount = connection.prepareStatement(insert)
                querynIsertAccount.execute()
            }
            catch (e: Exception) { //Если не получилось то отлавливаем ошибки и выводим их в консоль
                print(e.message)
            }
        }.join()
    }

    fun connectRecords() :MutableList<DataClassRegistr>{ //Функция записи результатов функции подключения к БД
        val listRegistr: MutableList<DataClassRegistr> = mutableListOf() //создание списка
        val user = "fan651sh_sai" //Имя пользователя БД
        val pass = "Dobro1337//" //Пароль к БД
        val url = "jdbc:mysql://fan651sh.beget.tech:3306/fan651sh_sai"
        thread { //Создание потока
            try { //попробовать
                Class.forName("com.mysql.jdbc.Driver")
                val connection = DriverManager.getConnection(url, user, pass) // подключение к БД
                val search = "SELECT * FROM records;" //Составленеи запроса к БД к определенной таблице
                val searchQuery = connection.prepareStatement(search)  // выборка всей таблицы
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
                }
            } catch (e: Exception) { //Если не получилось то отлавливаем ошибки и выводим их в консоль
                print(e.message)
            }
        }.join() //Отвечает за приостановку основного потока до выполнения этого
        return listRegistr //Вернуть список с значениями
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