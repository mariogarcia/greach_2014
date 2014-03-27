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
import java.util.concurrent.CountDownLatch

final countDown = new CountDownLatch(10)
final pong = Actors.actor {
    loop {
        react { message ->
            countDown.countDown()
            println "Countdown: ${countDown.count}"
            println message
            reply "pong"
        }
    }
}

final ping = Actors.actor {
    pong << "ping"
    loop {
        react { message ->
           println message
           Thread.sleep(2000)
           pong << "ping"
        }
    }
}

// EXAMPLE FOR READING A LINE AND RETURNING THE REVERSE
// EXAMPLE FOR AN ACTOR CROPPING IMAGES SENT TO IT

countDown.await()

