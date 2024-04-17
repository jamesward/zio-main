import zio.*

import scala.annotation.experimental

@zioMain
val run =
  Console.printLine("hello, world")

@zioMain
val runWithArg =
  for
    args <- ZIOAppArgs.getArgs
    name <- ZIO.fromOption(args.headOption)
    _    <- Console.printLine(s"hello, $name")
  yield
    ()
