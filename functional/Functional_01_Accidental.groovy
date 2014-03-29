//
//  ____ ___________(_)___/ /__  ____  / /_____ _/ /
// / __ `/ ___/ ___/ / __  / _ \/ __ \/ __/ __ `/ /
/// /_/ / /__/ /__/ / /_/ /  __/ / / / /_/ /_/ / /
//\__,_/\___/\___/_/\__,_/\___/_/ /_/\__/\__,_/_/
//                              __          _ __
//  _________  ____ ___  ____  / /__  _  __(_) /___  __
// / ___/ __ \/ __ `__ \/ __ \/ / _ \| |/_/ / __/ / / /
/// /__/ /_/ / / / / / / /_/ / /  __/>  </ / /_/ /_/ /
//\___/\____/_/ /_/ /_/ .___/_/\___/_/|_/_/\__/\__, /
//                   /_/                      /____/
//
// OBJECTIVE: WANT TO GET THE MAXIMUM VISITOR SCORE OF A NBA GAME OF ALL TIMES
// FROM A CSV FILE
// PROBLEM: ACCIDENTAL COMPLEXITY
// SAYING: NOT SEEING THE FOREST FOR THE TREES

@Grab(group='org.gperfutils', module='gbench', version='0.4.2-groovy-2.1')

final File NBA_SCORES_FILE = new File('data/nbascore.csv')
final String COMMA = ','

def imperativeVSFunctional = benchmark {
    /* --------------------------------------------------- */
    /* ---------------- IMPERATIVE MESS ------------------ */
    /* --------------------------------------------------- */
    'Imperative Mess' {

        Integer maxVisitorScore = 0
        // TOO MUCH THINGS TO DO FOR OPENING A FILE
        BufferedReader reader

        try {

            reader = new BufferedReader(new FileReader(NBA_SCORES_FILE))

            // WHILE READY ???
            while(reader.ready()) {
                try {

                    // DANGEROUS PLACES :S
                    String csv = reader.readLine()
                    List<String> rowValues = csv.split(COMMA)
                    Integer visitorPoints = Integer.parseInt(rowValues.get(2))

                    // IS THIS REALLY NECCESSARY. CAN MESS UP THE COMPARISON
                    if (visitorPoints > maxVisitorScore) {
                        maxVisitorScore = visitorPoints
                    }

                } catch (e) {
                    // BLANK TRY CATCH MMmmm
                }
            }

        } catch (io) {
            reader.close()
        }

        assert maxVisitorScore == 186

        // WHAT IF WE WANT TO GET THE MAXIMUM HOME TEAM SCORE
    }
    'Imperative Groovy' {

        /* --------------------------------------------------- */
        /* -------------- IMPERATIVE GROOVY ------------------ */
        /* --------------------------------------------------- */

        Integer maxVisitorScoreGroovy = 0

        // :) GETTING RID THE WAY WE SHOULD TRAVERSE A FILE
        NBA_SCORES_FILE.splitEachLine(COMMA){ tokens ->
            try {
                def current = tokens[2] as Integer
                if (current > maxVisitorScoreGroovy) {
                    maxVisitorScoreGroovy = current
                }
            } catch (e) {
               // MMmmmm JUST IN CASE CAST EXCEPTION
            }
        }

        assert maxVisitorScoreGroovy == 186

    }
    'Functional Groovy 1' {

        /* --------------------------------------------------- */
        /* -------------- FUNCTIONAL GROOVY ------------------ */
        /* --------------------------------------------------- */

        // NO OUTER VARIABLES
        // EASIER TO GO PARALLEL
        Integer max = NBA_SCORES_FILE.withReader { reader ->
            return reader.inject(0) { max, line ->
                try {
                    return [line.split(COMMA)[2].toInteger(), max].max()
                } catch (e) {
                    return max // NOT THERE YET
                }
            }
        }

        assert max == 186
    }

    'Functional Groovy 2' {
        Integer max = NBA_SCORES_FILE.withReader { reader->
            def visitorScore = { line -> try { line.split(COMMA)[2] as Integer } catch (e) { 0 } }

            // THIS DESCRIBES THE PROBLEM
            // GET ALL VISITOR SCORES AND GET THE MAXIMUM VALUE
            reader.collect(visitorScore).max()
        }
        assert max == 186
    }

} // END OF BENCHMARK


imperativeVSFunctional.prettyPrint()
