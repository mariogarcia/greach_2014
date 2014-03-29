//  _____ _               _____
// |  __ (_)             |  __ \
// | |__) | _ __   __ _  | |__) |__  _ __   __ _
// |  ___/ | '_ \ / _` | |  ___/ _ \| '_ \ / _` |
// | |   | | | | | (_| | | |  | (_) | | | | (_| |
// |_|   |_|_| |_|\__, | |_|   \___/|_| |_|\__, |
//                 __/ |                    __/ |
//                |___/                    |___/
//

import groovyx.gpars.actor.Actors
import groovyx.gpars.actor.DefaultActor
import java.util.concurrent.CountDownLatch

final countDown = new CountDownLatch(10)

class PingPongPlayer extends DefaultActor {

    String funnyWord
    Boolean first
    PingPongPlayer contender
    CountDownLatch countDown

    void act() {
        if (first) {
            contender << funnyWord
        }
        loop {
            react {
                println it
                contender << funnyWord
                Thread.sleep(2000)
                countDown.countDown()
            }
        }
    }

}

def ping = new PingPongPlayer(funnyWord:'Hey', first:true, countDown: countDown)
def pong = new PingPongPlayer(funnyWord:'Ho', contender: ping, countDown: countDown)

ping.contender = pong

[ping,pong]*.start()

// EXAMPLE FOR READING A LINE AND RETURNING THE REVERSE
// EXAMPLE FOR AN ACTOR CROPPING IMAGES SENT TO IT

countDown.await()

