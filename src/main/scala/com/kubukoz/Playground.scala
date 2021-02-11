package com.kubukoz

import java.nio.file.Paths
import java.nio.file.Path
import scala.meta.internal.semanticdb.TextDocuments
import scala.meta.internal.semanticdb.Signature.Empty
import scala.meta.internal.semanticdb.TypeSignature
import scala.meta.internal.semanticdb.ClassSignature
import scala.meta.internal.semanticdb.ValueSignature
import scala.meta.internal.semanticdb.MethodSignature
import scala.annotation.meta.param
import scala.meta.internal.semanticdb.Type
import scala.meta.internal.semanticdb.SymbolInformation
import scala.meta.internal.semanticdb.ExistentialType
import scala.meta.internal.semanticdb.SingleType
import scala.meta.internal.semanticdb.AnnotatedType
import scala.meta.internal.semanticdb.ConstantType
import scala.meta.internal.semanticdb.UnionType
import scala.meta.internal.semanticdb.IntersectionType
import scala.meta.internal.semanticdb.SuperType
import scala.meta.internal.semanticdb.WithType
import scala.meta.internal.semanticdb.UniversalType
import scala.meta.internal.semanticdb.TypeRef
import scala.meta.internal.semanticdb.RepeatedType
import scala.meta.internal.semanticdb.ThisType
import scala.meta.internal.semanticdb.StructuralType
import scala.meta.internal.semanticdb.ByNameType

object Playground extends App {

  val seeked = args

  def noop(code: => Any) = ()
  println {
    val map = scala.collection.mutable.Map.empty[Path, TextDocuments]

    scala.meta.internal.semanticdb.Locator(Paths.get("/Users/kubukoz/IdeaProjects/cats-effect/.bloop")) { case (path, docs) =>
      map += (path -> docs)
    }

    val docs = map.values.flatMap(_.documents)
    docs.foreach { doc =>
      val defs = doc.occurrences.filter(_.role.isDefinition)

      val implicitSymbols =
        doc.symbols.filter(_.isPublic).filter(_.isImplicit)

      var first = true

      defs
        .toList
        .flatMap { definition =>
          implicitSymbols.find(_.symbol == definition.symbol).map(definition -> _)
        }
        .filter { case (defn, sym) => seeked.forall(sym.toString.contains) }
        .foreach { case (definition, sym) =>
          if (first) {
            println(s"\nin ${doc.uri}\n")
            first = false
          }

          def simplify(tpe: Type): String = tpe match {
            case SingleType(prefix, symbol)             => s"$prefix.$symbol"
            case TypeRef(prefix, symbol, typeArguments) => s"$prefix.$symbol[${typeArguments.map(simplify).mkString(", ")}]"
            case _                                      => tpe.toString
          }

          val signature = sym.signature match {
            case Empty                                                       => "<none>"
            case TypeSignature(_, _, _)                                      => "<too complex for this hour>"
            case ClassSignature(_, _, _, _)                                  => "<class>"
            case ValueSignature(tpe)                                         => s"${simplify(tpe)}"
            case MethodSignature(typeParameters, parameterLists, returnType) =>
              s"(...) => ${simplify(returnType)}"
          }

          println(
            s"implicit ${sym.kind}: ${sym.displayName}, type $signature"
          )
        }

    }
  }

}
