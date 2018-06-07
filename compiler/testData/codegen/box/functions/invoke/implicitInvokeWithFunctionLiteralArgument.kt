// IGNORE_BACKEND: JS_IR
class TestClass {
    inline operator fun <T> invoke(task: () -> T) = task()
}

fun box(): String {
    val test = TestClass()
    val ok = "OK"

    val x = test { return ok }
}