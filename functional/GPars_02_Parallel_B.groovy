//
// PROBLEM:
// CONCEPT:
// EXAMPLE:
//
@Grab(group='org.gperfutils', module='gbench', version='0.4.2-groovy-2.1')

import static groovyx.gpars.GParsPool.withPool

//      _
//     | |
//   __| |_ __ _   _
//  / _` | '__| | | |
// | (_| | |  | |_| |
//  \__,_|_|   \__, |
//              __/ |
//             |___/
//

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
def toInteger = { possibleNumbers -> possibleNumbers*.toInteger() }
def sort = { numbers -> numbers.sort() }
def substract = { a, b -> b - a }
def safely = { closure ->
    return { line -> try { closure(line) } catch(e) { 0 } }
}

//
//                         _
//                        (_)
//   _____  _____ _ __ ___ _ ___  ___
//  / _ \ \/ / _ \ '__/ __| / __|/ _ \
// |  __/>  <  __/ | | (__| \__ \  __/
//  \___/_/\_\___|_|  \___|_|___/\___|
//
//


def sequentialVsParallel = benchmark(warmUpTime: 10) {
    'Pure functional semantics' {

    }
} // END OF BENCHMARK


sequentialVsParallel.prettyPrint()
