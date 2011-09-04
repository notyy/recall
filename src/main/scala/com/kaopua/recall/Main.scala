//import com.kaopu.recall.Interpreter._
package com.kaopua.recall

import Interpreter._

object Main extends App {
  welcome()
  Memory.setMemoryMap(JsonStore.loadMemories())
  askForCommand()
}
