//    __  ___          __      __           _ __
//   /  |/  /___  ____/ /_  __/ /___ ______(_) /___  __
//  / /|_/ / __ \/ __  / / / / / __ `/ ___/ / __/ / / /
// / /  / / /_/ / /_/ / /_/ / / /_/ / /  / / /_/ /_/ /
///_/  /_/\____/\__,_/\__,_/_/\__,_/_/  /_/\__/\__, /
//                                            /____/
//
// OBJECTIVE: NOT TO GET MAD AT YOUR BOSS
// PROBLEM: MODULARITY
// SAYING: DIVIDE AND CONQUER
//
//
@Grab(group='org.gperfutils', module='gbench', version='0.4.2-groovy-2.1')

class FunctionalSimplicity {

    final File NBA_SCORES_FILE = new File('data/nbascore.csv')
    final String COMMA = ','

    /* --------------------------------------------------- */
    /* ------------- FUNCTIONAL SIMPLICITY --------------- */
    /* --------------------------------------------------- */

    // GENERALIZATION
    Integer extractMaximum(Closure<Integer> collector) {
        return NBA_SCORES_FILE.withReader { reader->
            reader.
                collect { line -> try { collector(line) as Integer } catch (e) { 0 } }.
                max()
        }
    }

    Integer extractMaximumDifference() {
        return extractMaximum { it.split(COMMA)[4, 2]*.toInteger().sort().with { last() - first() } }
    }

    // MAXIMUM DIFFERENCE ON SATURDAYS

    Integer extractMaximumDifferenceOnSaturdays() {

        //F: HAS BEEN PLAYED
        //F: IS SATURDAY
        //C: EXTRACT FIELDS
        //C: GET DIFFERENCE


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




def imperativeVSFunctional = benchmark {
    'Functional Simplicity' {
        new FunctionalSimplicity().with {
            assert extractMaximumDifferenceOnSaturdays()  == 62
        }
    }
}

imperativeVSFunctional.prettyPrint()
