package okasaki

import scala.collection.immutable.Stream.iterate

/**
 * Copyright (C) 2015 Kamchatka Ltd
 */
abstract class DequeSpec[E, Q](deque: Deque[E, Q]) extends OutputRestrictedDequeSpec(deque) {
  "A deque" should {
    "Maintain the reverse order" ! prop {
      xs: List[E] =>
        val xs1 = drainReversed(fromListReversed(xs))
        xs1 === xs
    }
  }

  "An empty deque" should {
    "be empty for last" ! prop {
      e: E =>
        val q = deque.empty
        deque.last(q) should throwAn[IllegalStateException]
    }

    "be empty for init" ! prop {
      e: E =>
        val q = deque.empty
        deque.init(q) should throwAn[IllegalStateException]
    }
  }


  def drainReversed(q: Q): List[E] =
    iterate(q)(deque.init)
      .takeWhile(!deque.isEmpty(_))
      .map(deque.last)
      .toList
}
