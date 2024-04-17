package zio

import scala.annotation.{MacroAnnotation, experimental}
import scala.quoted.*

@experimental class zioMain extends MacroAnnotation:
  override def transform(using quotes: Quotes)(tree: quotes.reflect.Definition): List[quotes.reflect.Definition] =
    import quotes.reflect._

    tree match
      case ValDef(name, tt, _) =>
        tt.tpe.asType match
          case '[ZIO[?, ?, ?]] =>
            val parents = List(TypeTree.of[Object])

            def decls(cls: Symbol): List[Symbol] =
              List(Symbol.newMethod(cls, "main", MethodType(List("args"))(_ => List(TypeRepr.of[Array[String]]), _ => TypeRepr.of[Unit]), Flags.JavaStatic, Symbol.noSymbol))

            val cls: Symbol = Symbol.newClass(Symbol.spliceOwner, name, parents = parents.map(_.tpe), decls, selfType = None)

            val mainSym: Symbol = cls.declaredMethod("main").head

            val zio = Ref(tree.symbol).asExprOf[ZIO[ZIOAppArgs, Any, Any]]

            def mainBody(rhs: List[List[Tree]]) =
              val mainArgs = rhs.head.head.asExprOf[Array[String]]

              '{
                ZIOAppDefault
                  .fromZIO(${ zio })
                  .main(${ mainArgs })
              }

            val mainDef = DefDef(mainSym, rhs => Some(mainBody(rhs).asTerm))

            val clsDef = ClassDef(cls, parents, List(mainDef))

            List(clsDef, tree)

          case _ =>
            report.error("The val for zioMain must be a ZIO")
            List(tree)

      case _ =>
        report.error("zioMain can only be applied to a val")
        List(tree)
