package com.kubukoz

import zio._

object Playground extends zio.App {

  def run(args: List[String]): URIO[ZEnv, ExitCode] =
    console
      .putStrLn("hello world")
      .as(ExitCode.success)

}
