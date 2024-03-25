zio-main
--------

A Scala MacroAnnotation that makes it easy to create mains for ZIO apps.

```scala
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
```

Note: Due to a Scala bug, this doesn't work with sbt. So we use scala-cli instead.

Run without args:
```
scala-cli run Hello.scala -O -experimental --server=false --watch --main-class Hello\$package\$run .
```

Run with args:
```
scala-cli run Hello.scala -O -experimental --server=false --watch --main-class Hello\$package\$runWithArg . -- james
```
