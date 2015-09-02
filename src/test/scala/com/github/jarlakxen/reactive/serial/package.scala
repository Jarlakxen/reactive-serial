package com.github.jarlakxen.reactive

import akka.actor._
import akka.testkit._
import org.specs2.specification.After

/**
 * @author fviale
 */

package object serial {

  abstract class AkkaContext extends TestKit(ActorSystem("Test"))
      with After
      with ImplicitSender {

    def after = {
      system.shutdown
      system.awaitTermination
    }
  }

}