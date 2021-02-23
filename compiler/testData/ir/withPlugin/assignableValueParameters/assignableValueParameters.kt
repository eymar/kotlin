// MODULE: libA
// FILE: libA.kt
fun target(param: String = ""): String { // should be properly modified by Ir plugin
    return param
}

// MODULE: main(libA)
// FILE: test.kt
fun box(): String {
    val res = target()
    return if (res == "generated") "OK" else res
}