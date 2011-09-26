package com.kaopua.recall
abstract class Command
case class Mark(hint: String, content: String) extends Command
case class EndMark(marks: List[Mark]) extends Command
case class Remove(hint: String) extends Command
case class Recall(hint: String, mode: Int) extends Command
case class sync() extends Command
case class Train() extends Command
case class Help() extends Command
case class Quit() extends Command
case class Append(content: String) extends Command
case class Error(msg: String) extends Command
case class PlainText(prompt: String) extends Command
