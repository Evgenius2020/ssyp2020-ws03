package ru.leadpogrommer.mpg

open class Message
class TickMsg: Message()
class ConnectMsg(val c: Communicator): Message()
class RequestMsg(val c: Communicator, val r: Request): Message()
class DisconnectMsg(val c: Communicator): Message()