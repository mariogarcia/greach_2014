//  _   _ ____            _____                            _            _                _
// | \ | |  _ \   /\     |  __ \                          | |          | |              | |
// |  \| | |_) | /  \    | |  | | ___ _ __   ___ _ __   __| | ___ _ __ | |_  __   ____ _| |_   _  ___  ___
// | . ` |  _ < / /\ \   | |  | |/ _ \ '_ \ / _ \ '_ \ / _` |/ _ \ '_ \| __| \ \ / / _` | | | | |/ _ \/ __|
// | |\  | |_) / ____ \  | |__| |  __/ |_) |  __/ | | | (_| |  __/ | | | |_   \ V / (_| | | |_| |  __/\__ \
// |_| \_|____/_/    \_\ |_____/ \___| .__/ \___|_| |_|\__,_|\___|_| |_|\__|   \_/ \__,_|_|\__,_|\___||___/
//                                   | |
//
// PROBLEM: Calculate a difference between the highes value and the lowest one
// CONCEPT: ASYNC execution (Could be Dataflow but we'll see it later on)
// EXAMPLE: Getting the difference from the lowest score and the highest
//
@Grab(group='org.gperfutils', module='gbench', version='0.4.2-groovy-2.1')

import static groovyx.gpars.GParsPool.withPool
import static groovyx.gpars.GParsPool.executeAsync

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

// TRANSFORMATIONS
def toInteger = { possibleNumbers -> possibleNumbers*.toInteger() }
def getMax = { numbers -> numbers.max() }
def getMin = { numbers -> numbers.min() }
def sort = { numbers -> numbers.sort() }
def substract = { a, b -> b - a }

// FILTERS
def notZero = { n -> n != 0 }

// SAFETY HELPER
def safely = { closure ->
    return { line -> try { closure(line) } catch(e) { 0 } }
}

// TEMPLATES METHOD
def collectMaximum = { strategy ->
    return NBA_SCORES_FILE.withReader(strategy).max()
}

def collectMinimum = { strategy ->
    return NBA_SCORES_FILE.withReader(strategy).min()
}

// PROBLEM LOGIC
def lowestScore = getMin << toInteger << fields(2,4)
def highestScore = getMax << toInteger << fields(2,4)

def getMaximumScore = {
    return collectMaximum { r -> return r.collect(safely(highestScore)).findAll(notZero) }
}

def getMinimumScore = {
    return collectMinimum { r -> return r.collect(safely(lowestScore)).findAll(notZero) }
}

// BENCHMARK
def sequentialVsAsync = benchmark(warmUpTime: 3) {
    'Synchronous Groovy' {
        Integer result = getMaximumScore() - getMinimumScore()

        assert result == 168
    }
    'Asynchronous Groovy' {
        def result = withPool(4) {
            // BEWARE OF THE ORDER OF EXECUTION
            // Although getMinimumScore is the last declared execution could have ended the first
            return substract << executeAsync(getMaximumScore, getMinimumScore)*.get().sort()
        }

        assert result == 168
    }
} // END OF BENCHMARK


sequentialVsAsync.prettyPrint()
