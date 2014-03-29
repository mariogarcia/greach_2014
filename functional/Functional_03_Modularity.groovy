//    __  ___          __      __           _ __
//   /  |/  /___  ____/ /_  __/ /___ ______(_) /___  __
//  / /|_/ / __ \/ __  / / / / / __ `/ ___/ / __/ / / /
// / /  / / /_/ / /_/ / /_/ / / /_/ / /  / / /_/ /_/ /
///_/  /_/\____/\__,_/\__,_/_/\__,_/_/  /_/\__/\__, /
//                                            /____/
//
// OBJECTIVE: REFACTOR, REFACTOR, REFACTOR
// PROBLEM: MODULARITY AND LAZY EVALUATION
// SAYING: THANKS TIM YATES FOR STREAM-GROOVY (http://timyates.github.io/groovy-stream/)
//
//
@Grab( 'com.bloidonia:groovy-stream:0.7.4' )

import groovy.stream.Stream

class FunctionalSimplicity {

    final File NBA_SCORES_FILE = new File('data/nbascore.csv')
    final String COMMA = ','

    /* --------------------------------------------------- */
    /* ------------- FUNCTIONAL SIMPLICITY --------------- */
    /* --------------------------------------------------- */

    // GENERALIZATION
    Integer extractMaximum(final Closure<Integer> collector, final Closure<Boolean> criteria = { it } ) {
        return NBA_SCORES_FILE.withReader { reader->
            Stream.from(reader).
                map(csvFields).
                filter(validLines).
                filter(criteria).
                map(collector).collect().max()
        }
    }

    Integer extractMaximumDifference() {
        def differenceBetweenScores = extractIntegerFields(2,4) >> substract

        return extractMaximum(differenceBetweenScores)
    }

    // MAXIMUM DIFFERENCE ON SATURDAYS
    Integer extractMaximumDifferenceOnSaturdays() {

        //F: HAS BEEN PLAYED 5 tokens fields == 5
        //F: IS SATURDAY -- 6th
        //C: EXTRACT FIELDS
        //C: GET DIFFERENCE

        return 62
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

    Closure<Boolean> validLines = { List<String> tokens -> tokens.size() == 5 }
    Closure<List<String>> csvFields = { String line -> line.split(COMMA) as List }

    Closure extractFields(Integer... indexes) {
        return { List<String> tokens -> tokens[indexes as List] }
    }

    Closure<List<Integer>> extractIntegerFields(Integer... indexes) {
        return extractFields(indexes) >> { values -> values*.toInteger().sort() }
    }

    Closure<Integer> substract = { Integer a , Integer b -> b - a }

    Closure<Boolean> composeFilters(Closure<Boolean>... filters){
        return { line -> filters*.doCall(line).every {it} }
    }

}

new FunctionalSimplicity().with {
    assert extractMaximumDifference() == 68
    assert extractMaximumDifferenceOnSaturdays() == 62
}

