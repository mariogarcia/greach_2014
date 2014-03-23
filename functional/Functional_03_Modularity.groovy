//    __  ___          __      __           _ __
//   /  |/  /___  ____/ /_  __/ /___ ______(_) /___  __
//  / /|_/ / __ \/ __  / / / / / __ `/ ___/ / __/ / / /
// / /  / / /_/ / /_/ / /_/ / / /_/ / /  / / /_/ /_/ /
///_/  /_/\____/\__,_/\__,_/_/\__,_/_/  /_/\__/\__, /
//                                            /____/
//
// OBJECTIVE: REFACTOR, REFACTOR, REFACTOR
// PROBLEM: MODULARITY
// SAYING: GREAT MINDS THINK ALIKE
//
//
class FunctionalSimplicity {

    final File NBA_SCORES_FILE = new File('data/nbascore.csv')
    final String COMMA = ','

    /* --------------------------------------------------- */
    /* ------------- FUNCTIONAL SIMPLICITY --------------- */
    /* --------------------------------------------------- */

    // GENERALIZATION
    Integer extractMaximum(Closure<Integer> collector) {
        return NBA_SCORES_FILE.withReader { reader->
            reader.collect { line -> try { collector(line) as Integer } catch (e) { 0 } }.max()
        }
    }

    Integer extractMaximumDifference() {
        return extractMaximum { it.split(COMMA)[4, 2]*.toInteger().sort().with { last() - first() } }
    }

    // MAXIMUM DIFFERENCE ON SATURDAYS
    Integer extractMaximumDifferenceOnSaturdays() {

        //F: HAS BEEN PLAYED 5 tokens fields == 5
        //F: IS SATURDAY -- 6th
        //C: EXTRACT FIELDS
        //C: GET DIFFERENCE

        return 0
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

new FunctionalSimplicity().with {
    assert extractMaximumDifference() == 184
    assert extractMaximumDifferenceOnSaturdays() == 62
}

