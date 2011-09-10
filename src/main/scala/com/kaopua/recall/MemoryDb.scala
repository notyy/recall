package com.kaopua.recall

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Schema

object MemoryDb extends Schema {
  val memories = table[Memory]

  on(memories) (m => declare(
    m.hint is(indexed,unique),
    m.level is(indexed))
  )

}
