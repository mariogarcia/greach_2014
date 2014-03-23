//
//  _____                _ _      _
// |  __ \              | | |    | |
// | |__) |_ _ _ __ __ _| | | ___| |
// |  ___/ _` | '__/ _` | | |/ _ \ |
// | |  | (_| | | | (_| | | |  __/ |
// |_|___\__,_|_|  \__,_|_|_|\___|_|                    _
// |  __ \                                             (_)
// | |__) | __ ___   __ _ _ __ __ _ _ __ ___  _ __ ___  _ _ __   __ _
// |  ___/ '__/ _ \ / _` | '__/ _` | '_ ` _ \| '_ ` _ \| | '_ \ / _` |
// | |   | | | (_) | (_| | | | (_| | | | | | | | | | | | | | | | (_| |
// |_|   |_|  \___/ \__, |_|  \__,_|_| |_| |_|_| |_| |_|_|_| |_|\__, |
//                   __/ |                                       __/ |
//                  |___/                                       |___/
//
//

@Grab(group='org.gperfutils', module='gbench', version='0.4.2-groovy-2.1')

import static groovyx.gpars.GParsPool.withPool

class FunctionalSimplicity {

    final File NBA_SCORES_FILE = new File('data/nbascore.csv')
    final String COMMA = ','

    /* --------------------------------------------------- */
    /* ------------- FUNCTIONAL SIMPLICITY --------------- */
    /* --------------------------------------------------- */

    // GENERALIZATION
    Integer extractMaximum(Closure<Boolean> filter, Closure<Integer> collector) {
        return NBA_SCORES_FILE.withReader { reader->
            reader.
                findAll(filter).
                collect { line -> try { collector(line) as Integer } catch (e) { 0 } }.
                max()
        }
    }

    Integer extractMaximumDifference() {
        return extractMaximum({it}) { it.split(COMMA)[4, 2]*.toInteger().sort().with { last() - first() } }
    }

    // MAXIMUM DIFFERENCE ON SATURDAYS
    Integer extractMaximumDifferenceOnSaturdays() {
        return extractMaximum(hasBeenPlayedOnSaturday(), differenceBetweenScores())
    }

    // MAXIMUM DIFFERENCE ON SATURDAYS IN PARALLEL
    Integer extractMaximumDifferenceOnSaturdaysInParallel() {
        return extractMaximumInParallel(hasBeenPlayedOnSaturday(), differenceBetweenScores())
    }

    Integer extractMaximumInParallel(Closure<Boolean> filter, Closure<Integer> collector) {
        return withPool(Runtime.runtime.availableProcessors()) {
            NBA_SCORES_FILE.withReader { reader->
                reader.
                    findAllParallel(filter).
                    collectParallel { line -> try { collector(line) as Integer } catch (e) { 0 } }.
                max()
            }
        }
    }

//      _
//     | |
//   __| |_ __ _   _
//  / _` | '__| | | |
// | (_| | |  | |_| |
//  \__,_|_|   \__, |
//              __/ |
//             |___/
//

    Closure<Integer> differenceBetweenScores() {
        return extractIntegerFields([2,4]) >> { first, last -> last - first }
    }

    Closure<Boolean> hasBeenPlayedOnSaturday() {
        return composeFilters(hasBeenPlayed(), isSaturday())
    }

    Closure<Boolean> hasBeenPlayed() {
        return { line -> line.split(COMMA).size() == 5 }
    }

    Closure<Boolean> isSaturday() {
        return { line -> Date.parse('ddMMyyyy', extractFields(0) << line).format('u') == '6'}
    }

    Closure extractFields(indexes) {
        return { idxs, line -> line.split(COMMA)[idxs] }.curry(indexes)
    }

    Closure<List<Integer>> extractIntegerFields(indexes) {
        return extractFields(indexes) >> { values -> values*.toInteger().sort() }
    }

    Closure<Boolean> composeFilters(Closure<Boolean>... filters){
        return { line -> filters*.doCall(line).every {it} }
    }
}

def benchmark =  benchmark {
    'Not parallel' {
        assert new FunctionalSimplicity().extractMaximumDifferenceOnSaturdays() == 62
    }
    'Parallel' {
        assert new FunctionalSimplicity().extractMaximumDifferenceOnSaturdaysInParallel() == 62
    }
}

benchmark.prettyPrint()
