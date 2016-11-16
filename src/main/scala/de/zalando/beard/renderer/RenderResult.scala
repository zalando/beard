package de.zalando.beard.renderer

import java.io.StringWriter
import monix.reactive.subjects.ReplaySubject
import monix.reactive.Observable

/**
 * @author dpersa
 */
trait RenderResult[T] {
  def write(string: String): Unit

  def complete(): Unit = {}

  def result: T
}

case class StringWriterRenderResult() extends RenderResult[StringWriter] {

  val stringWriter = new StringWriter()

  override def write(renderedChunk: String): Unit = {
    stringWriter.write(renderedChunk)
  }

  override def result = stringWriter
}

case class MonifuRenderResult()
    extends RenderResult[Observable[String]] {

  val subject = ReplaySubject[Observable[String]]()

  override def write(renderedChunk: String): Unit = {
    subject.onNext(Observable.pure(renderedChunk))
  }

  override def complete(): Unit = subject.onComplete()

  override def result = subject.concat
}
