package okasaki.heaps

import okasaki.Heap
import okasaki.heaps.LazyBinomialHeap._
import okasaki.misc.Susp

/**
 * Copyright (C) 2015-2016 Kamchatka Ltd
 */
object LazyBinomialHeap {

  case class Node[E](e: E, c: List[Node[E]])

  type BHeap[E] = Susp[List[(Int, Node[E])]]

  def rank(t: (Int, Node[_])): Int = t._1

  def root[E](t: (Int, Node[E])): E = t._2.e

  def link[E](t1: (Int, Node[E]), t2: (Int, Node[E]))
             (implicit ord: Ordering[E]): (Int, Node[E]) =
    (t1, t2) match {
      case ((r, Node(x1, c1)), (_, Node(x2, c2))) =>
        if (ord.lteq(x1, x2)) (r + 1, Node(x1, t2._2 :: c1))
        else (r + 1, Node(x2, t1._2 :: c2))
    }

  def insTree[E](t: (Int, Node[E]), ts: List[(Int, Node[E])])
                (implicit ord: Ordering[E]): List[(Int, Node[E])] =
    ts match {
      case Nil => List(t)
      case t1 :: ts1 => if (rank(t) < rank(t1)) t :: ts else insTree(link(t, t1), ts1)
    }
}

class LazyBinomialHeap[E](val h: BHeap[E] = Susp(Nil.asInstanceOf[List[(Int, Node[E])]]))
                         (implicit val ord: Ordering[E]) extends Heap[E, LazyBinomialHeap[E]] {

  override def empty = new LazyBinomialHeap[E](Susp(Nil))

  override def isEmpty = h match {
    case Susp(Nil) => true
    case _ => false
  }

  override def insert(x: E) = new LazyBinomialHeap[E](Susp(insTree((0, Node(x, Nil)), h())))

  def mrg(ts1: List[(Int, Node[E])], ts2: List[(Int, Node[E])]): List[(Int, Node[E])] = (ts1, ts2) match {
    case (_, Nil) => ts1
    case (Nil, _) => ts2
    case (t1 :: ts11, t2 :: ts22) =>
      if (rank(t1) < rank(t2)) t1 :: mrg(ts11, ts2)
      else if (rank(t2) < rank(t1)) t2 :: mrg(ts1, ts22)
      else insTree(link(t1, t2), mrg(ts11, ts22))
  }

  override def merge(o: LazyBinomialHeap[E]) = new LazyBinomialHeap[E](Susp.lift2(mrg)(h, o.h))

  override def findMin: E = root(removeMinTree(h())._1)

  override def deleteMin = new LazyBinomialHeap[E](removeMinTree(h()) match {
    case ((r, Node(_, ts1)), ts2) => Susp.lift2(mrg)(Susp(ts1.map(withRank(r - 1)).reverse), Susp(ts2))
  })

  def withRank(r: Int)(x: Node[E]): (Int, Node[E]) = (r, x)

  private def removeMinTree(h: List[(Int, Node[E])]): ((Int, Node[E]), List[(Int, Node[E])]) = h match {
    case Nil => throw new IllegalStateException("called removeMinTree on an empty heap")
    case t :: Nil => (t, Nil)
    case t :: ts =>
      removeMinTree(ts) match {
        case (t1, ts1) =>
          if (ord.lteq(root(t), root(t1))) (t, ts)
          else (t1, t :: ts1)
      }
  }
}
