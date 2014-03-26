//  _   _ ____                                   _         _       __           _
// | \ | |  _ \   /\                            | |       | |     / _|         | |
// |  \| | |_) | /  \    __      _____  _ __ ___| |_    __| | ___| |_ ___  __ _| |_
// | . ` |  _ < / /\ \   \ \ /\ / / _ \| '__/ __| __|  / _` |/ _ \  _/ _ \/ _` | __|
// | |\  | |_) / ____ \   \ V  V / (_) | |  \__ \ |_  | (_| |  __/ ||  __/ (_| | |_
// |_| \_|____/_/    \_\   \_/\_/ \___/|_|  |___/\__|  \__,_|\___|_| \___|\__,_|\__|
//
//
// PROBLEM: Looking for the worst defeat in an NBA game of all times
// CONCEPT: PARALLEL
// EXAMPLE: Looking for the biggest difference in an NBA game of all times
//
@Grab(group='org.gperfutils', module='gbench', version='0.4.2-groovy-2.1')

import static groovyx.gpars.GParsPool.withPool

final File NBA_SCORES_FILE = new File('data/nbascore.csv')
final String COMMA = ','

// GATHERING DATA
def field = { field ->
    return { line -> line.split(COMMA).getAt(field) }
}

def fields = { Integer... fieldNumbers ->
    return { line ->
        fieldNumbers.collect{ fieldNumber -> field(fieldNumber) << line }
    }
}

// TRANSFORMING DATA
def toInteger = { possibleNumbers -> possibleNumbers*.toInteger() }
def sort = { numbers -> numbers.sort() }
def substract = { a, b -> b - a }
def differenceCollector = { line ->
    substract << sort << toInteger << fields(2,4) << line
}

// SAFETY HELPER
def safely = { closure ->
    return { line -> try { closure(line) } catch(e) { 0 } }
}

// TEMPLATE METHOD
def collectMaximum = { strategy ->
    return NBA_SCORES_FILE.withReader(strategy).max()
}

def sequentialVsParallel = benchmark(warmUpTime: 10) {
    'Sequencial Groovy' {
        Integer result = collectMaximum { reader ->
            return reader.collect(safely(differenceCollector))
        }

        assert result == 68
    }
    'Parallel Groovy' {
        Integer result = collectMaximum { reader ->
            return withPool(4) {
                reader.collectParallel(safely(differenceCollector))
            }
        }

        assert result == 68
    }
} // END OF BENCHMARK


sequentialVsParallel.prettyPrint()
