package kcoin

fun main() {
    val socket = SocketServes()
    Thread{ socket.server() }.start()
    socket.client()
}

