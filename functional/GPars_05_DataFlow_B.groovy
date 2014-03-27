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
import groovyx.gpars.dataflow.Dataflows

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

// PROBLEM

final def sinceNewYear = { game ->
    return (toDate << field(0) << game) >= toDate('01012014')
}
final def lakerAsVisitor = { game -> has('Lakers') << field(3) << game }
final def whenVisitor = when(validRecord, lakerAsVisitor)
final def whenVisitorSinceNewYear = when(whenVisitor, sinceNewYear)
final def winsOverTotal = { map -> map.win / map.total }

//
final def gamesPlayedAndWon = { records, game ->
    def didItWin = winOrLose << fields(2,4) << game
    if (didItWin) {
        records.win += 1
    }
    records.total += 1
    records
}
//
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
