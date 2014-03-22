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

class ImperativeMess {

    final File NBA_SCORES_FILE = new File('data/nbascore.csv')
    final String COMMA = ','

    /* --------------------------------------------------- */
    /* ---------------- IMPERATIVE MESS ------------------ */
    /* --------------------------------------------------- */

    /**
     * BASE CASE
     */
    Integer getMaximumVisitorRegisteredScore() {
        Integer maxVisitorScore = 0
        BufferedReader reader
        try {
            reader = new BufferedReader(new FileReader(NBA_SCORES_FILE))
            while(reader.ready()) {
                try {
                    String csv = reader.readLine()
                    List<String> rowValues = csv.split(COMMA)
                    Integer visitorPoints = Integer.parseInt(rowValues.get(2))
                    if (visitorPoints >= maxVisitorScore) {
                        maxVisitorScore = visitorPoints
                    }
                } catch (e) {
                }
            }
        } catch (io) {
            io.printStackTrace()
            reader.close()
        }

        return maxVisitorScore
    }

    // TELEPHONE RINGING ....

    // WANT TO GET MAXIMUM HOME SCORE ? ...
    // WELL I HAVE TO THINK ABOUT IT BOSS
    // ... IT COULD TAKE A WHILE
    //
    // YEAP
    // I'LL TRY ASAP...c[_]
    // (-.-)Zzz...

    /**
     *  Horrible refactoring
     */
    Integer getMaximumRegisteredScore(Boolean homeOrVisitor) {
        Integer maxVisitorScore = 0
        BufferedReader reader
        try {
            reader = new BufferedReader(new FileReader(NBA_SCORES_FILE))
            while(reader.ready()) {
                try {
                    String csv = reader.readLine()
                    List<String> rowValues = csv.split(COMMA)
                    Integer teamScoreField = homeOrVisitor ? 4 : 2
                    Integer visitorPoints = Integer.parseInt(rowValues.get(teamScoreField))
                    if (visitorPoints >= maxVisitorScore) {
                        maxVisitorScore = visitorPoints
                    }
                } catch (e) {
                }
            }
        } catch (io) {
            reader.close()
        }

        return maxVisitorScore
    }
}

// PHONE RINGING.....
//
//
// HOW TO GET MAXIMUM DIFFERENCE ?
// HOW TO GET MAXIMUM DIFFERENCE ON SATURDAYS ?
// HOW TO GET LAKERS, BULLS... ETC MAXIMUM SCORE ?
// ...
// ... BLABLABLA
// ... AND BY THE WAY IT HAS TO BE DONE...
// ...
// ... IN TWO HOURS
//      ___           ___
//     /   \         /   \
//    |     | _____ |     |
//    |  O  ||     ||  O  |
//    |     ||_____||     |
//    |     |       |     |
//     \___/         \___/
//


class FunctionalSimplicity {

    final File NBA_SCORES_FILE = new File('data/nbascore.csv')
    final String COMMA = ','

    /* --------------------------------------------------- */
    /* ------------- FUNCTIONAL SIMPLICITY --------------- */
    /* --------------------------------------------------- */

    // BASE CASE
    Integer extractVisitorMaximumScore() {
        return NBA_SCORES_FILE.withReader { reader->
            reader.
               collect { line -> try { line.split(COMMA)[2] as Integer } catch (e) { 0 } }.
               max()
        }
    }

    // GENERALIZATION
    Integer extractMaximum(Closure<Integer> criteria) {
        return NBA_SCORES_FILE.withReader { reader->
            reader.
                collect { line -> try { criteria(line) as Integer } catch (e) { 0 } }.
                max()
        }
    }

    Integer extractMaximumDifference() {
        return extractMaximum { it.split(COMMA)[4, 2]*.toInteger().sort().with { last() - first() } }
    }

    //assert extractMaximum { l -> l.split(COMMA)[2] } == 186 // Maximum visitor score
    //assert extractMaximum { l -> l.split(COMMA)[4] } == 184 // Maximum home score

}

def imperativeVSFunctional = benchmark {
    'Imperative Mess' {
        new ImperativeMess().with {
            assert getMaximumVisitorRegisteredScore() == 186
            assert getMaximumRegisteredScore(true) == 184
            assert getMaximumRegisteredScore(false) == 186
        }
    }
    'Functional Simplicity' {
        new FunctionalSimplicity().with {
            assert extractMaximum { l -> l.split(COMMA)[2] } == 186 // Maximum visitor score
            assert extractMaximum { l -> l.split(COMMA)[4] } == 184 // Maximum home score
            assert extractMaximumDifference() == 68
        }
    }
}

imperativeVSFunctional.prettyPrint()
