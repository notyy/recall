package com.kaopua.recall
abstract class Command
case class Mark(hint:String, content:String) extends Command
case class Recall(hint:String) extends Command
case class List() extends Command
case class sync() extends Command
case class Train() extends Command
case class Help() extends Command
case class Quit() extends Command
