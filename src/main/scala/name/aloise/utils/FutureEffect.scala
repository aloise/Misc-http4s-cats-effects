package name.aloise.utils

import cats.effect.{Effect, IO, LiftIO}
import java.util.concurrent.atomic.AtomicBoolean

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success, Try}

object FutureEffect extends FutureEffectInstances

trait FutureEffectInstances {

  implicit def taskEffect(implicit ec:ExecutionContext): Effect[Future] = new Effect[Future] {

    private def functionToPartial[A, B](f: A => B): PartialFunction[A, B] = _ match {
      case a => f(a)
    }

    override def runAsync[A](fa: Future[A])(cb: Either[Throwable, A] => IO[Unit]): IO[Unit] =
      IO.fromFuture(IO(fa)).runAsync(cb)

    override def async[A](k: (Either[Throwable, A] => Unit) => Unit): Future[A] = {
      val p = Promise[A]
      // callback should be set once ? In order to comply with `repeatedCallbackIgnored` law
      val atomic = new AtomicBoolean(true)
      k({
        case _ if !atomic.getAndSet(false) =>
          ()
        case Left(exception) =>
          p.failure(exception)
        case Right(value) =>
          p.success(value)
      })

      p.future
    }

    override def suspend[A](thunk: => Future[A]): Future[A] =
      Future.fromTry(Try(thunk)).flatten

    override def flatMap[A, B](fa: Future[A])(f: A => Future[B]): Future[B] =
      fa.flatMap(f)

    override def tailRecM[A, B](a: A)(f: A => Future[Either[A, B]]): Future[B] = f(a).flatMap {
      case Left(value) => tailRecM(value)(f)
      case Right(value) => Future.successful(value)
    }

    override def raiseError[A](e: Throwable): Future[A] = Future.failed(e)

    override def handleErrorWith[A](fa: Future[A])(f: Throwable => Future[A]): Future[A] =
      fa.recoverWith(functionToPartial(f))

    override def pure[A](x: A): Future[A] = Future.successful(x)
  }
}