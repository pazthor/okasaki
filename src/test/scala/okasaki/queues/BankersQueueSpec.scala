package okasaki.queues

import okasaki.{IntElements, QueueSpec}

/**
 * Copyright (C) 2015 Kamchatka Ltd
 */
class BankersQueueSpec
  extends QueueSpec(new BankersQueue[Int])
  with IntElements
