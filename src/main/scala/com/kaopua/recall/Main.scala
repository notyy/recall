package com.kaopua.recall

import Interpreter._
import org.squeryl._
import org.squeryl.adapters.H2Adapter

object Main extends App {
  Class.forName("org.h2.Driver")
  SessionFactory.concreteFactory = Some(() =>
      Session.create(
        java.sql.DriverManager.getConnection("jdbc:h2:~/com.kaopua/recall"),
        new H2Adapter))
  SessionFactory.newSession.bindToCurrentThread
//  MemoryDb.drop
  MemoryDb.create
  welcome()
  askForCommand()
}
