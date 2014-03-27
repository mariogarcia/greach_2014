//  _          _   _       _          _     ___
// | |        | | ( )     | |        | |   |__ \
// | |     ___| |_|/ ___  | |__   ___| |_     ) |
// | |    / _ \ __| / __| | '_ \ / _ \ __|   / /
// | |___|  __/ |_  \__ \ | |_) |  __/ |_   |_|
// |______\___|\__| |___/ |_.__/ \___|\__|  (_)                  _    __   _______
// \ \   / /          | |        | | | |                        | |   \ \ / /  __ \
//  \ \_/ /__  _   _  | |__   ___| |_| |_ ___ _ __   _ __   ___ | |_   \ V /| |  | |
//   \   / _ \| | | | | '_ \ / _ \ __| __/ _ \ '__| | '_ \ / _ \| __|   > < | |  | |
//    | | (_) | |_| | | |_) |  __/ |_| ||  __/ |    | | | | (_) | |_   / . \| |__| |
//    |_|\___/ \__,_| |_.__/ \___|\__|\__\___|_|    |_| |_|\___/ \__| /_/ \_\_____/
//
//
// PROBLEM: Dependent values dont have to be calculated sequentially
// CONCEPT: DATAFLOW
// EXAMPLE: Combined probability
//
@Grab(group='org.gperfutils', module='gbench', version='0.4.2-groovy-2.1')

import static groovyx.gpars.dataflow.Dataflow.task

import groovyx.gpars.group.DefaultPGroup
import groovyx.gpars.dataflow.Dataflows

//      _
//     | |
//   __| |_ __ _   _
//  / _` | '__| | | |
// | (_| | |  | |_| |
//  \__,_|_|   \__, |
//              __/ |
//             |___/
//

final File NBA_SCORES_FILE = new File("data/nbascore.csv")

// TRANSFORMATIONS
def split = { line -> line.split(',') }
def ex = { number -> return { String... elements -> elements.getAt(number) } }
def has = { String criteria ->
    return { String comparable -> comparable?.contains(criteria) }
}
def field = { number -> return { line -> ex(number) << split << line } }
def fields = { Integer... fieldNumbers ->
    return { game -> fieldNumbers*.collect { n -> field(n) << game }.flatten() }
}
def toDate = { String ddMMyyyy -> Date.parse('ddMMyyyy',ddMMyyyy) }
def winOrLose = { a, b -> a.toInteger() - b.toInteger() > 0 ? true : false }

// FILTERS
def validRecord = { game -> split(game).size() == 5 }
def when = { Closure... filters ->
    return { game -> filters*.doCall(game).every { it } }
}

//                  _     _
//                 | |   | |
//  _ __  _ __ ___ | |__ | | ___ _ __ ___
// | '_ \| '__/ _ \| '_ \| |/ _ \ '_ ` _ \
// | |_) | | | (_) | |_) | |  __/ | | | | |
// | .__/|_|  \___/|_.__/|_|\___|_| |_| |_|
// | |
// |_|
//

final def sinceNewYear = { game ->
    return (toDate << field(0) << game) >= toDate('01012014')
}
final def lakerAsVisitor = { game -> has('Lakers') << field(3) << game }
final def whenVisitor = when(validRecord, lakerAsVisitor)
final def whenVisitorSinceNewYear = when(whenVisitor, sinceNewYear)
final def winsOverTotal = { map -> map.win / map.total }

/* How to collect results */
final def gamesPlayedAndWon = { records, game ->
    def didItWin = winOrLose << fields(2,4) << game
    if (didItWin) {
        records.win += 1
    }
    records.total += 1
    records
}

/* We dont want to have a shared map out there */
final def emptyMap = { [total:0, win:0] }

// GENERALIZATION
final probabilityToWin = { filter ->
    def sample = NBA_SCORES_FILE.withReader { reader ->
        return reader.
            findAll(filter).
            inject(
                emptyMap(),
                gamesPlayedAndWon
            )
    }

    return winsOverTotal << sample
}

//            _       _   _
//           | |     | | (_)
//  ___  ___ | |_   _| |_ _  ___  _ __
// / __|/ _ \| | | | | __| |/ _ \| '_ \
// \__ \ (_) | | |_| | |_| | (_) | | | |
// |___/\___/|_|\__,_|\__|_|\___/|_| |_|
//
//

def sampleBenchmark = benchmark(warmUpTime: 10){
    'SEQUENTIAL' {
        def result = probabilityToWin(whenVisitor) * probabilityToWin(whenVisitorSinceNewYear)

        assert result < 0.50 && result > 0.1
    }
    'USING DATAFLOW' {

        final def df           = new Dataflows()
        final def group        = new DefaultPGroup(4)
        // CALCULATE

        group.task {
            df.allTimes = probabilityToWin(whenVisitor)
        }

        group.task {
            df.thisYear = probabilityToWin(whenVisitorSinceNewYear)
        }

        group.task {
            df.totalProbability = df.allTimes * df.thisYear
        }

        assert df.totalProbability < 0.50 && df.totalProbability > 0.1

    }
} // END OF BENCHMARK


sampleBenchmark.prettyPrint()
