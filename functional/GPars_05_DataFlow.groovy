//
//  _______ _                                          _ _                        _
// |__   __| |                                        (_) |                      | |
//    | |  | |__   ___    ___ ___  _ __ ___  _ __ ___  _| |_ _ __ ___   ___ _ __ | |_ ___
//    | |  | '_ \ / _ \  / __/ _ \| '_ ` _ \| '_ ` _ \| | __| '_ ` _ \ / _ \ '_ \| __/ __|
//    | |  | | | |  __/ | (_| (_) | | | | | | | | | | | | |_| | | | | |  __/ | | | |_\__ \
//    |_|  |_| |_|\___|  \___\___/|_| |_| |_|_| |_| |_|_|\__|_| |_| |_|\___|_| |_|\__|___/
//
// PROBLEM: Be efficient with resources and dont block while there's something to do meanwhile
// CONCEPT: DATAFLOW
// EXAMPLE: Two teams compete to finish a given sprint in the first place... let's see which one
// competes better ;)
//
@Grab(group='org.gperfutils', module='gbench', version='0.4.2-groovy-2.1')

import static groovyx.gpars.dataflow.Dataflow.task

import groovyx.gpars.group.DefaultPGroup
import groovyx.gpars.dataflow.DataflowVariable

def sequentialVsParallel = benchmark(warmUpTime: 10){
    'TEAM A' {

        def productOwner = 5
            Thread.sleep(500) // Contract signing
        def back         = productOwner + 10
            Thread.sleep(1000) // Something wasn't defined
        def front        = productOwner + 10
            Thread.sleep(500) // Client wanted to add something
        def release      = front + back + 10

        // COMMITED 40 POINTS

        assert release == 40
    }
    'TEAM B' {

        final def back         = new DataflowVariable()
        final def front        = new DataflowVariable()
        final def productOwner = new DataflowVariable()
        final def release      = new DataflowVariable()
        final def group        = new DefaultPGroup(4)

        // COMMITED 40 POINTS AS WELL

        group.task {
            productOwner <<  5
            Thread.sleep(500) // Contract signing
        }

        group.task {
            front <<  productOwner.val + 10
            Thread.sleep(500) // Client wanted to add something
        }

        group.task {
            back <<  productOwner.val + 10
            Thread.sleep(1000) // Something wasn't defined
        }

        group.task {
            release <<  back.val + front.val + 10
        }

        assert release.val == 40

    }
} // END OF BENCHMARK


sequentialVsParallel.prettyPrint()
