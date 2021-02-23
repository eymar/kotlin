// MODULE: main
// FILE: test.kt
fun target(param: String = ""): String { // should be properly modified by Ir plugin
    return param
}

fun box(): String {
    val res = target()
    return if (res == "generated") "OK" else res
}