package name.aloise.utils

import cats.effect.Effect

trait WithEffects {

  // just a shortcut - helper method
  protected def eff[F[_] : Effect]: Effect[F] = implicitly[Effect[F]]


}
