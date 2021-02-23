object TestObject {
    val target: String = "OK"
}

fun box(): String {
    return TestObject.target
}