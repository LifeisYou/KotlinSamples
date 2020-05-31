package com.example.kotlinsamples.data

class User(var name: String, var address: String)

class Person1 {
    var name: String? = null
}

open class Person2(var name: String?)

open class Person3 {
    var name: String? = null
    var address: String? = null

    constructor(name: String) {
        this.name = name
    }

    constructor(name: String, address:String) {
        this.name = name
        this.address = address
    }

    //主构造函数在类里，constructor次构造函数
    //初始化代码放在init的初始化快内，先执行主构造函数，再init
    // 同时包含init和次构造函数，优先执行init
    init {
        print("My name is $name")
    }

    open fun sayHello() {

    }
}

class Man(name: String) : Person2(name)

class Man2 : Person3 {
    constructor(name: String) : super(name)

    constructor(name: String, address: String) : super(name, address)

    override fun sayHello() {
        super.sayHello()
    }
}